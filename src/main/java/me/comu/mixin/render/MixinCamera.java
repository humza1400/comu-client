package me.comu.mixin.render;

import me.comu.Comu;
import me.comu.module.impl.active.Overlay;
import me.comu.overrides.EntityOverride;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class MixinCamera {

    @Unique
    private boolean firstTime = true;

    @Shadow
    protected abstract void setRotation(float yaw, float pitch);

    @Inject(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setRotation(FF)V", ordinal = 1, shift = At.Shift.AFTER))
    private void overrideCameraRotation(BlockView blockView, Entity entity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
        Overlay overlay = Comu.getInstance().getModuleManager().getModule(Overlay.class);
        if (entity instanceof ClientPlayerEntity player && overlay.isFreelookActive()) {
            EntityOverride freelook = (EntityOverride) player;

            if (firstTime && MinecraftClient.getInstance().player != null) {
                freelook.comu$setCameraYaw(MinecraftClient.getInstance().player.getYaw());
                freelook.comu$setCameraPitch(MinecraftClient.getInstance().player.getPitch());
                firstTime = false;
            }
            this.setRotation(freelook.comu$getCameraYaw(), freelook.comu$getCameraPitch());
        } else {
            firstTime = true;
        }
    }
}
