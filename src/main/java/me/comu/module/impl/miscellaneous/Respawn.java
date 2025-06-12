package me.comu.module.impl.miscellaneous;

import me.comu.api.registry.event.listener.Listener;
import me.comu.events.TickEvent;
import me.comu.module.Category;
import me.comu.module.ToggleableModule;

import java.util.List;

public class Respawn extends ToggleableModule {

    public Respawn() {
        super("Respawn", List.of("autorespawn"), Category.MISCELLANEOUS, "Automatically respawns for you when you die");
        listeners.add(new Listener<>(TickEvent.class) {
            @Override
            public void call(TickEvent event) {
                if (mc.player == null || mc.world == null) return;
                if (mc.player.isDead()) mc.player.requestRespawn();
            }
        });
    }
}
