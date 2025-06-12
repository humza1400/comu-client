package me.comu.command.commands.client;

import me.comu.Comu;
import me.comu.command.Argument;
import me.comu.command.Command;
import me.comu.command.CommandType;

import java.util.List;

public final class Friend {

    public static class Add extends Command {
        public Add() {
            super(List.of("friend", "add", "fadd", "a", "friendadd", "addfriend"), List.of(new Argument("username"), new Argument("alias", true)), CommandType.CLIENT);
        }

        @Override
        public String dispatch() {
            String name = getArgument("username").getValue();
            String alias = getArgument("alias").getValue();

            if (Comu.getInstance().getFriendManager().isFriend(name)) {
                return "That user is already your friend.";
            }

            boolean hasAlias = alias != null;
            Comu.getInstance().getFriendManager().add(name, hasAlias ? alias : name);

            return hasAlias ? String.format("Added friend &e%s &7with alias &e%s&7.", name, alias) : String.format("Added friend &e%s&7.", name);
        }
    }

    public static class Remove extends Command {
        public Remove() {
            super(List.of("delfriend", "remove", "rem", "friendremove", "removefriend", "remfriend", "friendrem", "frem", "deletefriend", "frienddelete"), List.of(new Argument("username/alias")), CommandType.CLIENT);
        }

        @Override
        public String dispatch() {
            String name = getArgument("username/alias").getValue();

            if (!Comu.getInstance().getFriendManager().isFriend(name)) {
                return "That user is not a friend.";
            }

            me.comu.people.friend.Friend friend = Comu.getInstance().getFriendManager().getByNameOrAlias(name);
            String oldAlias = friend.getAlias();
            Comu.getInstance().getFriendManager().remove(name);

            return String.format("Removed friend with alias &e%s&7.", oldAlias);
        }
    }
}
