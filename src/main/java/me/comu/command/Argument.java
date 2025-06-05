package me.comu.command;

public class Argument {
    private final String label;
    private String value;

    public Argument(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
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
