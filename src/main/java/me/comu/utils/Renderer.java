package me.comu.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

public final class Renderer {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private static final TextRenderer textRenderer = mc.textRenderer;

    public static void drawText(DrawContext context, String message, int x, int y, int color, boolean shadow) {
        context.drawText(textRenderer, message, x, y, color, shadow);
    }

    public static void drawTextWithBackground(DrawContext context, String message, int x, int y, int textColor, int backgroundColor, boolean shadow) {
        int width = getStringWidth(message);
        int height = getFontHeight();

        context.fill(x - 2, y - 1, x + width + 2, y + height + 1, backgroundColor);

        drawText(context, message, x, y, textColor, shadow);
    }

    public static int getStringWidth(String text) {
        return textRenderer.getWidth(text);
    }

    public static int getFontHeight() {
        return textRenderer.fontHeight;
    }

    private Renderer() {}
}
