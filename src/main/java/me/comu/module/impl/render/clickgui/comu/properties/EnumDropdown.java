package me.comu.module.impl.render.clickgui.comu.properties;

import me.comu.property.properties.EnumProperty;
import me.comu.render.Renderer2D;
import me.comu.utils.RenderUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EnumDropdown {
    private final EnumProperty enumProperty;
    private final int x, y, width, height;
    private boolean expanded = false;
    private int absoluteX, absoluteY;
    private final List<Enum<?>> sortedValues;


    public EnumDropdown(EnumProperty enumProperty, int x, int y, int width, int height) {
        this.enumProperty = enumProperty;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        this.sortedValues = new ArrayList<>();
        for (Object value : enumProperty.getValues()) {
            if (value instanceof Enum<?>) {
                this.sortedValues.add((Enum<?>) value);
            }
        }

        this.sortedValues.sort((a, b) -> formatEnumName(a).compareToIgnoreCase(formatEnumName(b)));
    }

    public void drawOnTop(DrawContext context, int mouseX, int mouseY) {
        if (!expanded) return;

        float scale = 0.8f;
        int baseX = this.absoluteX;
        int baseY = this.absoluteY;

        int inset = 4;
        int dropdownX = baseX + inset;
        int dropdownWidth = width - inset * 2;
        int optionY = baseY + height;

        MatrixStack matrices = context.getMatrices();
        matrices.push();

        matrices.translate(0, 0, 1000);

        for (Enum<?> option : sortedValues) {
            boolean hovered = mouseX >= dropdownX && mouseX <= dropdownX + dropdownWidth &&
                    mouseY >= optionY && mouseY <= optionY + height;

            boolean isSelected = option == enumProperty.getValue();
            int backgroundColor;

            if (isSelected) {
                backgroundColor = 0xFF90105A;
            } else if (hovered) {
                backgroundColor = 0xFF3A3A3A;
            } else {
                backgroundColor = 0xFF1E1E1E;
            }

            // Draw background (at high Z)
            context.fill(dropdownX, optionY, dropdownX + dropdownWidth, optionY + height, backgroundColor);

            // Draw option text
            matrices.push();
            matrices.translate(dropdownX + 3, optionY + 2, 0);
            matrices.scale(scale, scale, 1.0f);
            Renderer2D.drawText(context, formatEnumName(option), 0, 0, 0xFFFFFF, true);
            matrices.pop();

            optionY += height;
        }

        int borderX1 = dropdownX - 1;
        int borderY1 = baseY + height - 1;
        int borderX2 = dropdownX + dropdownWidth + 1;
        int borderY2 = borderY1 + sortedValues.size() * height + 2;
        RenderUtils.drawGradientBorder(context, borderX1, borderY1, borderX2, borderY2, 0xFF90105A, 0xFFFA74B4);

        matrices.pop();
    }

    public boolean isObstructing(int rectX, int rectY, int rectWidth, int rectHeight) {
        if (!expanded) return false;

        int dropdownX = this.absoluteX;
        int dropdownY = this.absoluteY + height;
        int dropdownW = this.width;
        int dropdownH = sortedValues.size() * height;

        return rectX < dropdownX + dropdownW &&
                rectX + rectWidth > dropdownX &&
                rectY < dropdownY + dropdownH &&
                rectY + rectHeight > dropdownY;
    }



    public boolean wasClickConsumed(double mouseX, double mouseY, int button) {
        if (!expanded) return false;

        int x = this.absoluteX;
        int y = this.absoluteY;

        if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
            return true;
        }

        int optionY = y + height;
        for (Enum<?> option : sortedValues) {
            if (mouseX >= x && mouseX <= x + width && mouseY >= optionY && mouseY <= optionY + height) {
                return true;
            }
            optionY += height;
        }

        return false;
    }

    public void draw(DrawContext context, int drawX, int drawY, int mouseX, int mouseY, boolean hoverDisabled) {
        float scale = 0.8f;
        int x = drawX + this.x;
        int y = drawY + this.y;

        setAbsolutePosition(x, y);
        String name = enumProperty.getName();
        MatrixStack matrices = context.getMatrices();

        matrices.push();
        matrices.translate(x, y - (int) (10 * scale), 0);
        matrices.scale(scale, scale, 1.0f);
        Renderer2D.drawText(context, name, 0, 0, 0xFFFFFF, true);
        matrices.pop();

        context.fill(x, y, x + width, y + height, 0xFF2B2B2B);

        if (!hoverDisabled && isHovered(mouseX, mouseY)) {
            context.fill(x, y, x + width, y + height, 0x30FFFFFF);
        }

        String current = formatEnumName((Enum<?>) enumProperty.getValue());

        matrices.push();
        matrices.translate(x + 3, y + 2, 0);
        matrices.scale(scale, scale, 1.0f);
        Renderer2D.drawText(context, current, 0, 0, 0xFFFFFFFF, true);
        matrices.pop();

/*        if (expanded) {
            int optionY = y + height;
            for (Enum<?> option : sortedValues) {
                boolean isSelected = option == enumProperty.getValue();
                int backgroundColor = isSelected ? 0xFF90105A : 0xFF1E1E1E;
                context.fill(x + 3, optionY, x + width - 3, optionY + height, backgroundColor);

                matrices.push();
                matrices.translate(x + 3, optionY + 2, 0);
                matrices.scale(scale, scale, 1.0f);
                Renderer2D.drawText(context, formatEnumName(option), 0, 0, 0xFFFFFFFF, true);
                matrices.pop();

                optionY += height;
            }
        }*/
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = this.absoluteX;
        int y = this.absoluteY;

        if (button != 0) return false;

        if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
            expanded = !expanded;
            return true;
        }

        if (expanded) {
            int optionY = y + height;
            for (Enum<?> option : sortedValues) {
                if (mouseX >= x && mouseX <= x + width && mouseY >= optionY && mouseY <= optionY + height) {
                    enumProperty.setValue(option);
                    expanded = false;
                    return true;
                }
                optionY += height;
            }

            expanded = false;
        }

        return false;
    }



    public int getHeight() {
        return expanded ? height + height * sortedValues.size() : height;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setAbsolutePosition(int absoluteX, int absoluteY) {
        this.absoluteX = absoluteX;
        this.absoluteY = absoluteY;
    }


    private String formatEnumName(Enum<?> value) {
        String[] parts = value.toString().toLowerCase().split("_");
        StringBuilder sb = new StringBuilder();

        for (String part : parts) {
            if (!part.isEmpty()) {
                sb.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1)).append(" ");
            }
        }

        return sb.toString().trim();
    }

    public boolean isHovered(int mouseX, int mouseY) {
        if (!expanded)
            return mouseX >= absoluteX && mouseX <= absoluteX + width && mouseY >= absoluteY && mouseY <= absoluteY + height;

        int optionY = absoluteY + height;
        for (Enum<?> option : sortedValues) {
            if (mouseX >= absoluteX && mouseX <= absoluteX + width && mouseY >= optionY && mouseY <= optionY + height) {
                return true;
            }
            optionY += height;
        }

        return mouseX >= absoluteX && mouseX <= absoluteX + width && mouseY >= absoluteY && mouseY <= absoluteY + height;
    }

    public int getAbsoluteX() {
        return absoluteX;
    }

    public int getAbsoluteY() {
        return absoluteY;
    }

    public int getWidth() {
        return width;
    }
    public int getOptionCount() {
        return sortedValues.size();
    }

    public int getOptionHeight() {
        return height;
    }


}
