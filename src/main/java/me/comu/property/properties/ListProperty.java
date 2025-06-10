package me.comu.property.properties;

import me.comu.property.Property;

import java.util.ArrayList;
import java.util.List;

public class ListProperty extends Property<List<Property<?>>> {

    public ListProperty(String name, List<String> aliases, List<Property<?>> defaultValue) {
        super(name, aliases, new ArrayList<>(defaultValue)); // Defensive copy
    }

    @Override
    public String getName() {
        return super.getName();
    }

    public void add(Property<?> property) {
        value.add(property);
    }

    public void remove(Property<?> property) {
        value.remove(property);
    }

    public boolean contains(Property<?> property) {
        return value.contains(property);
    }

    public List<Property<?>> getProperties() {
        return value;
    }

    public Property<?> getPropertyByName(String name) {
        for (Property<?> prop : value) {
            if (prop.getName().equalsIgnoreCase(name) || prop.getAliases().stream().anyMatch(a -> a.equalsIgnoreCase(name)))
                return prop;
        }
        return null;
    }

    public List<BooleanProperty> getBooleanProperties() {
        List<BooleanProperty> result = new ArrayList<>();
        for (Property<?> prop : value) {
            if (prop instanceof BooleanProperty bp) result.add(bp);
        }
        return result;
    }

    public List<InputProperty> getInputProperties() {
        List<InputProperty> result = new ArrayList<>();
        for (Property<?> prop : value) {
            if (prop instanceof InputProperty ip) result.add(ip);
        }
        return result;
    }

    public List<EnumProperty<?>> getEnumProperties() {
        List<EnumProperty<?>> result = new ArrayList<>();
        for (Property<?> prop : value) {
            if (prop instanceof EnumProperty<?> ep) result.add(ep);
        }
        return result;
    }

    public List<NumberProperty<?>> getNumberProperties() {
        List<NumberProperty<?>> result = new ArrayList<>();
        for (Property<?> prop : value) {
            if (prop instanceof NumberProperty<?> np) result.add(np);
        }
        return result;
    }

    public List<ListProperty> getListProperties() {
        List<ListProperty> result = new ArrayList<>();
        for (Property<?> prop : value) {
            if (prop instanceof ListProperty lp) result.add(lp);
        }
        return result;
    }

    @Override
    public void toggle() {
        for (Property<?> prop : value) {
            prop.toggle();
        }
    }
}
