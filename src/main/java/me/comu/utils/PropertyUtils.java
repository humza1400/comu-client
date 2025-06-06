package me.comu.utils;

import me.comu.property.Property;
import me.comu.property.properties.EnumProperty;

@SuppressWarnings("unchecked")
public class PropertyUtils {

    public static Object parseValue(Property<?> prop, String input) {
        Object current = prop.getValue();

        try {
            if (current instanceof Boolean)
                return Boolean.parseBoolean(input);
            if (current instanceof Integer)
                return Integer.parseInt(input);
            if (current instanceof Float)
                return Float.parseFloat(input);
            if (current instanceof Double)
                return Double.parseDouble(input);
            if (current instanceof Long)
                return Long.parseLong(input);
            if (current instanceof String)
                return input;
            if (prop instanceof EnumProperty<?> enumProp) {
                enumProp.setValueByName(input);
                return enumProp.getValue();
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid value for " + prop.getName() + ": " + input);
        }

        throw new IllegalArgumentException("Unsupported property type: " + current.getClass());
    }

    public static <T> void safelySet(Property<?> prop, T value) {
        ((Property<T>) prop).setValue(value);
    }

    public static String serializeValue(Property<?> prop) {
        Object val = prop.getValue();
        if (val instanceof Enum<?>)
            return ((Enum<?>) val).name();
        return val.toString();
    }

    public static String getFormattedPropertyValue(Property<?> prop) {
        Object value = prop.getValue();
        if (prop instanceof EnumProperty<?> ep) {
            return ep.getFormattedValue();
        } else if (value instanceof String) {
            return "\"" + value + "\"";
        } else {
            return value.toString();
        }
    }

}
