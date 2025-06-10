package me.comu.module.impl.render;

import me.comu.module.Category;
import me.comu.module.ToggleableModule;
import me.comu.property.properties.*;

import java.util.List;

public class HUD extends ToggleableModule {
    BooleanProperty mobs = new BooleanProperty("Monsters", List.of("mobs", "mob", "zombie", "zombies", "skeleton", "skeletons"), true);
    BooleanProperty passives = new BooleanProperty("Passives", List.of("passive", "villager", "villagers", "neutral", "neutrals", "animal", "cow", "cows", "sheep"), true);
    BooleanProperty players = new BooleanProperty("Players", List.of("player", "people"), true);
    BooleanProperty rayTrace = new BooleanProperty("Ray-Trace", List.of("raytrace", "rayt", "ray", "rt"), true);
    BooleanProperty cooldownAttack = new BooleanProperty("Cooldown", List.of("1.9pvp", "19pvp", "1.9", "19", "cd"), false);

    NumberProperty<Float> range = new NumberProperty<>("Reach", List.of("range", "r"), 4f, 3f, 6f, 0.1f);
    NumberProperty<Integer> aps = new NumberProperty<>("APS", List.of("speed", "cps"), 10, 1, 20, 1);
    NumberProperty<Integer> fov = new NumberProperty<>("FOV", List.of(), 180, 1, 180, 30);
    BooleanProperty showCoords = new BooleanProperty("Show Coords", List.of("coords"), true);
    NumberProperty<Double> scale = new NumberProperty<>("Scale", List.of("scaling"), 1.0d, 0.5d, 2.0d, 0.1d);
    EnumProperty<Alignment> alignment = new EnumProperty<>("Alignment", List.of("align"), Alignment.LEFT);
    InputProperty name = new InputProperty("Custom Name", List.of("tag"), "ComuClient");

    ListProperty nested = new ListProperty("Advanced Settings", List.of("advanced"), List.of(
            new BooleanProperty("Show Background", List.of("bg"), true),
            new NumberProperty<Float>("Background Alpha", List.of("alpha"), 0.8f, 0.0f, 1.0f, 1.0f),
            new EnumProperty<>( "Color Mode", List.of("colormode"), ColorMode.RAINBOW)
    ));

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
            name,
            nested
    ));

    public HUD() {
        super("HUD", List.of("textgui"), Category.RENDER, "Shows the client overlay and visual components");
        offerProperties(settings, nested, range, aps, fov, mobs, passives, players, rayTrace, cooldownAttack);
    }
}
