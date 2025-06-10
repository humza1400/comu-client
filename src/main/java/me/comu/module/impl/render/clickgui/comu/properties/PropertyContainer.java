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

        int offsetY = y + 12;
        int gapBetween = 6;

        List<Property<?>> props = listProperty.getProperties();

        for (int i = 0; i < props.size(); i++) {
            Property<?> prop = props.get(i);
            boolean isLast = (i == props.size() - 1);

            if (prop instanceof BooleanProperty boolProp) {
                booleanButtons.add(new BooleanButton(null, boolProp, x + padding, offsetY, 80, 8));
                offsetY += 14;
            } else if (prop instanceof NumberProperty<?> numProp) {
                sliders.add(new ValueSlider((NumberProperty) numProp, padding, offsetY - y, width - padding * 2, 8));
                offsetY += 12;
            } else if (prop instanceof EnumProperty<?> enumProp) {
                dropdowns.add(new EnumDropdown(enumProp, x + padding, offsetY, width - padding * 2, 12));
                offsetY += 22;
            } else if (prop instanceof InputProperty inputProp) {
                inputs.add(new StringInput(inputProp, x + padding, offsetY, width - padding * 2, 12));
                offsetY += 16;
            } else if (prop instanceof ListProperty nestedList) {
                PropertyContainer nested = new PropertyContainer(nestedList, x + padding, offsetY, width - padding * 2);
                nestedContainers.add(nested);
                offsetY += nested.getHeight();
            }

            if (!isLast) {
                offsetY += gapBetween;
            }
        }

        this.height = Math.max(18, offsetY - y + padding);
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

        int titleOffset = 5;
        int titleX = drawX + padding + titleOffset;
        int titleY = drawY;

        int lineY = titleY + titleHeight / 2;

        int boxTop = lineY;
        int boxLeft = drawX;
        int boxRight = drawX + width;
        int boxBottom = drawY + height;

        ctx.fill(boxLeft, boxTop, titleX - 4, boxTop + 1, 0xFFB0B0B0);
        ctx.fill(titleX + titleWidth + 4, boxTop, boxRight, boxTop + 1, 0xFFB0B0B0);

        ctx.fill(boxLeft, boxTop, boxLeft + 1, boxBottom, 0xFFB0B0B0);
        ctx.fill(boxRight - 1, boxTop, boxRight, boxBottom, 0xFFB0B0B0);
        ctx.fill(boxLeft, boxBottom - 1, boxRight, boxBottom, 0xFFB0B0B0);

        ctx.fill(boxLeft, boxTop + 1, boxRight, boxBottom - 1, 0x66181818);

        matrices.push();
        matrices.translate(titleX, titleY, 0);
        matrices.scale(scale, scale, 1.0f);
        Renderer2D.drawText(ctx, title, 0, 0, 0xFFFFFFFF, true);
        matrices.pop();

        for (BooleanButton b : booleanButtons)
            b.draw(ctx, parentX, parentY, mouseX, mouseY, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
        for (ValueSlider s : sliders) s.draw(ctx, drawX, drawY, mouseX, mouseY, false);
        for (EnumDropdown d : dropdowns) d.draw(ctx, parentX, parentY, mouseX, mouseY, false);
        for (StringInput i : inputs)
            i.draw(ctx, parentX, parentY, mouseX, mouseY, Integer.MIN_VALUE, Integer.MAX_VALUE);
        for (PropertyContainer nested : nestedContainers) nested.draw(ctx, parentX, parentY, mouseX, mouseY);
    }

    public boolean mouseClicked(double mouseX, double mouseY, double parentX, double parentY, int button) {
        int drawX = (int) parentX + x;
        int drawY = (int) parentY + y;
        for (BooleanButton b : booleanButtons) {
            if (b.mouseClicked(mouseX, mouseY, (int) parentX, (int) parentY, button)) {
                return true;
            }
        }

        for (ValueSlider s : sliders)
            if (s.mouseClicked(drawX, drawY, mouseX, mouseY, button)) return true;

        for (EnumDropdown d : dropdowns) {
            if (d.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }

        for (StringInput i : inputs) {
            if (i.mouseClicked(mouseX, mouseY, parentX, parentY, button)) {
                return true;
            }
        }

        for (PropertyContainer nested : nestedContainers) {
            if (nested.mouseClicked(mouseX, mouseY, parentX + x + padding, parentY + y + padding, button)) {
                return true;
            }
        }

        return false;
    }

    public void handleKeyTyped(char c, int keyCode) {
        for (StringInput i : inputs) {
            i.keyTyped(c, keyCode);
        }
        for (PropertyContainer nested : nestedContainers) {
            nested.handleKeyTyped(c, keyCode);
        }
    }

    public void handleMouseDrag(int parentX, int parentY, int mouseX, int mouseY) {
        for (ValueSlider s : sliders) {
            s.handleMouse(parentX + x, parentY + y, mouseX, mouseY);
        }
        for (PropertyContainer nested : nestedContainers) {
            nested.handleMouseDrag(parentX + x + padding, parentY + y + padding, mouseX, mouseY);
        }
    }

    public int getHeight() {
        return height;
    }

    public void collectExpandedDropdowns(List<EnumDropdown> list) {
        for (EnumDropdown dropdown : dropdowns) {
            if (dropdown.isExpanded()) {
                list.add(dropdown);
            }
        }
        for (PropertyContainer nested : nestedContainers) {
            nested.collectExpandedDropdowns(list);
        }
    }

    public void collectStringInputs(List<StringInput> list) {
        list.addAll(inputs);
        for (PropertyContainer nested : nestedContainers) {
            nested.collectStringInputs(list);
        }
    }
}
