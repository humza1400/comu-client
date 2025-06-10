package me.comu.module.impl.render.clickgui.comu.properties;

import me.comu.property.properties.NumberProperty;
import me.comu.render.Renderer2D;
import me.comu.utils.ClientUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.glfw.GLFW;

public class ValueSlider {
    private final NumberProperty<Number> valueProperty;
    private final int x, y, width, height;
    private boolean dragging;
    private static final int SLIDER_COLOR = 0xFF90105A;

    public ValueSlider(NumberProperty<Number> valueProperty, int x, int y, int width, int height) {
        this.valueProperty = valueProperty;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void draw(DrawContext context, int drawX, int drawY, int mouseX, int mouseY, boolean hoverDisabled) {
        int x = drawX + this.x;
        int y = drawY + this.y;

        int sliderHeight = 4;
        int sliderWidth = width;

        Number min = valueProperty.getMin();
        Number max = valueProperty.getMax();
        Number val = valueProperty.getValue();

        double percentage = (val.doubleValue() - min.doubleValue()) / (max.doubleValue() - min.doubleValue());
        int filledWidth = (int) (percentage * sliderWidth);

        context.fill(x, y, x + sliderWidth, y + sliderHeight, 0xFF2B2B2B);
        context.fill(x, y, x + filledWidth, y + sliderHeight, SLIDER_COLOR);

        if (!hoverDisabled && isHovered(drawX, drawY, mouseX, mouseY)) {
            context.fill(x, y, x + sliderWidth, y + sliderHeight, 0x30FFFFFF);
        }

        float scale = 0.8f;
        String formattedValue;
        double d = val.doubleValue();
        if (d < 0.01) formattedValue = String.format("%.5f", d);
        else if (d < 1) formattedValue = String.format("%.3f", d);
        else formattedValue = String.format("%.2f", d);

        String label = valueProperty.getName() + ": " + formattedValue;

        MatrixStack matrices = context.getMatrices();
        matrices.push();
        matrices.translate(x, y - (int) (10 * scale), 0);
        matrices.scale(scale, scale, 1.0f);
        Renderer2D.drawText(context, label, 0, 0, 0xFFFFFF, true);
        matrices.pop();
    }


    public void handleMouse(int drawX, int drawY, int mouseX, int mouseY) {
        int x = drawX + this.x;
        int y = drawY + this.y;

        if (!ClientUtils.isMouseButtonDown(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
            dragging = false;
            return;
        }

        if (dragging) {
            double percent = Math.max(0, Math.min(1, (mouseX - x) / (double) width));
            double newValue = valueProperty.getMin().doubleValue() + percent * (valueProperty.getMax().doubleValue() - valueProperty.getMin().doubleValue());

            if (valueProperty.getValue() instanceof Integer) valueProperty.setValue((int) Math.round(newValue));
            else if (valueProperty.getValue() instanceof Float) valueProperty.setValue((float) newValue);
            else if (valueProperty.getValue() instanceof Long) valueProperty.setValue((long) newValue);
            else valueProperty.setValue(newValue);
        }
    }


    public boolean mouseClicked(int drawX, int drawY, double mouseX, double mouseY, int button) {
        int x = drawX + this.x;
        int y = drawY + this.y;
        float scale = 0.8f;
        int scaledHeight = 4;

        if (button == 0 && mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + scaledHeight) {
            dragging = true;
            return true;
        }
        return false;
    }

    private boolean isHovered(int drawX, int drawY, int mouseX, int mouseY) {
        int x = drawX + this.x;
        int y = drawY + this.y;
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }
}
