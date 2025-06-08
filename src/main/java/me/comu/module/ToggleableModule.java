package me.comu.module;

import me.comu.Comu;
import me.comu.keybind.Keybind;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class ToggleableModule extends Module {
    private boolean enabled;
    private boolean drawn = true;
    private final Keybind keybind;

    public ToggleableModule(String name, List<String> aliases, Category category, String description) {
        super(name, aliases, category, description);
        this.keybind = new Keybind(name, GLFW.GLFW_KEY_UNKNOWN) {
            @Override
            public void onPress() {
                toggle();
            }
        };

        Comu.getInstance().getKeybindManager().register(keybind);
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
        if (this.enabled == enabled)
            return;

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

    public Keybind getKeybind() {
        return keybind;
    }
}
