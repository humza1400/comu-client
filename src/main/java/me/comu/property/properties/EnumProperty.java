package me.comu.property.properties;

import me.comu.property.Property;

import java.util.Arrays;
import java.util.List;

public class EnumProperty<T extends Enum<T>> extends Property<T> {
    private final T[] values;

    public EnumProperty(String name, List<String> aliases, T defaultValue) {
        super(name, aliases, defaultValue);
        this.values = defaultValue.getDeclaringClass().getEnumConstants();
    }

    public void increment() {
        int idx = (value.ordinal() + 1) % values.length;
        value = values[idx];
    }

    public void decrement() {
        int idx = (value.ordinal() - 1 + values.length) % values.length;
        value = values[idx];
    }

    public String getFormattedValue() {
        String raw = value.name().replace('_', ' ');
        return Character.toUpperCase(raw.charAt(0)) + raw.substring(1).toLowerCase();
    }

    public List<T> getValues() {
        return Arrays.asList(values);
    }

    public void setValueByName(String name) {
        for (T val : values) {
            if (val.name().equalsIgnoreCase(name)) {
                setValue(val);
                return;
            }
        }
        throw new IllegalArgumentException("Invalid enum name: " + name);
    }

    @Override
    public void toggle() {
        increment();
    }
}
