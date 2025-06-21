package me.comu.command;

import me.comu.Comu;
import net.minecraft.client.MinecraftClient;

import java.util.List;
import java.util.stream.Collectors;

public abstract class Command {
    private final List<String> aliases;
    private final List<Argument> arguments;
    private final CommandType commandType;

    protected MinecraftClient mc = MinecraftClient.getInstance();

    protected Command(List<String> aliases, List<Argument> arguments, CommandType commandType) {
        this.aliases = aliases;
        this.arguments = arguments;
        this.commandType = commandType;
    }

    public boolean matches(String inputAlias) {
        return aliases.stream().anyMatch(alias -> alias.equalsIgnoreCase(inputAlias));
    }

    public final List<Argument> getArguments() {
        return arguments;
    }

    public Argument getArgument(String alias) {
        for (Argument argument : arguments) {
            if (alias.equalsIgnoreCase(argument.getLabel())) {
                return argument;
            }
        }

        return null;
    }

    public String getName() {
        return aliases.getFirst();
    }

    public List<String> getAliases() {
        return aliases;
    }

    public String getSyntax() {
        return "\2477" + aliases.getFirst() + " " + arguments.stream().map(arg -> arg.isOptional() ? "\2478<" + arg.getLabel() + ">" : "\247e<" + arg.getLabel() + ">").collect(Collectors.joining(" "));
    }

    public final String dispatch(String[] input) {
        long requiredArgs = arguments.stream().filter(arg -> !arg.isOptional()).count();

        if (input.length - 1 < requiredArgs) {
            return Comu.getInstance().getCommandManager().getPrefix() + getSyntax();
        } else if (input.length - 1 > arguments.size()) {
            return "\247cToo many arguments. Max: \247e" + arguments.size() + "\247c.";
        }

        for (int i = 0; i < arguments.size(); i++) {
            arguments.get(i).setValue(i + 1 < input.length ? input[i + 1] : null);
        }

        boolean allValid = arguments.stream().filter(arg -> !arg.isOptional()).allMatch(Argument::isPresent);
        if (!allValid) {
            return "\247cInvalid argument(s).";
        }

        try {
            return dispatch();
        } catch (Exception ex) {
            return "\247cError executing command: " + ex.getMessage();
        }
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public abstract String dispatch();
}
