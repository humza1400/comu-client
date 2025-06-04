package me.comu.api.registry.event.listener;

import me.comu.api.registry.event.Event;

public abstract class Listener<T extends Event> {

    private final Class<T> eventClass;

    public Listener(Class<T> eventClass) {
        this.eventClass = eventClass;
    }

    public Class<T> getEventClass() {
        return eventClass;
    }

    public abstract void call(T event);
}