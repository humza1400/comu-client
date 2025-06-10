package me.comu.module.impl.render.clickgui.comu.properties;

import me.comu.module.Module;
import me.comu.property.properties.BooleanProperty;
import me.comu.render.Renderer2D;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;

public class BooleanButton {

    private Module module;
    private BooleanProperty property;
    private int x, y, width, height;
    private String label;

    private static final int boxSize = 8;
    private static final int labelGap = 3;
    private static final float scaleFactor = 0.8f;

    public BooleanButton(me.comu.module.Module module, BooleanProperty property, int x, int y, int width, int height) {
        this.module = module;
        this.property = property;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.label = property.getName();
    }

    public void draw(DrawContext context, int parentX, int parentDrawY, int mouseX, int mouseY, int visibleTop, int visibleBottom, boolean hoverDisabled) {
        int boxX = parentX + x;
        int boxY = parentDrawY + y;

        if (boxY + height < visibleTop || boxY > visibleBottom) {
            return;
        }

        context.fill(boxX, boxY, boxX + boxSize, boxY + boxSize, 0xFF555555);

        if (property.getValue()) {
            int inset = 2;
            context.fill(boxX + inset, boxY + inset, boxX + boxSize - inset, boxY + boxSize - inset, 0xFFAA00FF);
        }

        context.fill(boxX, boxY, boxX + boxSize, boxY + 1, 0xFF000000);
        context.fill(boxX, boxY, boxX + 1, boxY + boxSize, 0xFF000000);
        context.fill(boxX + boxSize - 1, boxY, boxX + boxSize, boxY + boxSize, 0xFF000000);
        context.fill(boxX, boxY + boxSize - 1, boxX + boxSize, boxY + boxSize, 0xFF000000);

        if (!hoverDisabled && isHovered(mouseX, mouseY, parentX, parentDrawY)) {
            context.fill(boxX, boxY, boxX + boxSize, boxY + boxSize, 0x30FFFFFF);
        }

        MatrixStack matrices = context.getMatrices();
        matrices.push();
        matrices.translate(boxX + boxSize + labelGap, boxY + 1, 0);
        matrices.scale(scaleFactor, scaleFactor, 1f);
        Renderer2D.drawText(context, label, 0, 0, 0xFFFFFF, true);
        matrices.pop();
    }

    public boolean mouseClicked(double mouseX, double mouseY, int parentX, int parentDrawY, int button) {
        if (button == 0 && isHovered(mouseX, mouseY, parentX, parentDrawY)) {
            property.setValue(!property.getValue());

            MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));

            return true;
        }

        return false;
    }

    public boolean isHovered(double mouseX, double mouseY, int parentX, int parentDrawY) {
        int boxX = parentX + x;
        int boxY = parentDrawY + y;

        if (mouseX >= boxX && mouseX < boxX + boxSize && mouseY >= boxY && mouseY < boxY + boxSize) {
            return true;
        }

        int textX = boxX + boxSize + labelGap;
        int textY = boxY;
        int textWidth = (int) (Renderer2D.getStringWidth(label) * scaleFactor);
        int textHeight = (int) (Renderer2D.getFontHeight() * scaleFactor);

        return mouseX >= textX && mouseX < textX + textWidth && mouseY >= textY && mouseY < textY + textHeight;
    }
}
