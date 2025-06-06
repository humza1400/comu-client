package me.comu.command;

import me.comu.Comu;
import me.comu.api.registry.Registry;
import me.comu.command.commands.client.*;
import me.comu.command.commands.client.Module;
import me.comu.logging.Logger;

import java.util.ArrayList;

public class CommandManager extends Registry<Command> {

    private String prefix = ".";

    public CommandManager() {
        registry = new ArrayList<>();
        // Client
        register(new Bind());
        register(new Toggle());
        register(new Help());
        register(new Prefix());
        register(new Module());
        register(new Modules());
        // Network

        // Player

        // Server
    }


    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public boolean tryDispatch(String message) {
        final String[] arguments = message.trim().split(" ");

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

        String[] args = message.split(" ");
        String commandName = args[0].substring(getPrefix().length());

        me.comu.module.Module matchedModule = Comu.getInstance().getModuleManager().getModuleByName(commandName);

        if (matchedModule != null) {
            Command moduleCommand = Comu.getInstance().getCommandManager().getCommandByName("module");
            if (moduleCommand != null) {
                String[] redirectedArgs = new String[args.length + 1];
                redirectedArgs[0] = "module";
                System.arraycopy(args, 0, redirectedArgs, 1, args.length);

                String response = moduleCommand.dispatch(redirectedArgs);
                Logger.getLogger().printToChat(applyDefaultColor(response));
                return true;
            }
        }

        Logger.getLogger().printToChat("Invalid command.");
        return true;
    }

    public Command getCommandByName(String name) {
        return registry.stream()
                .filter(command ->
                        command.getName().equalsIgnoreCase(name) ||
                                command.getAliases().stream().anyMatch(alias -> alias.equalsIgnoreCase(name)))
                .findFirst()
                .orElse(null);
    }

    private static String applyDefaultColor(String message) {
        if (message.matches("(?i).*(&|§)[0-9a-frlnok].*")) {
            return message;
        }
        return "&7" + message;
    }
}
