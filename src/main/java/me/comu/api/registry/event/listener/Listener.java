package me.comu.api.registry.event.listener;

import me.comu.api.registry.event.Event;

public abstract class Listener<T extends Event> {

    private final Class<T> eventClass;
    private boolean alwaysOn = false;

    public Listener(Class<T> eventClass, boolean alwaysOn) {
        this.eventClass = eventClass;
        this.alwaysOn = alwaysOn;
    }

    public Listener(Class<T> eventClass) {
        this.eventClass = eventClass;
    }

    public boolean isAlwaysOn() {
        return alwaysOn;
    }

    public Class<T> getEventClass() {
        return eventClass;
    }

    public abstract void call(T event);
}