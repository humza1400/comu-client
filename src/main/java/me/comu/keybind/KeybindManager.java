package me.comu.keybind;

import me.comu.Comu;
import me.comu.api.registry.Registry;
import me.comu.api.registry.event.listener.Listener;
import me.comu.events.InputEvent;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

public class KeybindManager extends Registry<Keybind> {

    public KeybindManager() {
        registry = new ArrayList<>();
        Comu.getInstance().getEventManager().register(new Listener<>(InputEvent.class) {
            @Override
            public void call(InputEvent event) {
                if (event.getType() == InputEvent.Type.KEYBOARD_KEY_PRESS) {
                    registry.forEach(keybind -> {
                        if (keybind.getKey() != GLFW.GLFW_KEY_UNKNOWN && keybind.getKey() == event.getKey()) {
                            keybind.onPress();
                        }
                    });
                }
            }
        });
    }

    public Keybind getKeybindByLabel(String label) {
        for (Keybind keybind : registry) {
            if (keybind.getLabel().equalsIgnoreCase(label)) {
                return keybind;
            }
        }
        return null;
    }
}
