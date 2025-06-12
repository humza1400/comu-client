package me.comu.module.impl.movement;

import me.comu.module.Category;
import me.comu.module.ToggleableModule;
import me.comu.property.properties.BooleanProperty;
import me.comu.property.properties.EnumProperty;

import java.util.List;

public class NoSlowdown extends ToggleableModule {

    BooleanProperty cobWebs = new BooleanProperty("Cobwebs", List.of("cob", "web", "webs", "cobs"), false);

    public NoSlowdown() {
        super("No Slowdown", List.of("noslow", "noslow"), Category.MOVEMENT, "Consume items without slowing down");
        offerProperties(cobWebs);
    }

    public BooleanProperty getCobWebs() {
        return cobWebs;
    }
}
