package me.comu.mixin.render;

import com.llamalad7.mixinextras.sugar.Local;
import me.comu.Comu;
import me.comu.events.Render3DEvent;
import me.comu.logging.Logger;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer {

    @Shadow
    @Final
    private Camera camera;

    @Inject(method = "renderWorld", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", args = "ldc=hand"))
    private void onRenderWorld(RenderTickCounter tickCounter, CallbackInfo ci, @Local MatrixStack matrices, @Local(ordinal = 1) float tickDelta) {
        Render3DEvent render3DEvent = new Render3DEvent(matrices, tickDelta, camera.getPos().x, camera.getPos().y, camera.getPos().z);
        Comu.getInstance().getEventManager().dispatch(render3DEvent);
    }
}
