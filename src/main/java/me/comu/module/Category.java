package me.comu.module;

import java.util.Arrays;
import java.util.List;

public enum Category {
    COMBAT("Combat"), PLAYER("Player"), MISCELLANEOUS("Miscellaneous"), MOVEMENT("Movement"), RENDER("Render"), WORLD("World"), PERSISTENT("Persistent", false), CONFIG("Config", false);

    private final String name;
    private final boolean isToggleableCategory;

    Category(String name) {
        this.name = name;
        this.isToggleableCategory = true;
    }

    Category(String name, boolean isToggleableCategory) {
        this.name = name;
        this.isToggleableCategory = isToggleableCategory;
    }

    public String getName() {
        return name;
    }

    public static List<Category> getAllCategories() {
        return Arrays.stream(Category.values()).toList();
    }

    public static List<Category> getToggleCategories() {
        return Arrays.stream(Category.values()).filter(category -> category.isToggleableCategory).toList();
    }
}
