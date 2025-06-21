package me.comu.command.commands.player;

import me.comu.command.Argument;
import me.comu.command.Command;
import me.comu.command.CommandType;

import java.util.List;

public class VClip extends Command {
    public VClip() {
        super(List.of("vclip", "vc"), List.of(new Argument("blocks")), CommandType.CLIENT);
    }

    @Override
    public String dispatch() {
        double blocks = Double.parseDouble(getArgument("blocks").getValue());
        mc.player.setPosition(mc.player.getX(), mc.player.getY() + blocks, mc.player.getZ());
        String direction = blocks < 0 ? "down" : "up";
        String plural = Math.abs(blocks) == 1.0 ? "" : "s";
        return String.format("Teleported %s &e%s&7 block%s.", direction, blocks, plural);
    }
}
