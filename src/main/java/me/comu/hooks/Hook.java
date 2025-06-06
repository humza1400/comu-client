package me.comu.hooks;

import me.comu.Comu;
import me.comu.events.TickEvent;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public final class Hook {

    private Hook() {}

    public static void init() {
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            Comu.getInstance().getEventManager().dispatch(new TickEvent(TickEvent.Phase.PRE));
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            Comu.getInstance().getEventManager().dispatch(new TickEvent(TickEvent.Phase.POST));
        });
    }
}
