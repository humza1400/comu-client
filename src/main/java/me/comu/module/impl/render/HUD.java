package me.comu.module.impl.render;

import me.comu.module.Category;
import me.comu.module.ToggleableModule;
import me.comu.property.properties.*;

import java.util.List;

public class HUD extends ToggleableModule {
/*    BooleanProperty showCoords = new BooleanProperty("Show Coords", List.of("coords"), true);
    NumberProperty<Double> scale = new NumberProperty<>("Scale", List.of("scaling"), 1.0d, 0.5d, 2.0d, 0.1d);
    EnumProperty<Alignment> alignment = new EnumProperty<>("Alignment", List.of("align"), Alignment.LEFT);
    InputProperty name = new InputProperty("Custom Name", List.of("tag"), "ComuClient");

    // Nested ListProperty
//    ListProperty nested = new ListProperty("Advanced Settings", List.of("advanced"), List.of(
//            new BooleanProperty("Show Background", List.of("bg"), true),
//            new NumberProperty<Float>("Background Alpha", List.of("alpha"), 0.8f, 0.0f, 1.0f, 1.0f),
//            new EnumProperty<>( "Color Mode", List.of("colormode"), ColorMode.RAINBOW)
//    ));

    public enum Alignment {
        LEFT, CENTER, RIGHT
    }

    public enum ColorMode {
        STATIC, RAINBOW, PULSE
    }

    // Main ListProperty
    ListProperty settings = new ListProperty("HUD Settings", List.of("hudsettings"), List.of(
            showCoords,
            scale,
            alignment,
            name
    ));*/

    public HUD() {
        super("HUD", List.of("textgui"), Category.RENDER, "Shows the client overlay and visual components");
        offerProperties();
    }
}
