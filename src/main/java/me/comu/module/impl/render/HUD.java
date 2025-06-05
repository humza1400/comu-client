package me.comu.module.impl.render;

import me.comu.module.Category;
import me.comu.module.ToggleableModule;

import java.util.List;

public class HUD extends ToggleableModule {

    public HUD() {
        super("HUD", List.of("textgui"), Category.RENDER, "Shows the client overlay and visual components");

    }
}
