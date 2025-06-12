package me.comu.events;

import me.comu.api.registry.event.Event;

public class InputEvent extends Event {
    private final Type type;
    private final int key;

    public InputEvent(Type type, int key) {
        this.type = type;
        this.key = key;
    }

    public int getKey() {
        return key;
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        KEYBOARD_KEY_PRESS, MOUSE_LEFT_CLICK, MOUSE_RIGHT_CLICK, MOUSE_MIDDLE_CLICK, MOUSE_SCROLL_UP, MOUSE_SCROLL_DOWN, MOUSE_BUTTON_4_CLICK, MOUSE_BUTTON_5_CLICK, MOUSE_EXTRA_BUTTON
    }
}
