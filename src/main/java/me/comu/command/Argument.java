package me.comu.command;

public class Argument {
    private final String label;
    private final boolean optional;
    private String value;

    public Argument(String label) {
        this(label, false);
    }

    public Argument(String label, boolean optional) {
        this.label = label;
        this.optional = optional;
    }

    public String getLabel() {
        return label;
    }

    public boolean isOptional() {
        return optional;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isPresent() {
        return value != null && !value.isEmpty();
    }
}
