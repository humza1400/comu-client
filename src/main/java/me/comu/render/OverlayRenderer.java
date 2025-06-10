package me.comu.render;

import me.comu.Comu;
import me.comu.logging.Logger;
import me.comu.module.impl.render.HUD;
import me.comu.module.impl.render.TabGui;
import me.comu.module.impl.render.tabgui.TabGuiState;
import me.comu.module.impl.render.tabgui.comu.ComuTabGui;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Formatting;

public final class OverlayRenderer {

    public static void init() {
        Logger.getLogger().print("Initializing HUD Renderer");
        HudRenderCallback.EVENT.register(OverlayRenderer::onRender);
    }

    private static final ComuTabGui renderer = new ComuTabGui();

    private static void onRender(DrawContext context, RenderTickCounter tickCounter) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.getDebugHud().shouldShowDebugHud()) return;

        HUD hud = (HUD) Comu.getInstance().getModuleManager().getModuleByName("HUD");
        if (hud == null || !hud.isEnabled()) return;

        Renderer2D.drawTextWithBackground(context, Formatting.RED + Comu.getClientName() + Formatting.GRAY + " b" + Comu.getClientVersion(), 4, 4, 0xFFFFFFFF, 0x90000000, true);


        int screenWidth = mc.getWindow().getScaledWidth();
        int y = 4;
        for (var module : Comu.getInstance().getModuleManager().getToggleableModules()) {
            if (module.isEnabled()) {
                String name = Formatting.GRAY + module.getDisplayName();
                int textWidth = Renderer2D.getStringWidth(name);
                Renderer2D.drawTextWithBackground(context, name, screenWidth - textWidth - 4, y, 0xFFFFFFFF, 0x90000000, true);

                y += Renderer2D.getFontHeight() + 2;
            }
        }

        TabGui tabGui = (TabGui) Comu.getInstance().getModuleManager().getModuleByName("TabGui");
        if (tabGui != null && tabGui.isEnabled()) {
            renderer.render(context, tabGui.getGuiState(), 3, 17);
        }
    }


}
