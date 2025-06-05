package me.comu.keybind;

public abstract class Keybind {
    private final String label;
    private int key;

    public Keybind(String label, int key) {
        this.label = label;
        this.key = key;
    }

    public String getLabel() {
        return label;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public abstract void onPress();
}
