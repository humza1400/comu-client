package me.comu.module;

import me.comu.api.registry.Registry;
import me.comu.module.impl.movement.Sprint;

import java.util.concurrent.CopyOnWriteArrayList;

public class ModuleManager extends Registry<Module> {

    public ModuleManager() {
        registry = new CopyOnWriteArrayList<>();

        // Active

        // Combat
        Sprint sprint = new Sprint();
        register(sprint);
        sprint.setEnabled(true);

        // Exploits

        // Miscellaneous

        // Movement

        // Render

        // World
    }

    public Module getModuleByName(String name) {
        return registry.stream()
                .filter(module -> module.getAliases().stream()
                        .anyMatch(alias -> alias.equalsIgnoreCase(name)))
                .findFirst()
                .orElse(null);
    }
}
