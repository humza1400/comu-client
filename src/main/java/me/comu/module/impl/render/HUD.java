package me.comu.module.impl.render;

import me.comu.module.Category;
import me.comu.module.ToggleableModule;
import me.comu.property.properties.*;

import java.util.List;

public class HUD extends ToggleableModule {

    private final BooleanProperty potions = new BooleanProperty("Potions", List.of("potion", "pot", "pots"), true);
    private final BooleanProperty coords = new BooleanProperty("Coords", List.of("coordinate", "coordinates", "coord", "xyz"), true);
    private final BooleanProperty clock = new BooleanProperty("Clock", List.of("time"), true);
    private final BooleanProperty fps = new BooleanProperty("FPS", List.of("raytrace", "rayt", "ray", "rt"), true);
    private final BooleanProperty ping = new BooleanProperty("Ping", List.of("ms"), true);
    private final BooleanProperty direction = new BooleanProperty("Direction", List.of("facing"), true);
    private final BooleanProperty gapple = new BooleanProperty("Gapple", List.of("gappletimer"), true);
    private final BooleanProperty watermark = new BooleanProperty("Watermark", List.of("title"), true);
    private final BooleanProperty arrayList = new BooleanProperty("Array List", List.of("arraylist"), true);
    private final BooleanProperty armor = new BooleanProperty("Armor", List.of(), true);
    private final BooleanProperty bps = new BooleanProperty("BPS", List.of("blockspersec","blockspersecond"), true);
    private final BooleanProperty tps = new BooleanProperty("TPS", List.of(""), true);

    private final BooleanProperty suffix = new BooleanProperty("Suffix", List.of(""), true);
    private final EnumProperty<ArrayListPosition> arrayListPosition = new EnumProperty<>("Position", List.of("arraylistposition"), ArrayListPosition.TOPRIGHT);
    private final EnumProperty<ArrayListTheme> arrayListTheme = new EnumProperty<>("Theme", List.of("arraylisttheme"), ArrayListTheme.COMU);
    private final EnumProperty<ArrayListSort> arrayListSort = new EnumProperty<>("Sort", List.of("arraylistsort"), ArrayListSort.LONGEST);
    private final EnumProperty<ArrayListCase> arrayListCase = new EnumProperty<>("Casing", List.of("arraylistcase", "arraylistcasing"), ArrayListCase.DEFAULT);
    private final EnumProperty<ArrayListAnimation> arrayListAnimation = new EnumProperty<>("Animation", List.of("arraylistanimation", "arraylisttransittion"), ArrayListAnimation.DEFAULT);
    private final ListProperty arrayListOptions = new ListProperty("Array List Options", List.of("arraylistoption", "arrayoptions", "arrayoption", "arraylistoptions"), List.of(suffix, arrayListPosition, arrayListTheme, arrayListAnimation, arrayListSort, arrayListCase));

    public enum ArrayListPosition {
        TOPLEFT, TOPRIGHT, CROSSHAIR
    }

    public enum ArrayListTheme {
        MINECRAFT, INDIGO, COMU, GRAYSCALE, WHITE, RAINBOW, VIRTUE
    }

    public enum ArrayListSort {
        ABC, REVERSE_ABC, LONGEST, SHORTEST, CATEGORY
    }

    public enum ArrayListCase {
        DEFAULT, LOWER, UPPER, CUB, PAREN, DASH, STAR
    }

    public enum ArrayListAnimation {
        DEFAULT, SLIDE, BOUNCE, FADE
    }

    public HUD() {
        super("HUD", List.of("textgui"), Category.RENDER, "Shows the client overlay and visual components");
        offerProperties(potions, coords, clock, fps, ping, direction, gapple, watermark, arrayList, arrayListOptions);
    }

    public BooleanProperty getPotions() {
        return potions;
    }

    public BooleanProperty getCoords() {
        return coords;
    }

    public BooleanProperty getClock() {
        return clock;
    }

    public BooleanProperty getFps() {
        return fps;
    }

    public BooleanProperty getPing() {
        return ping;
    }

    public BooleanProperty getDirection() {
        return direction;
    }

    public BooleanProperty getGapple() {
        return gapple;
    }

    public BooleanProperty getWatermark() {
        return watermark;
    }

    public BooleanProperty getArrayList() {
        return arrayList;
    }

    public BooleanProperty getArmor() {
        return armor;
    }

    public BooleanProperty shouldRenderSuffix() {
        return suffix;
    }

    public EnumProperty<ArrayListSort> getArrayListSort() {
        return arrayListSort;
    }

    public EnumProperty<ArrayListCase> getArrayListCase() {
        return arrayListCase;
    }

    public EnumProperty<ArrayListPosition> getArrayListPosition() {
        return arrayListPosition;
    }

    public EnumProperty<ArrayListAnimation> getArrayListAnimation() {
        return arrayListAnimation;
    }

    public EnumProperty<ArrayListTheme> getArrayListTheme() {
        return arrayListTheme;
    }
}
