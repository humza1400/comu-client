package me.comu.command.commands.client;

import me.comu.Comu;
import me.comu.command.Argument;
import me.comu.command.Command;
import me.comu.command.CommandType;
import me.comu.module.Module;
import me.comu.module.ToggleableModule;
import me.comu.keybind.Keybind;
import org.lwjgl.glfw.GLFW;

import java.util.Comparator;
import java.util.List;
import java.util.StringJoiner;

public class Modules extends Command {
    public Modules() {
        super(List.of("modules","mods"), List.of(), CommandType.CLIENT);
    }

    @Override
    public String dispatch() {
        StringJoiner stringJoiner = new StringJoiner(", ");
        List<Module> modules = Comu.getInstance().getModuleManager().getRegistry();
        modules.sort(Comparator.comparing(Module::getName));
        modules.forEach(module ->
        {
            if (module instanceof ToggleableModule toggleableModule)
            {
                stringJoiner.add(String.format("%s%s&7", toggleableModule.isEnabled() ?  "&a" : "&c", toggleableModule.getName()));
            }
        });
        return String.format("Modules (%s) %s", Comu.getInstance().getModuleManager().getRegistry().size(), stringJoiner);
    }
}
