package me.comu.property.properties;

import me.comu.property.Property;
import java.util.List;

public class InputProperty extends Property<String> {

    public InputProperty(String name, List<String> aliases, String defaultValue) {
        super(name, aliases, defaultValue);
    }

    public void setValue(String newValue) {
        this.value = newValue;
    }

    public void append(String addition) {
        this.value += addition;
    }

    public boolean isEmpty() {
        return this.value == null || this.value.trim().isEmpty();
    }

    public String getFormattedValue() {
        return "\"" + this.value + "\"";
    }

    public void clear() {
        this.value = "";
    }

    @Override
    public void toggle() {
        ;
    }
}

