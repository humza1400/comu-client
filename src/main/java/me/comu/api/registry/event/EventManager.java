package me.comu.api.registry.event;

import me.comu.api.registry.event.listener.Listener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventManager implements IEventManager {

    private final List<Listener<? extends Event>> listeners = new CopyOnWriteArrayList<>();

    @Override
    public void register(Listener<?> listener) {
        if (listener == null || listeners.contains(listener))
            return;
        listeners.add(listener);
    }


    @Override
    public void unregister(Listener<?> listener) {
        if (listener != null)
            listeners.remove(listener);
    }


    @Override
    public void clear() {
        listeners.clear();
    }

    @Override
    public void dispatch(Event event) {
        for (Listener<? extends Event> listener : listeners) {
            if (listener.getEventClass().isAssignableFrom(event.getClass())) {
                dispatchSafely(listener, event);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends Event> void dispatchSafely(Listener<? extends Event> listener, Event event) {
        ((Listener<T>) listener).call((T) event);
    }

    @Override
    public List<Listener<? extends Event>> getListeners() {
        return listeners;
    }

}
