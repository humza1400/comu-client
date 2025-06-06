package me.comu.command.commands.client;

import me.comu.Comu;
import me.comu.command.Argument;
import me.comu.command.Command;
import me.comu.command.CommandType;
import me.comu.module.Module;
import me.comu.module.ToggleableModule;
import me.comu.keybind.Keybind;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class Bind extends Command {
    public Bind() {
        super(List.of("bind"), List.of(
                new Argument("module"),
                new Argument("key")
        ), CommandType.CLIENT);
    }

    @Override
    public String dispatch() {
        String moduleName = getArgument("module").getValue();
        String keyName = getArgument("key").getValue();

        Module module = Comu.getInstance().getModuleManager().getModuleByName(moduleName);

        if (module == null) {
            return "\247cNo such module exists.";
        }

        if (!(module instanceof ToggleableModule toggleableModule)) {
            return "\247cThat module is not toggleable.";
        }

        Keybind keybind = Comu.getInstance().getKeybindManager().getKeybindByLabel(toggleableModule.getName());
        if (keyName.equalsIgnoreCase("none")) {
            keybind.setKey(GLFW.GLFW_KEY_UNKNOWN);
            return "\247e" + toggleableModule.getName() + " \2477has been unbound.";
        }

        String readableName = keyName.toUpperCase();
        int key = getKeyCodeByName(readableName);

        if (key == GLFW.GLFW_KEY_UNKNOWN) {
            return "\247cInvalid key: " + keyName;
        }

        if (keybind == null) {
            return "\247cNo keybind found for that module.";
        }

        keybind.setKey(key);
        return String.format("\247e%s \2477has been bound to \247e%s\2477.", toggleableModule.getName(), getKeyName(key).toUpperCase());
    }

    private int getKeyCodeByName(String name) {
        try {
            return (int) GLFW.class.getField("GLFW_KEY_" + name).get(null);
        } catch (Exception ignored) {
            return GLFW.GLFW_KEY_UNKNOWN;
        }
    }

    private String getKeyName(int keyCode) {
        for (var field : GLFW.class.getFields()) {
            try {
                if (field.getName().startsWith("GLFW_KEY_") && (int) field.get(null) == keyCode) {
                    return field.getName().replace("GLFW_KEY_", "");
                }
            } catch (Exception ignored) {}
        }
        return "UNKNOWN";
    }
}
