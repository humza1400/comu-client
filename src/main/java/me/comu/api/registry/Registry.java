package me.comu.api.registry;

import java.util.ArrayList;
import java.util.List;

public class Registry<T> {
    protected List<T> registry;

    public Registry() {
        this.registry = new ArrayList<T>();
    }

    public void register(T t) {
        registry.add(t);
    }

    public void unregister(T t) {
        registry.remove(t);
    }

    public void clear() {
        registry.clear();
    }

    public List<T> getRegistry() {
        return registry;
    }
}
