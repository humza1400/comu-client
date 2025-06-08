package me.comu.api.registry.event;

import me.comu.Comu;
import me.comu.logging.Logger;

public abstract class Event {
    private boolean cancelled;

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public void dispatch() {
        IEventManager eventManager = Comu.getInstance().getEventManager();
        if (eventManager != null) {
            eventManager.dispatch(this);
        } else Logger.getLogger().print("Event tried to dispatch before EventManager was initialized.", Logger.LogType.ERROR);
    }
}
