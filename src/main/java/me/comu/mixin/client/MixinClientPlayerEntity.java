package me.comu.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.comu.Comu;
import me.comu.events.MotionEvent;
import me.comu.events.MoveEvent;
import me.comu.logging.Logger;
import me.comu.module.ToggleableModule;
import me.comu.module.impl.movement.NoSlowdown;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.Perspective;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class MixinClientPlayerEntity {

    @Shadow private double lastXClient;
    @Shadow private double lastYClient;
    @Shadow private double lastZClient;
    @Shadow private float lastYawClient;
    @Shadow private float lastPitchClient;
    @Shadow private boolean lastOnGround;
    @Shadow private boolean autoJumpEnabled;
    @Shadow private boolean lastHorizontalCollision;
    @Shadow private int ticksSinceLastPositionPacketSent;
    @Shadow private net.minecraft.client.MinecraftClient client;

    @Shadow protected abstract boolean isCamera();
    @Shadow protected abstract void sendSprintingPacket();


    @Inject(method = "sendMovementPackets", at = @At("HEAD"), cancellable = true)
    private void sendMovementPackets(CallbackInfo ci) {
        ci.cancel();

        MinecraftClient mc = MinecraftClient.getInstance();
        ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;
        MotionEvent motionEvent = new MotionEvent(MotionEvent.Phase.PRE, player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch(), player.isOnGround());
        motionEvent.dispatch();

        if (motionEvent.isCancelled())
            return;

        // TODO: Move to render event
        if (mc.options.getPerspective() == Perspective.THIRD_PERSON_FRONT || mc.options.getPerspective() == Perspective.THIRD_PERSON_BACK) {
            mc.player.headYaw = motionEvent.getYaw();
            mc.player.bodyYaw = motionEvent.getYaw();
            mc.player.setPitch(motionEvent.getPitch());
        }

        if (this.isCamera()) {
            this.sendSprintingPacket();
            double d = motionEvent.getX() - this.lastXClient;
            double e = motionEvent.getY() - this.lastYClient;
            double f = motionEvent.getZ() - this.lastZClient;
            double g = motionEvent.getYaw() - this.lastYawClient;
            double h = motionEvent.getPitch() - this.lastPitchClient;
            this.ticksSinceLastPositionPacketSent++;

            boolean bl = MathHelper.squaredMagnitude(d, e, f) > MathHelper.square(2.0E-4) || this.ticksSinceLastPositionPacketSent >= 20;
            boolean bl2 = g != 0.0 || h != 0.0;

            if (bl && bl2) {
                player.networkHandler.sendPacket(new PlayerMoveC2SPacket.Full(motionEvent.getPos(), motionEvent.getYaw(), motionEvent.getPitch(), motionEvent.isOnGround(), player.horizontalCollision));
            } else if (bl) {
                player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(motionEvent.getPos(), motionEvent.isOnGround(), player.horizontalCollision));
            } else if (bl2) {
                player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(motionEvent.getYaw(), motionEvent.getPitch(), motionEvent.isOnGround(), player.horizontalCollision));
            } else if (this.lastOnGround != motionEvent.isOnGround() || this.lastHorizontalCollision != player.horizontalCollision) {
                player.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(motionEvent.isOnGround(), player.horizontalCollision));
            }

            if (bl) {
                this.lastXClient = motionEvent.getX();
                this.lastYClient = motionEvent.getY();
                this.lastZClient = motionEvent.getZ();
                this.ticksSinceLastPositionPacketSent = 0;
            }

            if (bl2) {
                this.lastYawClient = motionEvent.getYaw();
                this.lastPitchClient = motionEvent.getPitch();
            }

            this.lastOnGround = motionEvent.isOnGround();
            this.lastHorizontalCollision = player.horizontalCollision;
            this.autoJumpEnabled = this.client.options.getAutoJump().getValue();
        }

        motionEvent.setPhase(MotionEvent.Phase.POST).dispatch();
    }

    @Inject(method = "tickMovement", at = @At("HEAD"))
    private void onTickMovement(CallbackInfo ci) {
        Entity self = (Entity) (Object) this;
        MoveEvent event = new MoveEvent(self.getVelocity());
        Comu.getInstance().getEventManager().dispatch(event);

        if (event.isCancelled()) {
            self.setVelocity(Vec3d.ZERO);
        } else {
            self.setVelocity(event.getMovement());
        }
    }

    @ModifyExpressionValue(method = "applyMovementSpeedFactors", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"))
    private boolean redirectUsingItem(boolean isUsingItem) {
        NoSlowdown noSlow = Comu.getInstance().getModuleManager().getModule(NoSlowdown.class);
        if (noSlow.isEnabled()) return false;
        return isUsingItem;
    }
}
