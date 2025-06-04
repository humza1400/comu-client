package me.comu.mixin.entity;

import me.comu.Comu;
import me.comu.events.MotionEvent;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class MixinClientPlayerEntity {

    @Inject(method = "tickMovement", at = @At("HEAD"), cancellable = true)
    private void onPreTickMovement(CallbackInfo ci) {
        MotionEvent event = new MotionEvent(MotionEvent.Phase.PRE);
        Comu.getInstance().getEventManager().dispatch(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "tickMovement", at = @At("TAIL"))
    private void onPostTickMovement(CallbackInfo ci) {
        Comu.getInstance().getEventManager().dispatch(new MotionEvent(MotionEvent.Phase.POST));
    }
}
