package me.comu.macro;

import net.minecraft.client.MinecraftClient;

public class MacroAction {
    private final String action;

    public MacroAction(String action) {
        this.action = action;
    }

    public void dispatch() {
        if (MinecraftClient.getInstance().getNetworkHandler() == null) return;
        MinecraftClient.getInstance().getNetworkHandler().sendChatMessage(action);
    }

    public final String getAction() {
        return this.action;
    }
}