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

public class Prefix extends Command {
    public Prefix() {
        super(List.of("prefix"), List.of(
                new Argument("prefix")
        ), CommandType.CLIENT);
    }

    @Override
    public String dispatch() {
        String prefix = getArgument("prefix").getValue();
        Comu.getInstance().getCommandManager().setPrefix(prefix);
        return String.format("&e%s&7 is now your prefix.", prefix);
    }
}
