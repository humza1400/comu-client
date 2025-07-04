package me.comu.module;

import me.comu.api.registry.Registry;
import me.comu.module.impl.active.Overlay;
import me.comu.module.impl.combat.AutoArmor;
import me.comu.module.impl.combat.AutoPotion;
import me.comu.module.impl.combat.KillAura;
import me.comu.module.impl.miscellaneous.AntiVanish;
import me.comu.module.impl.miscellaneous.ChatSpammer;
import me.comu.module.impl.miscellaneous.LagDetector;
import me.comu.module.impl.miscellaneous.MiddleClickPerson;
import me.comu.module.impl.movement.*;
import me.comu.module.impl.player.InventoryCleaner;
import me.comu.module.impl.player.PingSpoof;
import me.comu.module.impl.player.Respawn;
import me.comu.module.impl.render.*;
import me.comu.module.impl.world.Timer;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ModuleManager extends Registry<Module> {

    public ModuleManager() {
        registry = new CopyOnWriteArrayList<>();

        // Persistent
        register(new Overlay());

        // Combat
        register(new KillAura());
        register(new AutoPotion());
        register(new AutoArmor());

        // Player
        register(new PingSpoof());

        // Miscellaneous
        register(new ChatSpammer());
        register(new Respawn());
        register(new InventoryCleaner());
        register(new MiddleClickPerson());
        register(new LagDetector());
        register(new AntiVanish());

        // Movement
        register(new Sprint());
        register(new Speed());
        register(new Fly());
        register(new Velocity());
        register(new NoSlowdown());
        register(new InventoryMove());
        register(new NoFall());

        // Render
        register(new HUD());
        register(new Nametags());
        register(new ClickGui());
        register(new TabGui());
        register(new Freecam());
        register(new ViewClip());
        register(new ItemESP());

        // World
        register(new Timer());
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

    public Module getToggleableModuleByName(String name) {
        return registry.stream()
                .filter(module ->
                        module instanceof ToggleableModule &&
                                module.getName().equalsIgnoreCase(name) ||
                                module.getDisplayName().equalsIgnoreCase(name) ||
                                module.getAliases().stream().anyMatch(alias -> alias.equalsIgnoreCase(name)))
                .findFirst()
                .orElse(null);
    }

    @SuppressWarnings("unchecked")
    public <T extends Module> T getModule(Class<T> clazz) {
        return (T) registry.stream()
                .filter(module -> clazz.isAssignableFrom(module.getClass()))
                .findFirst()
                .orElse(null);
    }

    public List<ToggleableModule> getModulesByCategory(Category category) {
        return registry.stream()
                .filter(module -> module instanceof ToggleableModule)
                .map(module -> (ToggleableModule) module)
                .filter(module -> module.getCategory() == category)
                .toList();
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