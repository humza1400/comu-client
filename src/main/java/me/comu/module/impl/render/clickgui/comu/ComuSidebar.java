package me.comu.module.impl.render.clickgui.comu;


import com.mojang.blaze3d.systems.RenderSystem;
import me.comu.module.Category;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.Window;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class ComuSidebar {

    private final List<ComuPanel> categoryPanels = new ArrayList<>();
    private Category selectedCategory = Category.COMBAT;
    private int baseX, baseY;
    private boolean inSearchMode = false;
    private int scrollOffset = 0;
    private static final int MAX_HEIGHT = 290;


    public void init(int startX, int startY, int width, int height, int spacing) {
        categoryPanels.clear();
        for (Category category : Category.values()) {
            categoryPanels.add(new ComuPanel(category, 0, 0, width, height));
        }
        categoryPanels.add(new ComuPanel(null, 0, 0, width, height));
    }

    public void draw(DrawContext context, int mouseX, int mouseY) {
        int paneX = baseX, paneY = baseY;
        int paneW = getWidth(), paneH = getHeight();
        int contentHeight = getTotalContentHeight();

        int scrollBarWidth = 1;
        int trackX = paneX + paneW + 3;
//        int trackY = paneY;
//        context.fill(trackX, trackY, trackX + scrollBarWidth, trackY + paneH, 0xFF333333);
        if (contentHeight > paneH) {
            double ratio = paneH / (double) contentHeight;
            int thumbH = Math.max((int) (paneH * ratio), 10);
            int maxScroll = contentHeight - paneH;
            double scrollFrac = scrollOffset / (double) maxScroll;
            int thumbY = paneY + (int) ((paneH - thumbH) * scrollFrac);

            context.fill(trackX, thumbY, trackX + scrollBarWidth, thumbY + thumbH, 0x4DCCCCCC);
        }

        context.enableScissor(paneX, paneY, paneX + paneW, paneY + paneH);

        int y = paneY - scrollOffset;
        for (ComuPanel panel : categoryPanels) {
            panel.setPosition(paneX, y);
            boolean sel = !inSearchMode && panel.getCategory() == selectedCategory;
            panel.draw(context, mouseX, mouseY, sel);
            y += panel.getHeight() + 5;
        }

        context.disableScissor();
    }


    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (ComuPanel panel : categoryPanels) {
            if (panel.isHovered(mouseX, mouseY)) {
                selectedCategory = panel.getCategory();
                inSearchMode = false;
                MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                return true;
            }
        }
        return false;
    }

    public void handleScroll(int direction) {
        int scrollAmount = 15;
        int contentHeight = getTotalContentHeight();
        int maxScroll = Math.max(0, contentHeight - MAX_HEIGHT);

        scrollOffset = MathHelper.clamp(scrollOffset + direction * scrollAmount, 0, maxScroll);
    }


    private int getTotalContentHeight() {
        int height = 0;
        for (ComuPanel panel : categoryPanels) {
            height += panel.getHeight() + 5;
        }
        return height;
    }

    public void setInSearchMode(boolean searching) {
        this.inSearchMode = searching;
    }

    public boolean isInSearchMode() {
        return inSearchMode;
    }

    public Category getSelectedCategory() {
        return selectedCategory;
    }

    public void setPosition(int x, int y) {
        this.baseX = x;
        this.baseY = y;
    }

    public int getX() {
        return baseX;
    }

    public int getY() {
        return baseY;
    }

    public int getWidth() {
        return categoryPanels.isEmpty() ? 60 : categoryPanels.getFirst().getWidth();
    }

    public int getHeight() {
        return MAX_HEIGHT;
    }

}