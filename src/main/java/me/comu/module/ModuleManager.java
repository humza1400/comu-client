package me.comu.module;

import me.comu.api.registry.Registry;
import me.comu.module.impl.combat.KillAura;
import me.comu.module.impl.movement.Fly;
import me.comu.module.impl.movement.Speed;
import me.comu.module.impl.movement.Sprint;
import me.comu.module.impl.movement.Velocity;
import me.comu.module.impl.render.HUD;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ModuleManager extends Registry<Module> {

    public ModuleManager() {
        registry = new CopyOnWriteArrayList<>();

        // Active

        // Combat
        register(new KillAura());

        // Exploits

        // Miscellaneous

        // Movement
        register(new Sprint());
        register(new Speed());
        register(new Fly());
        register(new Velocity());

        // Render
        register(new HUD());
        // World
    }

    public Module getModuleByName(String name) {
        return registry.stream()
                .filter(module ->
                        module.getName().equalsIgnoreCase(name) ||
                                module.getDisplayName().equalsIgnoreCase(name) ||
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

    public Module getConflictForRename(Module target, String newName) {
        for (Module module : registry) {
            if (module == target) continue;

            boolean conflictsWithIdOrDisplayName =
                    module.getName().equalsIgnoreCase(newName) ||
                            module.getDisplayName().equalsIgnoreCase(newName);

            boolean conflictsWithAlias =
                    module.getAliases().stream().anyMatch(alias -> alias.equalsIgnoreCase(newName));

            if (conflictsWithIdOrDisplayName || conflictsWithAlias) {
                return module;
            }
        }

        return null;
    }

}
