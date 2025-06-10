package me.comu.command.commands.client;

import me.comu.Comu;
import me.comu.command.Argument;
import me.comu.command.Command;
import me.comu.command.CommandType;
import me.comu.module.Module;
import me.comu.module.ToggleableModule;

import java.util.List;

public class Visible extends Command {
    public Visible() {
        super(List.of("visible", "hide", "show"), List.of(new Argument("module")), CommandType.CLIENT);
    }

    @Override
    public String dispatch() {
        String moduleNameToParse = getArgument("module").getValue();

        Module module = Comu.getInstance().getModuleManager().getModuleByName(moduleNameToParse);
        if (module == null) {
            return "Module not found";
        }

        if (!(module instanceof ToggleableModule toggleableModule)) {
            return "Module visibility cannot be set for persistent modules.";
        }

        toggleableModule.setDrawn(!toggleableModule.isDrawn());
        return "&e" + module.getDisplayName() + "&7 is now &e" + (toggleableModule.isDrawn() ? "shown" : "hidden") + "&7.";
    }
}
