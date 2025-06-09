package me.comu.module.impl.render.clickgui.comu.properties;

import me.comu.property.Property;
import me.comu.property.properties.*;
import me.comu.render.Renderer2D;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.List;

public class PropertyContainer {

    private final ListProperty listProperty;
    private final List<BooleanButton> booleanButtons = new ArrayList<>();
    private final List<ValueSlider> sliders = new ArrayList<>();
    private final List<EnumDropdown> dropdowns = new ArrayList<>();
    private final List<StringInput> inputs = new ArrayList<>();
    private final List<PropertyContainer> nestedContainers = new ArrayList<>();

    private int x, y, width, height;
    private final int padding = 5;

    public PropertyContainer(ListProperty listProperty, int x, int y, int width) {
        this.listProperty = listProperty;
        this.x = x;
        this.y = y;
        this.width = width;
        rebuild();
    }

    public void rebuild() {
        booleanButtons.clear();
        sliders.clear();
        dropdowns.clear();
        inputs.clear();
        nestedContainers.clear();

        int offsetY = y + 15;

        for (Property<?> prop : listProperty.getProperties()) {
            if (prop instanceof BooleanProperty boolProp) {
                booleanButtons.add(new BooleanButton(null, boolProp, x + padding, offsetY, 80, 8));
                offsetY += 12;
            } else if (prop instanceof NumberProperty<?> numProp) {
                sliders.add(new ValueSlider((NumberProperty) numProp, x + padding, offsetY, width - padding * 2, 8));
                offsetY += 14;
            } else if (prop instanceof EnumProperty<?> enumProp) {
                dropdowns.add(new EnumDropdown(enumProp, x + padding, offsetY, width - padding * 2, 12));
                offsetY += 22;
            } else if (prop instanceof InputProperty inputProp) {
                inputs.add(new StringInput(inputProp, x + padding, offsetY, width - padding * 2, 12));
                offsetY += 16;
            } else if (prop instanceof ListProperty nestedList) {
                PropertyContainer nested = new PropertyContainer(nestedList, x + padding, offsetY, width - padding * 2);
                nestedContainers.add(nested);
                offsetY += nested.getHeight() + 6;
            }
        }

        this.height = offsetY - y + padding;
    }

    public void draw(DrawContext ctx, int parentX, int parentY, int mouseX, int mouseY) {
        MinecraftClient mc = MinecraftClient.getInstance();
        MatrixStack matrices = ctx.getMatrices();

        int drawX = parentX + x;
        int drawY = parentY + y;

        float scale = 0.8f;

        String title = listProperty.getName();
        int titleRawWidth = Renderer2D.getStringWidth(title);
        int titleWidth = (int) (titleRawWidth * scale);
        int titleHeight = (int) (Renderer2D.getFontHeight() * scale);

        int titleX = drawX + padding;
        int titleY = drawY;

        int lineY = titleY + titleHeight / 2;

        int boxTop = lineY;
        int boxLeft = drawX;
        int boxRight = drawX + width;
        int boxBottom = drawY + height;

        // Draw top border split around title
        ctx.fill(boxLeft, boxTop, titleX - 4, boxTop + 1, 0xFFB0B0B0); // left line
        ctx.fill(titleX + titleWidth + 4, boxTop, boxRight, boxTop + 1, 0xFFB0B0B0); // right line

        // Draw remaining borders
        ctx.fill(boxLeft, boxTop, boxLeft + 1, boxBottom, 0xFFB0B0B0); // left
        ctx.fill(boxRight - 1, boxTop, boxRight, boxBottom, 0xFFB0B0B0); // right
        ctx.fill(boxLeft, boxBottom - 1, boxRight, boxBottom, 0xFFB0B0B0); // bottom

        // Draw background inside box
        ctx.fill(boxLeft, boxTop + 1, boxRight, boxBottom - 1, 0x66181818);

        // Scaled title text
        matrices.push();
        matrices.translate(titleX, titleY, 0);
        matrices.scale(scale, scale, 1.0f);
        Renderer2D.drawText(ctx, title, 0, 0, 0xFFFFFFFF, true);
        matrices.pop();

        // Draw children
        for (BooleanButton b : booleanButtons) b.draw(ctx, parentX, parentY, mouseX, mouseY, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
        for (ValueSlider s : sliders) s.draw(ctx, parentX, parentY, mouseX, mouseY, false);
        for (EnumDropdown d : dropdowns) d.draw(ctx, parentX, parentY, mouseX, mouseY, false);
        for (StringInput i : inputs) i.draw(ctx, parentX, parentY, mouseX, mouseY, Integer.MIN_VALUE, Integer.MAX_VALUE);
        for (PropertyContainer nested : nestedContainers) nested.draw(ctx, parentX, parentY, mouseX, mouseY);
    }


    public int getHeight() {
        return height;
    }
}
