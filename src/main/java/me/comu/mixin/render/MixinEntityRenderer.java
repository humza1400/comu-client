package me.comu.mixin.render;

import me.comu.Comu;
import me.comu.logging.Logger;
import me.comu.module.impl.render.Nametags;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer<T extends Entity, S extends EntityRenderState> {
    @Inject(method = "getDisplayName", at = @At("HEAD"), cancellable = true)
    private void overrideNametagRendering(T entity, CallbackInfoReturnable<Text> cir) {
        Nametags nametags = Comu.getInstance().getModuleManager().getModule(Nametags.class);
        if (entity instanceof PlayerEntity && nametags.isEnabled()) {
            cir.setReturnValue(null);
        }
    }
}
