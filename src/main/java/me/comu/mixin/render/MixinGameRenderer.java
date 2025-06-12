package me.comu.mixin.render;

import com.llamalad7.mixinextras.sugar.Local;
import me.comu.Comu;
import me.comu.events.Render3DEvent;
import me.comu.hooks.Hook;
import me.comu.module.impl.active.Overlay;
import me.comu.property.properties.ListProperty;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("unchecked")
@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer {

    @Shadow
    @Final
    private Camera camera;

    private static float currentFovMultiplier = 1.0f;


    @Inject(method = "renderWorld", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", args = "ldc=hand"))
    private void onRenderWorld(RenderTickCounter tickCounter, CallbackInfo ci, @Local MatrixStack matrices, @Local(ordinal = 1) float tickDelta) {
        Render3DEvent render3DEvent = new Render3DEvent(matrices, tickDelta, camera.getPos().x, camera.getPos().y, camera.getPos().z);
        Comu.getInstance().getEventManager().dispatch(render3DEvent);
    }

    @Inject(method = "getFov(Lnet/minecraft/client/render/Camera;FZ)F", at = @At("RETURN"), cancellable = true)
    private void onGetFovReturn(Camera camera, float tickProgress, boolean changingFov, CallbackInfoReturnable<Float> cir) {
        Overlay overlay = Comu.getInstance().getModuleManager().getModule(Overlay.class);
        boolean isZooming = Hook.getZoomKeybind().isPressed();
        ListProperty zoomProperty = (ListProperty) overlay.getPropertyByName("Zoom");

        float zoomFactor = (float) zoomProperty.getPropertyByName("zoom factor").getValue();
        float smoothFactor = (float) zoomProperty.getPropertyByName("smooth factor").getValue();

        float target = isZooming ? zoomFactor : 1.0f;
        currentFovMultiplier += (target - currentFovMultiplier) * smoothFactor;

        float baseFov = cir.getReturnValueF();
        cir.setReturnValue(baseFov * currentFovMultiplier);
    }
}
