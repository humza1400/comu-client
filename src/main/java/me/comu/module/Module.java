package me.comu.module;

import me.comu.Comu;
import me.comu.api.registry.event.listener.Listener;
import me.comu.logging.Logger;
import me.comu.mixin.minecraft.MinecraftClientAccessor;
import me.comu.property.Property;
import me.comu.property.properties.ListProperty;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Module {
    private final String name;
    private String displayName;
    private String suffix;
    private final List<String> aliases;
    private final List<Property<?>> properties = new ArrayList<>();
    private final Category category;
    private final String description;

    @SuppressWarnings("rawtypes")
    protected final List<Listener> listeners = new ArrayList<>();
    protected final MinecraftClient mc = MinecraftClient.getInstance();
    protected final MinecraftClientAccessor mcAccessor = (MinecraftClientAccessor) mc;

    public Module(String name, List<String> aliases, Category category, String description) {
        this.name = name;
        this.displayName = name;
        this.aliases = aliases;
        this.category = category;
        this.description = description;
    }

    public Module(String name, String displayName, List<String> aliases, Category category, String description) {
        this.name = name;
        this.displayName = displayName;
        this.aliases = aliases;
        this.category = category;
        this.description = description;
    }

    protected void offerProperties(Property<?>... properties) {
        this.properties.clear();
        this.properties.addAll(Arrays.asList(properties));
    }

    public Property<?> getPropertyByName(String name) {
        for (Property<?> prop : properties) {
            if (prop.getName().equalsIgnoreCase(name)) return prop;
            for (String alias : prop.getAliases()) {
                if (alias.equalsIgnoreCase(name)) return prop;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void resetDisplayName() {
        setDisplayName(name);
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public Category getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public List<Property<?>> getProperties() {
        return properties;
    }

    protected boolean isPlayerOrWorldNull(MinecraftClient mc) {
        return mc.player == null && mc.world == null;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
