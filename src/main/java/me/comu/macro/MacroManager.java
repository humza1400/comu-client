package me.comu.macro;


import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

import java.util.*;

public final class MacroManager {

    private final List<Macro> macros = new ArrayList<>();
    private final Map<Integer, Boolean> keyStates = new HashMap<>();

    public MacroManager() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            for (Macro macro : macros) {
                int key = macro.getKey();
                if (key == -1) continue; // Invalid key

                boolean isPressed = org.lwjgl.glfw.GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), key) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
                boolean wasPressed = keyStates.getOrDefault(key, false);

                // If the key was just pressed this tick
                if (isPressed && !wasPressed) {
                    macro.dispatch();
                }

                keyStates.put(key, isPressed);
            }
        });
    }

    public void addMacro(Macro macro) {
        macros.add(macro);
    }

    public void removeMacro(Macro macro) {
        macros.remove(macro);
    }

    public Macro getUsingKey(int key) {
        for (Macro macro : macros) {
            if (macro.getKey() == key)
                return macro;
        }
        return null;
    }

    public boolean isMacro(int key) {
        return macros.stream().anyMatch(m -> m.getKey() == key);
    }

    public void remove(int key) {
        macros.removeIf(macro -> macro.getKey() == key);
    }

    public void resetMacros() {
        macros.clear();
        keyStates.clear();
    }

    public List<Macro> getMacros() {
        return macros;
    }
}