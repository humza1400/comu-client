package me.comu.render;

import me.comu.Comu;
import me.comu.logging.Logger;
import me.comu.module.impl.render.HUD;
import me.comu.module.impl.render.TabGui;
import me.comu.module.impl.render.tabgui.comu.ComuTabGui;
import me.comu.utils.ClientUtils;
import me.comu.utils.RenderUtils;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;


public final class HUDRenderer {

    public static void init() {
        Logger.getLogger().print("Initializing HUD Renderer");
        HudRenderCallback.EVENT.register(HUDRenderer::onRender);
    }

    private static final ComuTabGui renderer = new ComuTabGui();

    private static void onRender(DrawContext context, RenderTickCounter tickCounter) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.getDebugHud().shouldShowDebugHud()) return;

        HUD hud = (HUD) Comu.getInstance().getModuleManager().getModuleByName("HUD");
        TabGui tabGui = (TabGui) Comu.getInstance().getModuleManager().getModuleByName("TabGui");

        if (hud == null || !hud.isEnabled()) return;

        int screenWidth = mc.getWindow().getScaledWidth();
        int screenHeight = mc.getWindow().getScaledHeight();

        if (hud.getWatermark().getValue()) {
            Renderer2D.drawText(context, Formatting.RED + Comu.getClientName() + Formatting.GRAY + " b" + Comu.getClientVersion(), 4, 4, 0xFFFFFFFF, true);
        }
        if (tabGui != null && tabGui.isEnabled()) {
            renderer.render(context, tabGui.getGuiState(), 3, hud.getWatermark().getValue() ? 15 : 4);
        }

        if (hud.getArrayList().getValue()) {
            int y = 4;
            for (var module : Comu.getInstance().getModuleManager().getToggleableModules()) {
                if (module.isEnabled()) {
                    String name = Formatting.GRAY + module.getDisplayName();
                    int textWidth = Renderer2D.getStringWidth(name);
                    Renderer2D.drawTextWithBackground(context, name, screenWidth - textWidth - 4, y, 0xFFFFFFFF, 0x90000000, true);

                    y += Renderer2D.getFontHeight() + 2;
                }
            }
        }

        int yOffset = screenHeight - 2;
        if (hud.getPotions().getValue()) yOffset = drawPotions(context, screenWidth, yOffset);
        if (hud.getCoords().getValue()) yOffset = drawCoords(context, screenWidth, yOffset);
        if (hud.getClock().getValue()) yOffset = drawClock(context, screenWidth, yOffset);
        if (hud.getFps().getValue()) yOffset = drawFPS(context, screenWidth, yOffset);
        if (hud.getPing().getValue()) yOffset = drawPing(context, screenWidth, yOffset);
        if (hud.getDirection().getValue()) drawDirection(context, screenWidth, yOffset);

        if (hud.getGapple().getValue()) drawGappled(context, screenWidth, screenHeight);
        if (hud.getArmor().getValue()) drawArmor(context, screenWidth, screenHeight);
    }

    private static int drawPotions(DrawContext context, int screenWidth, int yOffset) {
        MinecraftClient mc = MinecraftClient.getInstance();
        for (var effect : mc.player.getStatusEffects()) {
            var type = effect.getEffectType();
            var color = type.value().getColor();
            String name = type.value().getName().getString();
            int level = effect.getAmplifier() + 1;
            int durationTicks = effect.getDuration();
            int durationSeconds = durationTicks / 20;
            String durationStr = String.format("%d:%02d", durationSeconds / 60, durationSeconds % 60);
            String display = name + " " + level + " " + Formatting.GRAY + durationStr;

            int textWidth = Renderer2D.getStringWidth(display);
            yOffset -= Renderer2D.getFontHeight();
            Renderer2D.drawText(context, display, screenWidth - textWidth - 4, yOffset, 0xFF000000 | color, true);
            yOffset -= 1;
        }
        return yOffset;
    }

    private static int drawCoords(DrawContext context, int screenWidth, int yOffset) {
        MinecraftClient mc = MinecraftClient.getInstance();
        String coords = String.format(Formatting.WHITE + "%d, %d, %d" + Formatting.GRAY + " XYZ", (int) mc.player.getX(), (int) mc.player.getY(), (int) mc.player.getZ());
        int textWidth = Renderer2D.getStringWidth(coords);
        yOffset -= Renderer2D.getFontHeight();
        Renderer2D.drawText(context, coords, screenWidth - textWidth - 4, yOffset, 0xFFFFFFFF, true);
        return yOffset - 1;
    }

    private static int drawClock(DrawContext context, int screenWidth, int yOffset) {
        String clock = Formatting.GRAY + java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("h:mm a"));
        int textWidth = Renderer2D.getStringWidth(clock);
        yOffset -= Renderer2D.getFontHeight();
        Renderer2D.drawText(context, clock, screenWidth - textWidth - 4, yOffset, 0xFFFFFFFF, true);
        return yOffset - 1;
    }

    private static int drawFPS(DrawContext context, int screenWidth, int yOffset) {
        MinecraftClient mc = MinecraftClient.getInstance();
        String fpsStr = Formatting.GRAY + "" + mc.getCurrentFps() + " FPS";
        int textWidth = Renderer2D.getStringWidth(fpsStr);
        yOffset -= Renderer2D.getFontHeight();
        Renderer2D.drawText(context, fpsStr, screenWidth - textWidth - 4, yOffset, 0xFFFFFFFF, true);
        return yOffset - 1;
    }

    private static int drawPing(DrawContext context, int screenWidth, int yOffset) {
        MinecraftClient mc = MinecraftClient.getInstance();
        int ping = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid()) != null ? mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid()).getLatency() : -1;
        String pingStr = Formatting.GRAY + "" + ping + "ms";
        int textWidth = Renderer2D.getStringWidth(pingStr);
        yOffset -= Renderer2D.getFontHeight();
        Renderer2D.drawText(context, pingStr, screenWidth - textWidth - 4, yOffset, 0xFFFFFFFF, true);
        return yOffset - 1;
    }

    private static void drawDirection(DrawContext context, int screenWidth, int yOffset) {
        MinecraftClient mc = MinecraftClient.getInstance();
        String[] directions = {"South", "West", "North", "East"};
        int facing = Math.round(mc.player.getYaw() / 90f) & 3;
        String direction = Formatting.GRAY + directions[facing];
        int textWidth = Renderer2D.getStringWidth(direction);
        yOffset -= Renderer2D.getFontHeight();
        Renderer2D.drawText(context, direction, screenWidth - textWidth - 4, yOffset, 0xFFFFFFFF, true);
    }

    private static void drawGappled(DrawContext context, int screenWidth, int screenHeight) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;

        boolean isGappled = false;
        int durationSeconds = 0;

        for (var effect : mc.player.getStatusEffects()) {
            if (effect.getEffectType() == StatusEffects.REGENERATION) {
                isGappled = true;
                durationSeconds = effect.getDuration() / 20;
                break;
            }
        }

        String text = isGappled
                ? Formatting.GREEN + "Gappled " + Formatting.GRAY + "(" + durationSeconds + ")"
                : Formatting.RED.toString() + Formatting.UNDERLINE + "NOT Gappled";

        int textWidth = Renderer2D.getStringWidth(text);

        int x = screenWidth / 2 + textWidth - 60;
        int y = screenHeight / 2 + 12;

        Renderer2D.drawText(context, text, x, y, 0xFFFFFFFF, true);
    }

    private static void drawArmor(DrawContext context, int screenWidth, int screenHeight) {
        MinecraftClient mc = MinecraftClient.getInstance();
        List<ItemStack> items = new ArrayList<>();

        for (int i = 1; i <= 4; i++) {
            ItemStack stack = ClientUtils.getEquipmentItem(mc.player, i);
            if (!stack.isEmpty()) items.add(stack);
        }

        if (items.isEmpty()) return;

        final int iconSize = 16;
        final int iconSpacing = 18;

        int startX = screenWidth / 2 + 93;
        int yOffset = screenHeight - 18;

        for (int i = 0; i < items.size(); i++) {
            ItemStack stack = items.get(i);
            int x = startX + i * iconSpacing;

            RenderUtils.drawItem(context, stack, x, yOffset, 1f, true);

            if (stack.isDamageable()) {
                context.getMatrices().push();

                float scaleFactor = 0.7f;

                int durability = stack.getMaxDamage() - stack.getDamage();
                String durabilityText = String.valueOf(durability);
                int textWidth = mc.textRenderer.getWidth(durabilityText);
                int textHeight = mc.textRenderer.fontHeight;

                float unscaledX = x + iconSize - textWidth * scaleFactor - 1;
                float unscaledY = yOffset + iconSize - textHeight * scaleFactor - 8.5f;

                context.getMatrices().translate(unscaledX, unscaledY, 9999);
                context.getMatrices().scale(scaleFactor, scaleFactor, 1f);
                context.drawText(mc.textRenderer, durabilityText, 0, 0, 0xFFAA00, true);
                context.getMatrices().pop();
            }
        }
    }
}
