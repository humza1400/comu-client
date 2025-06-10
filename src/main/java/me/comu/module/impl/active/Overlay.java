package me.comu.module.impl.active;

import me.comu.module.Category;
import me.comu.module.Module;
import me.comu.property.properties.BooleanProperty;
import me.comu.property.properties.ListProperty;

import java.util.List;

public class Overlay extends Module {

    BooleanProperty blockOverlay = new BooleanProperty("Block Overlay", List.of("blockoverlay", "blockpos"), true);
    BooleanProperty noFire = new BooleanProperty("Fire Animation", List.of("fire", "fireeffect", "fireanimation"), false);
    BooleanProperty noVanillPotionHud = new BooleanProperty("Potion Icons", List.of("vanillapotionicon", "vanillapotionicons", "vanillastatusicon","vanillastatusicons", "effecticon", "effecticons", "vanillapotions", "vanillapotion", "vanillapots", "vanillapot", "poticons", "poticon", "potionicon", "potionicons"), true);

    ListProperty noRender = new ListProperty("No Render", List.of("norender", "nr"), List.of(noFire, noVanillPotionHud));

    public Overlay() {
        super("Overlay", List.of(), Category.PERSISTENT, "Modify vanilla minecraft screen behavior");
        offerProperties(noRender, blockOverlay);
    }
}
