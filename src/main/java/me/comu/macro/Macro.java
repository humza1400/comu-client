package me.comu.macro;

import net.minecraft.client.MinecraftClient;

import java.util.Objects;

public class Macro {
    private final int key;

    private final MacroAction action;

    public Macro(int key, String action) {
        this.key = key;
        this.action = new MacroAction(action);
    }

    public final int getKey() {
        return this.key;
    }

    public final MacroAction getAction() {
        return this.action;
    }

    public void dispatch() {

        String processed = action.getAction().replaceAll("_", " ").replace("\\", "_");

        Objects.requireNonNull(MinecraftClient.getInstance().getNetworkHandler()).sendChatMessage(processed);
    }

}