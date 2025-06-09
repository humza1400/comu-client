package me.comu.module.impl.render.clickgui.comu;

import me.comu.module.Category;
import me.comu.render.Renderer2D;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class ComuPanel {

    private Category category;
    private int x, y, width, height;

    public ComuPanel(Category category, int x, int y, int width, int height) {
        this.category = category;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void draw(DrawContext context, int mouseX, int mouseY, boolean selected) {
        boolean hovering = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
        int backgroundColor = selected ? 0xFF2A2A2A : 0xFF1A1A1A;
        int outlineColor = hovering ? 0xFFAAAAAA : 0xFF707070;
        int textColor = 0xFFFFFFFF;

        context.fill(x, y, x + width, y + height, backgroundColor);
        context.fill(x, y, x + width, y + 1, outlineColor);
        context.fill(x, y + height - 1, x + width, y + height, outlineColor);
        context.fill(x, y, x + 1, y + height, outlineColor);
        context.fill(x + width - 1, y, x + width, y + height, outlineColor);

        String name = category != null ? category.getName() : "Active";
        if (name.equalsIgnoreCase("miscellaneous")) name = "Misc";

        int textWidth = Renderer2D.getStringWidth(name);
        int textX = x + (width - textWidth) / 2;
        int textY = y + (height - Renderer2D.getFontHeight()) / 2;

        context.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, name, textX, textY, textColor);
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getHeight() {
        return height;
    }

    public Category getCategory() {
        return category;
    }

    public boolean isHovered(double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public int getWidth() {
        return width;
    }

    public String getDisplayName() {
        return category != null ? category.name() : "Always Active";
    }
}
