package me.comu.render;

import me.comu.Comu;
import me.comu.logging.Logger;
import me.comu.module.impl.render.HUD;
import me.comu.utils.Renderer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Formatting;

public final class HUDRenderer {

    public static void init() {
        Logger.getLogger().print("Initializing HUD Renderer");
        HudRenderCallback.EVENT.register(HUDRenderer::onRender);
    }

    private static void onRender(DrawContext context, RenderTickCounter tickCounter) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.getDebugHud().shouldShowDebugHud()) return;

        HUD hud = (HUD) Comu.getInstance().getModuleManager().getModuleByName("HUD");
        if (hud == null || !hud.isEnabled()) return;

        Renderer.drawTextWithBackground(context, Formatting.RED + Comu.CLIENT_NAME, 4, 4, 0xFFFFFFFF, 0x90000000, true);


        int screenWidth = mc.getWindow().getScaledWidth();
        int y = 4;
        for (var module : Comu.getInstance().getModuleManager().getToggleableModules()) {
            if (module.isEnabled()) {
                String name = Formatting.GRAY + module.getName();
                int textWidth = Renderer.getStringWidth(name);
                Renderer.drawTextWithBackground(context, name, screenWidth - textWidth - 4, y, 0xFFFFFFFF, 0x90000000, true);

                y += Renderer.getFontHeight() + 2;
            }
        }
    }
}
