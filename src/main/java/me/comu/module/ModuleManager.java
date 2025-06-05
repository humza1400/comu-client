package me.comu.module;

import me.comu.api.registry.Registry;
import me.comu.module.impl.movement.Sprint;
import me.comu.module.impl.render.HUD;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ModuleManager extends Registry<Module> {

    public ModuleManager() {
        registry = new CopyOnWriteArrayList<>();

        // Active

        // Combat
        register(new Sprint());

        // Exploits

        // Miscellaneous

        // Movement

        // Render
        register(new HUD());
        // World
    }

    public Module getModuleByName(String name) {
        return registry.stream()
                .filter(module ->
                        module.getName().equalsIgnoreCase(name) ||
                                module.getAliases().stream().anyMatch(alias -> alias.equalsIgnoreCase(name)))
                .findFirst()
                .orElse(null);
    }


    public List<Module> getModules() {
        return registry;
    }

    public List<ToggleableModule> getToggleableModules() {
        return registry.stream()
                .filter(module -> module instanceof ToggleableModule)
                .map(module -> (ToggleableModule) module)
                .toList();
    }


}
