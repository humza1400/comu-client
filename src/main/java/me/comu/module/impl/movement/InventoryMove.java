package me.comu.module.impl.movement;

import me.comu.module.Category;
import me.comu.module.ToggleableModule;
import me.comu.property.properties.EnumProperty;

import java.util.List;

public class InventoryMove extends ToggleableModule {

    EnumProperty<Mode> mode = new EnumProperty<>("Mode", List.of("m"), Mode.VANILLA);

    public enum Mode {
        VANILLA
    }

    public InventoryMove() {
        super("Inventory Move", List.of("invmove", "invwalk","inventorymove", "inventorywalk"), Category.MOVEMENT, "Allows you to move while in inventory screens");
        offerProperties(mode);
    }
}
