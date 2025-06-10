package me.comu.module.impl.world;

import me.comu.module.Category;
import me.comu.module.ToggleableModule;
import me.comu.property.properties.NumberProperty;

import java.util.List;

public class Timer extends ToggleableModule {

    NumberProperty<Float> timer = new NumberProperty<>("Multiplier", List.of("speed", "t", "s", "timerspeed", "timer"), 1.0f, 0.1f, 10.0f, 0.25f);

    public Timer() {
        super("Timer", List.of("timerspeed"), Category.WORLD, "Speeds up everything in the game");
        offerProperties(timer);
    }
}
