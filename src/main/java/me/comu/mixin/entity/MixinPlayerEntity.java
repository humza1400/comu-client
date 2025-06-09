package me.comu.mixin.entity;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.comu.Comu;
import me.comu.events.SprintAttackEvent;
import me.comu.module.impl.movement.NoSlowdown;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity {

    @Shadow
    public abstract PlayerAbilities getAbilities();

    @ModifyReturnValue(method = "getMovementSpeed", at = @At("RETURN"))
    private float getMovementSpeed(float original) {
        NoSlowdown noSlow = Comu.getInstance().getModuleManager().getModule(NoSlowdown.class);
        if (noSlow != null && noSlow.isEnabled()) {
            float walkSpeed = getAbilities().getWalkSpeed();

            if (original < walkSpeed) return walkSpeed;
        }
        return original;
    }

    @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V"), cancellable = true)
    private void attack(Entity target, CallbackInfo ci) {
        SprintAttackEvent event = new SprintAttackEvent(target);
        Comu.getInstance().getEventManager().dispatch(event);

        if (event.isCancelled()) {
            ci.cancel();
        }
    }
}
