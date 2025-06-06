package me.comu.property.properties;

import me.comu.property.Property;

import java.util.List;

public class BooleanProperty extends Property<Boolean> {
    public BooleanProperty(String name, List<String> aliases, boolean defaultValue) {
        super(name, aliases, defaultValue);
    }

    @Override
    public void toggle() {
        value = !value;
    }
}
