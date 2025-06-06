package me.comu.command.commands.client;

import me.comu.Comu;
import me.comu.command.Argument;
import me.comu.command.Command;
import me.comu.command.CommandType;

import java.util.*;
import java.util.stream.Collectors;

public class Help extends Command {

    public Help() {
        super(List.of("help", "halp", "autism", "how"), List.of(new Argument("category", true)), CommandType.CLIENT);
    }

    @Override
    public String dispatch() {
        var registry = Comu.getInstance().getCommandManager().getRegistry();
        Map<CommandType, List<Command>> grouped = registry.stream()
                .collect(Collectors.groupingBy(Command::getCommandType));

        Argument categoryArg = getArgument("category");
        String filterValue = categoryArg != null ? categoryArg.getValue() : null;
        if (filterValue != null) {
            String filter = filterValue.toUpperCase();
            try {
                CommandType type = CommandType.valueOf(filter);
                List<Command> filtered = grouped.getOrDefault(type, List.of())
                        .stream()
                        .sorted(Comparator.comparing(c -> c.getAliases().getFirst()))
                        .toList();

                if (filtered.isEmpty())
                    return String.format("&e%s &7category has no commands.", type.getName());

                StringBuilder sb = new StringBuilder("&e" + type.getName() + " Commands:\n");
                for (Command command : filtered) {
                    sb.append(" &c").append(Comu.getInstance().getCommandManager().getPrefix()).append(command.getAliases().getFirst()).append(" &7• &f")
                            .append(command.getSyntax()).append("\n");
                }

                if (sb.toString().endsWith("\n")) {
                    sb.setLength(sb.length() - 1);
                }
                return sb.toString();
            } catch (IllegalArgumentException e) {
                return "&cUnknown command category: &e" + filter;
            }
        }

        StringBuilder result = new StringBuilder();
        grouped.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    CommandType type = entry.getKey();
                    List<Command> commands = entry.getValue().stream()
                            .sorted(Comparator.comparing(c -> c.getAliases().getFirst()))
                            .toList();

                    result.append("&e").append(type.getName()).append(" Commands:\n");
                    for (Command command : commands) {
                        result.append(" &c").append(Comu.getInstance().getCommandManager().getPrefix()).append(command.getAliases().getFirst())
                                .append(" &7• &f").append(command.getSyntax()).append("\n");
                    }
                });

        if (result.toString().endsWith("\n")) {
            result.setLength(result.length() - 1);
        }

        return result.toString();
    }
}
