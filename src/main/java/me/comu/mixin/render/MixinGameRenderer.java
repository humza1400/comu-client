package me.comu.mixin.render;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import me.comu.Comu;
import me.comu.events.Render3DEvent;
import me.comu.hooks.Hook;
import me.comu.mixin.render.accessor.CameraAccessor;
import me.comu.module.impl.active.Overlay;
import me.comu.property.properties.ListProperty;
import me.comu.render.Renderer3D;
import me.comu.utils.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.profiler.Profilers;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("unchecked")
@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer {

    @Shadow
    @Final
    private MinecraftClient client;

    @Unique
    private Renderer3D renderer;

    @Unique
    private Renderer3D depthRenderer;

    @Unique
    private final MatrixStack matrices = new MatrixStack();

    @Shadow
    @Final
    private BufferBuilderStorage buffers;

    @Shadow
    @Final
    private Camera camera;

    @Shadow
    protected abstract void bobView(MatrixStack matrices, float tickDelta);

    @Shadow
    protected abstract void tiltViewWhenHurt(MatrixStack matrices, float tickDelta);

    private static float currentFovMultiplier = 1.0f;


    @Inject(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;render(Lnet/minecraft/client/util/ObjectAllocator;Lnet/minecraft/client/render/RenderTickCounter;ZLnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/GameRenderer;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;)V", shift = At.Shift.AFTER))
    private void onRenderWorld(RenderTickCounter tickCounter, CallbackInfo ci) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null || mc.player == null) return;

        VertexConsumerProvider.Immediate consumers = mc.getBufferBuilders().getEntityVertexConsumers();
        MatrixStack matrixStack = new MatrixStack();

        Render3DEvent event = new Render3DEvent(matrixStack, consumers, tickCounter.getTickProgress(true), camera.getPos().x, camera.getPos().y, camera.getPos().z);
        Comu.getInstance().getEventManager().dispatch(event);

        consumers.draw();
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
