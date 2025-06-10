package me.comu.mixin.minecraft;


import me.comu.Comu;
import me.comu.logging.Logger;
import me.comu.module.impl.world.Timer;
import net.minecraft.client.render.RenderTickCounter;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderTickCounter.Dynamic.class)
public abstract class MixinRenderTickCounterDynamic {
    @Shadow
    private float dynamicDeltaTicks;

    @Inject(method = "beginRenderTick(J)I", at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/RenderTickCounter$Dynamic;lastTimeMillis:J", opcode = Opcodes.PUTFIELD))
    private void onBeingRenderTick(long a, CallbackInfoReturnable<Integer> info) {
        Timer timer = Comu.getInstance().getModuleManager().getModule(Timer.class);
        if (timer != null && timer.isEnabled()) {
            float timerSpeed = (float) timer.getPropertyByName("Multiplier").getValue();
            dynamicDeltaTicks *= timerSpeed;
        }
    }
}