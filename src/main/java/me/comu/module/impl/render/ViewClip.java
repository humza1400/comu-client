package me.comu.module.impl.render;

import me.comu.module.Category;
import me.comu.module.ToggleableModule;

import java.util.List;

public class ViewClip extends ToggleableModule {
    public ViewClip() {
        super("View Clip", List.of("viewclip", "vc"), Category.RENDER, "Doesn't let the camera clip on blocks when in third person");
        offerProperties();
    }
}
