package me.comu.module.impl.render.clickgui.comu.properties;

import me.comu.property.properties.InputProperty;
import me.comu.render.Renderer2D;
import me.comu.utils.ClientUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.glfw.GLFW;

public class StringInput {
    private final InputProperty property;
    private final int x, y, width, height;
    private boolean focused;

    private int cursorIndex = 0;
    private int scrollOffset = 0;
    private boolean selectedAll = false;
    private long lastCursorBlink = System.currentTimeMillis();
    private boolean cursorVisible = true;


    public StringInput(InputProperty property, int x, int y, int width, int height) {
        this.property = property;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.focused = false;
    }

    public void draw(DrawContext ctx, int px, int py, int mx, int my, int top, int bot) {
        int drawX = px + x, drawY = py + y;
        if (drawY + height < top || drawY > bot) return;

        int bgColor = focused ? 0xFF2A2A3A : 0xFF1E1E1E;
        int borderColor = 0xFF707070;
        ctx.fill(drawX, drawY, drawX + width, drawY + height, bgColor);
        ctx.fill(drawX, drawY, drawX + width, drawY + 1, borderColor);
        ctx.fill(drawX, drawY + height - 1, drawX + width, drawY + height, borderColor);
        ctx.fill(drawX, drawY, drawX + 1, drawY + height, borderColor);
        ctx.fill(drawX + width - 1, drawY, drawX + width, drawY + height, borderColor);

        MatrixStack ms = ctx.getMatrices();
        ms.push();
        ms.translate(drawX, drawY - 10, 0);
        ms.scale(0.8f, 0.8f, 1f);
        Renderer2D.drawText(ctx, property.getName(), 0, 0, 0xFFFFFFFF, true);
        ms.pop();

        String fullRawText = property.getValue();
        String displayText = fullRawText;
        int textX = drawX + 2;
        int textY = drawY + (height - Renderer2D.getFontHeight()) / 2 + 2;
        int innerWidth = width - 8;

        ms.push();
        ms.translate(textX, textY, 0);
        ms.scale(0.8f, 0.8f, 1f);

        int gray = 0xFF888888;
        int white = 0xFFFFFFFF;
        int offsetX = 0;

        if (focused) {
            int cursorPixelX = Renderer2D.getStringWidth(displayText.substring(0, Math.min(cursorIndex, displayText.length())));
            if (cursorPixelX - scrollOffset > innerWidth) scrollOffset = cursorPixelX - innerWidth;
            else if (cursorPixelX - scrollOffset < 0) scrollOffset = cursorPixelX;

            int skipChars = 0, widthSoFar = 0;
            while (skipChars < displayText.length()) {
                char c = displayText.charAt(skipChars);
                int w = Renderer2D.getCharWidth(c);
                if (widthSoFar + w > scrollOffset) break;
                widthSoFar += w;
                skipChars++;
            }

            String visibleText = ClientUtils.trimTextToFit(displayText.substring(skipChars), innerWidth);
            boolean trimmedLeft = skipChars > 0;

            if (trimmedLeft) {
                Renderer2D.drawText(ctx, "..", offsetX, 0, white, false);
                offsetX += Renderer2D.getStringWidth("..");
            }

            if (selectedAll && !displayText.isEmpty()) {
                int textW = Renderer2D.getStringWidth(visibleText);
                ctx.fill(offsetX, 0, offsetX + textW, Renderer2D.getFontHeight(), 0x80FFAACC);
            }

            Renderer2D.drawText(ctx, visibleText, offsetX, 0, white, false);

            if (cursorVisible) {
                int visibleIndex = Math.max(0, Math.min(visibleText.length(), cursorIndex - skipChars));
                int cursorOffsetX = offsetX + Renderer2D.getStringWidth(visibleText.substring(0, visibleIndex));
                ctx.fill(cursorOffsetX, 0, cursorOffsetX + 1, Renderer2D.getFontHeight(), white);
            }

        } else {

            String formatted = property.getFormattedValue().replace("\"", "");
            int quoteW = Renderer2D.getCharWidth('"');
            int dotsW = Renderer2D.getStringWidth("..");
            int buffer = Renderer2D.getCharWidth('A') * 1;
            int spaceForText = innerWidth - quoteW * 2 + buffer;

            String trimmed = ClientUtils.trimTextToFit(formatted, spaceForText);
            boolean scissored = trimmed.length() < formatted.length();

            if (scissored) {
                int spaceWithDots = spaceForText - dotsW;
                String baseTrim = ClientUtils.trimTextToFit(formatted, spaceWithDots);
                trimmed = baseTrim + "..";
            }

            Renderer2D.drawText(ctx, "\"", offsetX, 0, gray, false);
            offsetX += quoteW;

            Renderer2D.drawText(ctx, trimmed, offsetX, 0, white, false);
            offsetX += Renderer2D.getStringWidth(trimmed);

            Renderer2D.drawText(ctx, "\"", offsetX, 0, gray, false);
        }

        ms.pop();
    }


    public void tick() {
        long now = System.currentTimeMillis();
        if (now - lastCursorBlink > 500) {
            cursorVisible = !cursorVisible;
            lastCursorBlink = now;
        }
    }


    public boolean mouseClicked(double mx, double my, double parentX, double parentY, int button) {
        if (button != 0) return false;
        int drawX = (int) (parentX + x);
        int drawY = (int) (parentY + y);
        boolean hit = mx >= drawX && mx < drawX + width && my >= drawY && my < drawY + height;
        focused = hit;

        if (focused) {
            String value = property.getValue();
            cursorIndex = value.length();
            selectedAll = false;
        }
        return hit;
    }


    public void keyTyped(char c, int keyCode) {
        if (!focused) return;

        String value = property.getValue();

        switch (keyCode) {
            case GLFW.GLFW_KEY_BACKSPACE -> {
                if (selectedAll) {
                    property.setValue("");
                    cursorIndex = 0;
                    selectedAll = false;
                } else if (cursorIndex > 0) {
                    property.setValue(value.substring(0, cursorIndex - 1) + value.substring(cursorIndex));
                    cursorIndex--;
                }
            }
            case GLFW.GLFW_KEY_LEFT -> {
                if (cursorIndex > 0) {
                    cursorIndex--;
                    selectedAll = false;
                }
            }
            case GLFW.GLFW_KEY_RIGHT -> {
                if (cursorIndex < value.length()) {
                    cursorIndex++;
                    selectedAll = false;
                }
            }
            case GLFW.GLFW_KEY_A -> {
                if ((GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_LEFT_CONTROL) == GLFW.GLFW_PRESS) || (GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_RIGHT_CONTROL) == GLFW.GLFW_PRESS)) {
                    cursorIndex = value.length();
                    selectedAll = true;
                }
            }
            case GLFW.GLFW_KEY_ESCAPE, GLFW.GLFW_KEY_ENTER -> {
                focused = false;
            }
            default -> {
                if (c >= 32 && c <= 126) {
                    if (selectedAll) {
                        property.setValue("" + c);
                        cursorIndex = 1;
                        selectedAll = false;
                    } else {
                        property.setValue(value.substring(0, cursorIndex) + c + value.substring(cursorIndex));
                        cursorIndex++;
                    }
                }
            }
        }
    }

    public int getHeight() {
        return height + 12;
    }

    public boolean isFocused() {
        return focused;
    }

    public String getValue() {
        return property.getValue();
    }

    public void paste(String clipboardText) {
        if (!focused || clipboardText == null) return;

        String value = property.getValue();
        if (selectedAll) {
            property.setValue(clipboardText);
            cursorIndex = clipboardText.length();
        } else {
            property.setValue(value.substring(0, cursorIndex) + clipboardText + value.substring(cursorIndex));
            cursorIndex += clipboardText.length();
        }
        selectedAll = false;
    }
}
