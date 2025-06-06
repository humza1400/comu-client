package me.comu.property;

import java.util.List;

public abstract class Property<T> {
    protected final String name;
    protected final List<String> aliases;
    protected T value;

    public Property(String name, List<String> aliases, T defaultValue) {
        this.name = name;
        this.aliases = aliases;
        this.value = defaultValue;
    }

    public String getName() {
        return name;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public abstract void toggle();
}
