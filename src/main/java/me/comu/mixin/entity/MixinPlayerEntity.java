package me.comu.mixin.entity;

import me.comu.Comu;
import me.comu.events.SprintAttackEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class MixinPlayerEntity {

    @Inject(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V"
            ),
            cancellable = true
    )
    private void attack(Entity target, CallbackInfo ci) {
        SprintAttackEvent event = new SprintAttackEvent(target);
        Comu.getInstance().getEventManager().dispatch(event);

        if (event.isCancelled()) {
            ci.cancel();
        }
    }
    }
