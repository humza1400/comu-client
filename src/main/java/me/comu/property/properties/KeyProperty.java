package me.comu.property.properties;

import me.comu.property.Property;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Objects;

public class KeyProperty extends Property<Integer> {
    private String keyName;

    public KeyProperty(String name, List<String> aliases, int defaultKeyCode) {
        super(name, aliases, defaultKeyCode);
        this.keyName = glfwKeyName(defaultKeyCode);
    }

    @Override
    public void toggle() {

    }

    public int getKeyCode() {
        return value;
    }

    public void setKeyCode(int keyCode) {
        this.value = keyCode;
        this.keyName = glfwKeyName(keyCode);
    }

    public String getKeyName() {
        return keyName;
    }

    public boolean isPressed() {
        return org.lwjgl.glfw.GLFW.glfwGetKey(
                org.lwjgl.glfw.GLFW.glfwGetCurrentContext(),
                value
        ) == GLFW.GLFW_PRESS;
    }

    private String glfwKeyName(int keyCode) {
        return switch (keyCode) {
            case GLFW.GLFW_KEY_LEFT_SHIFT -> "Left Shift";
            case GLFW.GLFW_KEY_RIGHT_SHIFT -> "Right Shift";
            case GLFW.GLFW_KEY_LEFT_CONTROL -> "Left Ctrl";
            case GLFW.GLFW_KEY_RIGHT_CONTROL -> "Right Ctrl";
            case GLFW.GLFW_KEY_LEFT_ALT -> "Left Alt";
            case GLFW.GLFW_KEY_RIGHT_ALT -> "Right Alt";
            case GLFW.GLFW_KEY_ESCAPE -> "Escape";
            case GLFW.GLFW_KEY_ENTER -> "Enter";
            case GLFW.GLFW_KEY_SPACE -> "Space";
            case GLFW.GLFW_KEY_BACKSPACE -> "Backspace";
            default -> GLFW.glfwGetKeyName(keyCode, 0) != null
                    ? Objects.requireNonNull(GLFW.glfwGetKeyName(keyCode, 0)).toUpperCase()
                    : "Unknown";
        };
    }
}
