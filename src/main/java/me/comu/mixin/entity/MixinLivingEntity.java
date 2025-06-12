package me.comu.mixin.entity;

import me.comu.Comu;
import me.comu.events.JumpEvent;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class MixinLivingEntity {

    @Inject(method = "jump", at = @At("HEAD"), cancellable = true)
    private void onJump(CallbackInfo ci) {
        JumpEvent event = new JumpEvent();
        Comu.getInstance().getEventManager().dispatch(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }
}