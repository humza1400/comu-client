package me.comu.module.impl.movement;

import me.comu.module.Category;
import me.comu.module.ToggleableModule;
import me.comu.property.properties.NumberProperty;

import java.util.List;

public class Timer extends ToggleableModule {

    NumberProperty<Float> timer = new NumberProperty<>("Timer", List.of("speed", "t", "s", "timerspeed"), 1.0f, 0.1f, 10.0f, 0.25f);

    public Timer() {
        super("Timer", List.of("timerspeed"), Category.MOVEMENT, "Speeds up everything in the game");
        offerProperties(timer);
    }
}
