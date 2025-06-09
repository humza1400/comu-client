package me.comu.mixin.render.accessor;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GameRenderer.class)
public interface GameRendererAccessor {
    @Invoker("getFov")
    float callGetFov(Camera camera, float tickDelta, boolean changing);

    @Invoker("getBasicProjectionMatrix")
    Matrix4f callGetBasicProjectionMatrix(float fovDegrees);
}