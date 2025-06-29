package me.comu.command.commands.client;

import me.comu.Comu;
import me.comu.command.Argument;
import me.comu.command.Command;
import me.comu.command.CommandType;

import java.util.List;

public class Prefix extends Command {
    public Prefix() {
        super(List.of("prefix"), List.of(new Argument("prefix")), CommandType.CLIENT);
    }

    @Override
    public String dispatch() {
        String prefix = getArgument("prefix").getValue();
        Comu.getInstance().getCommandManager().setPrefix(prefix);
        return String.format("&e%s&7 is now your prefix.", prefix);
    }
}
