package me.comu.command.commands.client;

import me.comu.Comu;
import me.comu.command.Argument;
import me.comu.command.Command;
import me.comu.command.CommandType;
import me.comu.module.Module;
import me.comu.module.ToggleableModule;

import java.util.List;

public class Toggle extends Command {
    public Toggle() {
        super(List.of("toggle","t"), List.of(
                new Argument("module")
        ), CommandType.CLIENT);
    }

    @Override
    public String dispatch() {
        String moduleName = getArgument("module").getValue();

        Module module = Comu.getInstance().getModuleManager().getModuleByName(moduleName);

        if (module == null) {
            return "\247cNo such module exists.";
        }

        if (!(module instanceof ToggleableModule toggleableModule)) {
            return "\247cThat module is not toggleable.";
        }

        toggleableModule.toggle();
        return String.format("&e%s&7 has been %s&7.", toggleableModule.getName(), toggleableModule.isEnabled() ? "&aenabled" : "&cdisabled");
    }
}
