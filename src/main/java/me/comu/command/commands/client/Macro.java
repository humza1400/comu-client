package me.comu.command.commands.client;

import me.comu.Comu;
import me.comu.command.Argument;
import me.comu.command.Command;
import me.comu.command.CommandType;
import me.comu.utils.ClientUtils;

public final class Macro {
    public static class Add extends Command {
        public Add() {
            super(java.util.List.of("macro", "mac", "macroadd", "madd", "macadd", "newmacro", "newmac"), java.util.List.of(new Argument("key"), new Argument("action", true)), CommandType.CLIENT);
        }

        @Override
        public String dispatch() {
            String key = getArgument("key").getValue().toUpperCase();
            String action = getArgument("action").getValue().replaceAll("_", " ");
            int keyValue = ClientUtils.getKeyCodeByName(key);
            if (Comu.getInstance().getMacroManager().isMacro(keyValue))
                return "A macro correlated with &e\"" + ClientUtils.getKeyName(keyValue).toUpperCase() + "\" &7already exists.";
            Comu.getInstance().getMacroManager().addMacro(new me.comu.macro.Macro(keyValue, action));
            return "&e\"" + ClientUtils.getKeyName(keyValue).toUpperCase() + "\" &7Macro created. (\"" + action + "\")";
        }
    }

    public static final class Remove extends Command {
        public Remove() {
            super(java.util.List.of("remmacro", "delmacro", "removemacro", "delmac", "deletemacro", "macrodel", "macroremove", "clearmacro"), java.util.List.of(new Argument("key")), CommandType.CLIENT);
        }


        public String dispatch() {
            String key = getArgument("key").getValue().toUpperCase();
            me.comu.macro.Macro macro = Comu.getInstance().getMacroManager().getUsingKey(ClientUtils.getKeyCodeByName(key));
            if (macro != null) {
                Comu.getInstance().getMacroManager().remove(macro.getKey());
                return "Removed Macro &e\"" + ClientUtils.getKeyName(macro.getKey()).toUpperCase() + "\" (\"" + macro.getAction().getAction() + "\")&7.";
            }

            return "There is no macro correlated with &e\"" + key.toUpperCase() + "\"&7.";
        }
    }

    public static final class List extends Command {
        public List() {
            super(java.util.List.of("macrolist", "listmacros", "listmacro", "mlist"), java.util.List.of(), CommandType.CLIENT);
        }

        public String dispatch() {
            StringBuilder stringBuilder = new StringBuilder("Macros (" + Comu.getInstance().getMacroManager().getMacros().size() + ")\n");
            for (me.comu.macro.Macro macro : Comu.getInstance().getMacroManager().getMacros()) {
                stringBuilder.append(String.format("&e\"%s\"&7 - %s%s", ClientUtils.getKeyName(macro.getKey()).toUpperCase(), macro.getAction().getAction(), "\n"));
            }

            return stringBuilder.toString();
        }
    }

    public static final class Reset extends Command {
        public Reset() {
            super(java.util.List.of("macroreset", "delmacros", "purgemacros", "purgemacro", "macrosdel", "macropurge", "macropurge", "purgemacro", "macroclear", "clearmacros"), java.util.List.of(), CommandType.CLIENT);
        }

        public String dispatch() {
            int size = Comu.getInstance().getMacroManager().getMacros().size();
            Comu.getInstance().getMacroManager().getMacros().clear();
            return String.format("&e%s &7macros were removed.", size);
        }
    }
}
