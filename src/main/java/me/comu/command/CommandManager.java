package me.comu.command;

import me.comu.api.registry.Registry;
import me.comu.command.commands.Bind;
import me.comu.command.commands.Toggle;
import me.comu.logging.Logger;

import java.util.ArrayList;

public class CommandManager extends Registry<Command> {

    private String prefix = ".";

    public CommandManager() {
        registry = new ArrayList<>();
        register(new Bind());
        register(new Toggle());
    }


    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public boolean tryDispatch(String message) {
        final String[] arguments = message.trim().split(" ");
        if (arguments.length < 0) return false;

        final String label = arguments[0].substring(prefix.length());
        for (Command command : registry) {
            for (String alias : command.getAliases()) {
                if (label.equals(alias)) {
                    String result = command.dispatch(arguments);
                    Logger.getLogger().printToChat(result);
                    return true;
                }
            }
        }

        Logger.getLogger().printToChat("Invalid command.");
        return true;
    }
}
