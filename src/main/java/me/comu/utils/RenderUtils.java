package me.comu.utils;

import com.mojang.blaze3d.systems.ProjectionType;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.awt.*;

public class RenderUtils {

    private static MinecraftClient mc = MinecraftClient.getInstance();


    public static void unscaledProjection() {
        RenderSystem.setProjectionMatrix(new Matrix4f().setOrtho(0, mc.getWindow().getFramebufferWidth(), mc.getWindow().getFramebufferHeight(), 0, 1000, 21000), ProjectionType.ORTHOGRAPHIC);
    }

    public static void scaledProjection() {
        RenderSystem.setProjectionMatrix(new Matrix4f().setOrtho(0, (float) (mc.getWindow().getFramebufferWidth() / mc.getWindow().getScaleFactor()), (float) (mc.getWindow().getFramebufferHeight() / mc.getWindow().getScaleFactor()), 0, 1000, 21000), ProjectionType.PERSPECTIVE);
    }

    public static void drawItem(DrawContext drawContext, ItemStack itemStack, int x, int y, float scale, boolean overlay, String countOverride) {
        MatrixStack matrices = drawContext.getMatrices();
        matrices.push();
        matrices.scale(scale, scale, 1f);
        matrices.translate(0, 0, 401);

        int scaledX = (int) (x / scale);
        int scaledY = (int) (y / scale);

        drawContext.drawItem(itemStack, scaledX, scaledY);
        if (overlay) drawContext.drawStackOverlay(mc.textRenderer, itemStack, scaledX, scaledY, countOverride);

        matrices.pop();
    }

    public static void drawItem(DrawContext drawContext, ItemStack itemStack, int x, int y, float scale, boolean overlay) {
        drawItem(drawContext, itemStack, x, y, scale, overlay, null);
    }

    public static void drawGradientBorder(DrawContext context, int x1, int y1, int x2, int y2, int startColor, int endColor) {
        context.fillGradient(x1, y1, x2, y1 + 1, startColor, endColor);
        context.fillGradient(x1, y2 - 1, x2, y2, startColor, endColor);
        context.fillGradient(x1, y1, x1 + 1, y2, startColor, endColor);
        context.fillGradient(x2 - 1, y1, x2, y2, startColor, endColor);
    }

    public static void drawBlockOverlay(DrawContext context, BlockPos blockPos, float tickDelta) {
        MinecraftClient mc = MinecraftClient.getInstance();
        Camera camera = mc.gameRenderer.getCamera();
        Vec3d camPos = camera.getPos();

        float hue = (System.currentTimeMillis() % 2000L) / 2000.0f;
        int rgb = Color.HSBtoRGB(hue, 1.0f, 1.0f);
        float r = ((rgb >> 16) & 0xFF) / 255f;
        float g = ((rgb >> 8) & 0xFF) / 255f;
        float b = (rgb & 0xFF) / 255f;
        float alpha = 0.3f;

        Box box = new Box(blockPos).offset(-camPos.x, -camPos.y, -camPos.z);

        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        VertexConsumer vertices = immediate.getBuffer(RenderLayer.getLines());

        drawBoxOutline(vertices, box, r, g, b, alpha);

        immediate.draw();
    }

    public static void drawBoxOutline(VertexConsumer buffer, Box box, float r, float g, float b, float alpha) {
        drawLine(buffer, box.minX, box.minY, box.minZ, box.maxX, box.minY, box.minZ, r, g, b, alpha);
        drawLine(buffer, box.minX, box.minY, box.minZ, box.minX, box.maxY, box.minZ, r, g, b, alpha);
        drawLine(buffer, box.minX, box.minY, box.minZ, box.minX, box.minY, box.maxZ, r, g, b, alpha);
        drawLine(buffer, box.maxX, box.maxY, box.maxZ, box.minX, box.maxY, box.maxZ, r, g, b, alpha);
        drawLine(buffer, box.maxX, box.maxY, box.maxZ, box.maxX, box.minY, box.maxZ, r, g, b, alpha);
        drawLine(buffer, box.maxX, box.maxY, box.maxZ, box.maxX, box.maxY, box.minZ, r, g, b, alpha);

        drawLine(buffer, box.maxX, box.minY, box.minZ, box.maxX, box.maxY, box.minZ, r, g, b, alpha);
        drawLine(buffer, box.maxX, box.minY, box.minZ, box.maxX, box.minY, box.maxZ, r, g, b, alpha);
        drawLine(buffer, box.minX, box.maxY, box.maxZ, box.minX, box.minY, box.maxZ, r, g, b, alpha);
        drawLine(buffer, box.minX, box.maxY, box.maxZ, box.minX, box.maxY, box.minZ, r, g, b, alpha);
        drawLine(buffer, box.minX, box.minY, box.maxZ, box.maxX, box.minY, box.maxZ, r, g, b, alpha);
        drawLine(buffer, box.minX, box.maxY, box.minZ, box.maxX, box.maxY, box.minZ, r, g, b, alpha);
    }

    public static void drawLine(VertexConsumer buffer, double x1, double y1, double z1, double x2, double y2, double z2, float r, float g, float b, float a) {
        buffer.vertex((float) x1, (float) y1, (float) z1).color(r, g, b, a).normal(0, 1, 0);
        buffer.vertex((float) x2, (float) y2, (float) z2).color(r, g, b, a).normal(0, 1, 0);
    }

    public static void drawGradientRect(DrawContext context, int x, int y, int width, int height, int topColor, int bottomColor) {
        for (int i = 0; i < height; i++) {
            float ratio = (float) i / height;
            int r = (int) MathHelper.lerp(ratio, (topColor >> 16) & 0xFF, (bottomColor >> 16) & 0xFF);
            int g = (int) MathHelper.lerp(ratio, (topColor >> 8) & 0xFF, (bottomColor >> 8) & 0xFF);
            int b = (int) MathHelper.lerp(ratio, topColor & 0xFF, bottomColor & 0xFF);
            int color = (255 << 24) | (r << 16) | (g << 8) | b;
            context.fill(x, y + i, x + width, y + i + 1, color);
        }
    }

    public static void drawHollowRect(DrawContext context, int x, int y, int width, int height, int color) {
        context.fill(x, y, x + width, y + 1, color);
        context.fill(x, y + height - 1, x + width, y + height, color);
        context.fill(x, y, x + 1, y + height, color);
        context.fill(x + width - 1, y, x + width, y + height, color);
    }

    public static void drawEnhancedText(DrawContext context, TextRenderer textRenderer, Text text, int x, int y, float hoverProgress, float animationTime) {
        String textString = text.getString();

        int shadowColor = (int) (100 + hoverProgress * 50) << 24;
        context.drawText(textRenderer, textString, x - textRenderer.getWidth(textString) / 2 + 1, y + 1, shadowColor, false);

        float textWave = (float) Math.sin(animationTime * 1.5) * 0.1f + 0.9f;
        int textR = (int) (255 * textWave);
        int textG = (int) (255 * (0.9f + hoverProgress * 0.1f));
        int textB = (int) (200 + hoverProgress * 55);
        int textColor = (255 << 24) | (textR << 16) | (textG << 8) | textB;

        float textOffset = (float) Math.sin(animationTime * 2) * hoverProgress * 0.5f;
        context.drawText(textRenderer, textString, x - textRenderer.getWidth(textString) / 2, (int) (y + textOffset), textColor, false);

        if (hoverProgress > 0.5f) {
            int glowAlpha = (int) ((hoverProgress - 0.5f) * 60);
            int glowColor = (glowAlpha << 24) | 0xFFFFFF;
            context.drawText(textRenderer, textString, x - textRenderer.getWidth(textString) / 2, y, glowColor, false);
        }
    }
}
