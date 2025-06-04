package me.comu.api.registry.event;

import me.comu.api.registry.event.listener.Listener;

import java.util.List;

public interface IEventManager {

    void register(Listener<?> listener);

    void unregister(Listener<?> listener);

    void clear();

    void dispatch(Event event);

    List<Listener<?>> getListeners();
}
