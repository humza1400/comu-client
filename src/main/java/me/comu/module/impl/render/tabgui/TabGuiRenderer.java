package me.comu.module.impl.render.tabgui;

import net.minecraft.client.gui.DrawContext;

public interface TabGuiRenderer {

    void render(DrawContext context, TabGuiState state, int x, int y);
}
