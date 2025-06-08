package me.comu.command.commands.client;

import me.comu.Comu;
import me.comu.command.Argument;
import me.comu.command.Command;
import me.comu.command.CommandType;
import me.comu.module.Module;

import java.util.List;

public class Rename extends Command {
    public Rename() {
        super(List.of("rename"), List.of(new Argument("module"), new Argument("name", true)), CommandType.CLIENT);
    }

    @Override
    public String dispatch() {
        String moduleNameToParse = getArgument("module").getValue();

        Module module = Comu.getInstance().getModuleManager().getModuleByName(moduleNameToParse);
        if (module == null) {
            return "Module not found";
        }
        String nameToParse = getArgument("name").getValue();
        if (nameToParse == null) {
            module.resetDisplayName();
            return String.format("Module name reset to &e%s&7.", module.getName());
        }

        Module conflictModule = Comu.getInstance().getModuleManager().getConflictForRename(module, nameToParse);
        if (conflictModule == null) {
            String oldName = module.getDisplayName();
            module.setDisplayName(nameToParse);
            return String.format("&e%s &7renamed to &e%s&7.", oldName, nameToParse);
        } else {
            return String.format("There is already a module named &e%s&7.", conflictModule.getDisplayName());
        }
    }
}
