package me.comu.property.properties;

import me.comu.property.Property;

import java.util.List;

@SuppressWarnings("unchecked")
public class NumberProperty<T extends Number> extends Property<T> {
    private final T min;
    private final T max;
    private final T step;

    public NumberProperty(String name, List<String> aliases, T defaultValue, T min, T max, T step) {
        super(name, aliases, defaultValue);
        this.min = min;
        this.max = max;
        this.step = step;
    }

    @Override
    public void setValue(T value) {
        super.setValue(clamp(value));
    }

    public void setOverrideValue(T value) {
        super.setValue(value);
    }

    public void increment() {
        setValue(add(value, step));
    }

    public void decrement() {
        setValue(subtract(value, step));
    }

    private T add(T a, T b) {
        if (a instanceof Integer)
            return (T) (Integer) (a.intValue() + b.intValue());
        if (a instanceof Float)
            return (T) (Float) (a.floatValue() + b.floatValue());
        if (a instanceof Double)
            return (T) (Double) (a.doubleValue() + b.doubleValue());
        if (a instanceof Long)
            return (T) (Long) (a.longValue() + b.longValue());
        return a;
    }


    private T subtract(T a, T b) {
        if (a instanceof Integer)
            return (T) (Integer) (a.intValue() - b.intValue());
        if (a instanceof Float)
            return (T) (Float) (a.floatValue() - b.floatValue());
        if (a instanceof Double)
            return (T) (Double) (a.doubleValue() - b.doubleValue());
        if (a instanceof Long)
            return (T) (Long) (a.longValue() - b.longValue());
        return a;
    }


    private T clamp(T val) {
        if (val instanceof Integer)
            return (T) (Integer) Math.max(min.intValue(), Math.min(max.intValue(), val.intValue()));
        if (val instanceof Float)
            return (T) (Float) Math.max(min.floatValue(), Math.min(max.floatValue(), val.floatValue()));
        if (val instanceof Double)
            return (T) (Double) Math.max(min.doubleValue(), Math.min(max.doubleValue(), val.doubleValue()));
        return val;
    }

    public T getMin() {
        return min;
    }

    public T getMax() {
        return max;
    }

    public T getStep() {
        return step;
    }

    @Override
    public void toggle() {
        ;
    }
}
