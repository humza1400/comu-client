package me.comu.command.commands.client;

import me.comu.Comu;
import me.comu.command.Argument;
import me.comu.command.Command;
import me.comu.command.CommandType;

import java.util.List;

public final class Enemy {

    public static class Add extends Command {
        public Add() {
            super(List.of("enemy", "eadd", "enemyadd", "addenemy"), List.of(new Argument("username"), new Argument("alias", true)), CommandType.CLIENT);
        }

        @Override
        public String dispatch() {
            String name = getArgument("username").getValue();
            String alias = getArgument("alias").getValue();

            if (Comu.getInstance().getStaffManager().isStaff((name))) {
                return "That user is already your enemy.";
            }

            boolean hasAlias = alias != null;
            Comu.getInstance().getStaffManager().add(name, hasAlias ? alias : name);

            return hasAlias ? String.format("Added enemy &e%s &7with alias &e%s&7.", name, alias) : String.format("Added enemy &e%s&7.", name);
        }
    }

    public static class Remove extends Command {
        public Remove() {
            super(List.of("delenemy", "enemydel", "erem", "enemyremove", "eremove", "deleteenemy", "enemydelete"), List.of(new Argument("username/alias")), CommandType.CLIENT);
        }

        @Override
        public String dispatch() {
            String name = getArgument("username/alias").getValue();

            if (!Comu.getInstance().getStaffManager().isStaff(name)) {
                return "That user is not an enemy.";
            }

            me.comu.people.staff.Staff staff = Comu.getInstance().getStaffManager().getStaffByNameOrAlias(name);
            String oldAlias = staff.getAlias();
            Comu.getInstance().getStaffManager().remove(name);

            return String.format("Removed enemy with alias &e%s&7.", oldAlias);
        }
    }
}
