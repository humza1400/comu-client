package me.comu.mixin.render;

import me.comu.Comu;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingEntityRenderer<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> {
    @Inject(method = "updateRenderState", at = @At("TAIL"))
    private void onUpdateRenderState(T entity, S state, float tickDelta, CallbackInfo ci) {
        MinecraftClient mc = MinecraftClient.getInstance();
        boolean isThirdPerson = mc.options.getPerspective() == Perspective.THIRD_PERSON_FRONT || mc.options.getPerspective() == Perspective.THIRD_PERSON_BACK;
        boolean isInBlockingGui = mc.currentScreen instanceof InventoryScreen;

        if (entity == mc.player && isThirdPerson && !isInBlockingGui) {
            var rotationManager = Comu.getInstance().getRotationManager();

            if (rotationManager.isRotating()) {
                state.bodyYaw = rotationManager.getYaw();
                state.relativeHeadYaw = rotationManager.getYaw() - state.bodyYaw;
                state.pitch = rotationManager.getPitch();
            }
        }
    }
}
