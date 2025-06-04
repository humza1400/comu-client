package me.comu.module;

import me.comu.api.registry.event.listener.Listener;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.List;

public abstract class Module {
    private final String name;
    private final List<String> aliases;
    private final Category category;
    private final String description;

    @SuppressWarnings("rawtypes")
    protected final List<Listener> listeners = new ArrayList<>();
    protected MinecraftClient mc = MinecraftClient.getInstance();

    public Module(String name, List<String> aliases, Category category, String description) {
        this.name = name;
        this.aliases = aliases;
        this.category = category;
        this.description = description;
    }

    public String getName() {
        return name;
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
}
