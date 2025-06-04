package me.comu.module;

import me.comu.Comu;

import java.util.List;

public class ToggleableModule extends Module {
    private boolean enabled;
    private boolean drawn = true;

    public ToggleableModule(String name, List<String> aliases, Category category, String description) {
        super(name, aliases, category, description);
    }

    public void onEnable() {
        listeners.forEach(listener -> Comu.getInstance().getEventManager().register(listener));
    }

    public void onDisable() {
        listeners.forEach(listener -> {
            Comu.getInstance().getEventManager().unregister(listener);
        });
    }

    public void toggle() {
        setEnabled(!enabled);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (enabled) onEnable();
        else onDisable();
    }

    public void setDrawn(boolean drawn) {
        this.drawn = drawn;
    }

    public boolean isDrawn() {
        return drawn;
    }
}
