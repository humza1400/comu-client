package me.comu.command.commands.client;

import me.comu.Comu;
import me.comu.command.Argument;
import me.comu.command.Command;
import me.comu.command.CommandType;
import me.comu.property.Property;
import me.comu.property.properties.BooleanProperty;
import me.comu.property.properties.EnumProperty;
import me.comu.property.properties.ListProperty;
import me.comu.utils.PropertyUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.StringJoiner;

public class Module extends Command {

    public Module()
    {
        super(List.of("module", "mod", "prop"), List.of(
                new Argument("module"),
                new Argument("property", true),
                new Argument("value", true)
        ), CommandType.CLIENT);
    }

    @Override
    public String dispatch() {
        String moduleName = getArgument("module").getValue();
        String propName = getArgument("property").getValue();
        String value = getArgument("value").getValue();

        moduleName = moduleName.replaceFirst(Comu.getInstance().getCommandManager().getPrefix(), "");
        me.comu.module.Module module = Comu.getInstance().getModuleManager().getModuleByName(moduleName);
        if (module == null) return "&cModule not found.";

        if (propName == null || propName.equalsIgnoreCase("list")) {
            if (module.getProperties().isEmpty())
                return String.format("&e%s&7 has no properties.", module.getName());

            StringJoiner joiner = getFormattedPropertyList(module);
            return String.format("&7Properties (%d): %s", module.getProperties().size(), joiner);
        }

        Property<?> prop = module.getPropertyByName(propName);
        if (prop == null)
            return "&7Property not found.";

        if (prop instanceof BooleanProperty) {
            if (value == null || value.equalsIgnoreCase(propName)) {
                prop.toggle();
                boolean state = (Boolean) prop.getValue();
                String result = state ? "&aenabled" : "&cdisabled";
                return String.format("&e%s&7 has been %s&7 for &e%s&7.", prop.getName(), result, module.getName());
            }
        }

        if (value == null || value.equalsIgnoreCase("get")) {
            return String.format("&e%s&7 current value is &e%s&7 for &e%s&7.", prop.getName(), PropertyUtils.getFormattedPropertyValue(prop), module.getName());
        }

        if (prop instanceof EnumProperty<?> ep && value.equalsIgnoreCase("list")) {
            StringJoiner joiner = new StringJoiner("&7, ");
            Enum<?> current = ep.getValue();


            for (Enum<?> constant : ep.getValues()) {
                String formatted = constant.name().substring(0, 1).toUpperCase() + constant.name().substring(1).toLowerCase();
                if (constant == current) {
                    joiner.add("&a" + formatted);
                } else {
                    joiner.add("&c" + formatted);
                }
            }

            return String.format("&7Modes (%d): %s&7.", ep.getValues().size(), joiner);
        }


        try {
            Object parsed = PropertyUtils.parseValue(prop, value);
            PropertyUtils.safelySet(prop, parsed);
            return String.format("&e%s&7 has been set to &e%s&7 for &e%s&7.", prop.getName(), PropertyUtils.getFormattedPropertyValue(prop), module.getName());
        } catch (Exception e) {
            String msg = e.getMessage();
            int colonIndex = msg.indexOf(':');

            if (colonIndex != -1) {
                String before = msg.substring(0, colonIndex + 1);
                String after = msg.substring(colonIndex + 1).trim();
                return "&7" + before + " &c" + after;
            } else {
                return "&7" + msg;
            }

        }
    }

    private static @NotNull StringJoiner getFormattedPropertyList(me.comu.module.Module module) {
        StringJoiner joiner = new StringJoiner(", ");

        module.getProperties().stream()
                .sorted((a, b) -> {
                    boolean aList = a instanceof ListProperty;
                    boolean bList = b instanceof ListProperty;
                    return Boolean.compare(aList, bList);
                })
                .forEach(prop -> appendProperty(joiner, prop, ""));

        return joiner;
    }


    private static void appendProperty(StringJoiner joiner, Property<?> prop, String prefix) {
//        if (prop instanceof ListProperty listProp) {
//            String groupName = prefix.isEmpty() ? listProp.getName() : prefix + "." + listProp.getName();
//
//            StringJoiner groupJoiner = new StringJoiner(", ");
//            for (Property<?> nested : listProp.getProperties()) {
//                if (nested instanceof ListProperty) {
//                    StringJoiner nestedGroupJoiner = new StringJoiner(", ");
//                    appendProperty(nestedGroupJoiner, nested, "");
//                    groupJoiner.add(nestedGroupJoiner.toString());
//                } else {
//                    String nestedName = nested.getName();
//                    String value = PropertyUtils.getFormattedPropertyValue(nested);
//                    groupJoiner.add(String.format("&7%s&e[%s]&7", nestedName, value));
//                }
//            }
//
//            joiner.add(String.format("&6%s: [&7%s&6]", groupName, groupJoiner));
//        } else {
//      // No point in showing list properties if we can't even change them through chat commands, looks ugly. Maybe add support later
        if (prop instanceof ListProperty) return; {}
            String fullName = prefix.isEmpty() ? prop.getName() : prefix + "." + prop.getName();
            String displayVal = PropertyUtils.getFormattedPropertyValue(prop);
            joiner.add(String.format("&7%s&e[%s]&7", fullName, displayVal));
    }
}
