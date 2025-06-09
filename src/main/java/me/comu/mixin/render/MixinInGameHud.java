package me.comu.mixin.render;

import me.comu.Comu;
import me.comu.events.Render2DEvent;
import me.comu.utils.RenderUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.profiler.Profilers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class MixinInGameHud {
    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        context.draw();
        Profilers.get().push(Comu.getClientName() + "_render_2d_event");
        RenderUtils.unscaledProjection();
        Comu.getInstance().getEventManager().dispatch(new Render2DEvent(context, tickCounter.getTickProgress(true), context.getScaledWindowWidth(), context.getScaledWindowHeight()));
        context.draw();
        RenderUtils.scaledProjection();
        Profilers.get().pop();
    }
}
