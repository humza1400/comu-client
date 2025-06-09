package me.comu.utils;

import me.comu.render.Renderer2D;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.Box;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ClientUtils {

    private static MinecraftClient mc = MinecraftClient.getInstance();

    private static final List<String> ENCHANTMENT_PRIORITY_LIST = List.of(
            "sharpness",
            "fire_aspect",
            "efficiency",
            "silk_touch",
            "fortune",
            "power",
            "punch",
            "flame",
            "infinity",
            "protection",
            "projectile_protection",
            "blast_protection",
            "fire_protection",
            "thorns",
            "feather_falling",
            "aqua_affinity",
            "respiration",
            "unbreaking",
            "mending",
            "frost_walker"
    );

    public static int getKeyCodeByName(String name) {
        try {
            return (int) GLFW.class.getField("GLFW_KEY_" + name).get(null);
        } catch (Exception ignored) {
            return GLFW.GLFW_KEY_UNKNOWN;
        }
    }

    public static String getKeyName(int keyCode) {
        for (var field : GLFW.class.getFields()) {
            try {
                if (field.getName().startsWith("GLFW_KEY_") && (int) field.get(null) == keyCode) {
                    return field.getName().replace("GLFW_KEY_", "");
                }
            } catch (Exception ignored) {}
        }
        return "UNKNOWN";
    }

    public static boolean isKeyPressed(int key) {
        long windowHandle = MinecraftClient.getInstance().getWindow().getHandle();
        return GLFW.glfwGetKey(windowHandle, key) == GLFW.GLFW_PRESS;
    }


    public static boolean isMouseButtonDown(int button) {
        long windowHandle = MinecraftClient.getInstance().getWindow().getHandle();
        return GLFW.glfwGetMouseButton(windowHandle, button) == GLFW.GLFW_PRESS;
    }

    public record Triple<A, B, C>(A first, B second, C third) {
    }

    public static void packet(Packet<?> packet) {
        if (mc.getNetworkHandler() == null) { return; }
        mc.getNetworkHandler().sendPacket(packet);
    }

    public static boolean isMoving() {
        assert mc.player != null;
        return mc.player.input.getMovementInput().x != 0.0f || mc.player.input.getMovementInput().y != 0.0f;
    }

    public static boolean isOnGround(double height) {
        if (mc.player == null || mc.world == null) return false;

        Box box = mc.player.getBoundingBox().offset(0.0, -height, 0.0);
        return mc.world.getBlockCollisions(mc.player, box).iterator().hasNext();
    }

    public static String trimTextToFit(String text, int maxWidth) {
        int i = text.length();
        while (i > 0 && Renderer2D.getStringWidth(text.substring(0, i)) > maxWidth) i--;
        return text.substring(0, i);
    }

    public static List<String> getShortenedEnchantments(ItemStack stack) {
        List<String> lines = new ArrayList<>();
        List<ClientUtils.Triple<String, Integer, Integer>> enchList = new ArrayList<>(); // id, level, priority

        ItemEnchantmentsComponent enchantments = EnchantmentHelper.getEnchantments(stack);
        for (RegistryEntry<Enchantment> enchantment : enchantments.getEnchantments()) {
            String id = enchantment.getIdAsString().replace("minecraft:", "").toLowerCase();
            int lvl = enchantments.getLevel(enchantment);
            if (lvl <= 0) continue;

            int priority = ENCHANTMENT_PRIORITY_LIST.indexOf(id);
            if (priority == -1) priority = Integer.MAX_VALUE;

            enchList.add(new ClientUtils.Triple<>(id, lvl, priority));
        }

        enchList.sort(Comparator.comparingInt(ClientUtils.Triple::third));
        for (int i = 0; i < Math.min(enchList.size(), 2); i++) {
            String id = enchList.get(i).first();
            int lvl = enchList.get(i).second();
            String shortName = switch (id) {
                case "sharpness" -> "sh";
                case "fire_aspect" -> "fa";
                case "knockback" -> "kb";
                case "looting" -> "lt";
                case "sweeping" -> "sw";
                case "unbreaking" -> "ub";
                case "efficiency" -> "ef";
                case "fortune" -> "fo";
                case "mending" -> "me";
                case "protection" -> "pr";
                case "fire_protection" -> "fp";
                case "blast_protection" -> "bp";
                case "projectile_protection" -> "pp";
                case "feather_falling" -> "ff";
                case "respiration" -> "rs";
                case "aqua_affinity" -> "aa";
                case "depth_strider" -> "ds";
                case "frost_walker" -> "fw";
                case "thorns" -> "th";
                case "binding_curse" -> "bc";
                case "vanishing_curse" -> "vc";
                case "bane_of_arthropods" -> "ar";
                case "breach" -> "br";
                case "channeling" -> "ch";
                case "density" -> "de";
                case "flame" -> "fl";
                case "impaling" -> "im";
                case "infinity" -> "in";
                case "loyalty" -> "lo";
                case "luck_of_the_sea" -> "ls";
                case "lure" -> "lu";
                case "multishot" -> "ms";
                case "piercing" -> "pi";
                case "power" -> "po";
                case "punch" -> "pu";
                case "quick_charge" -> "qc";
                case "riptide" -> "rt";
                case "silk_touch" -> "st";
                case "smite" -> "sm";
                case "soul_speed" -> "ss";
                case "sweeping_edge" -> "se";
                case "swift_sneak" -> "ss";
                case "wind_burst" -> "wb";

                default -> id.length() > 2 ? id.substring(0, 2) : id;
            };
            lines.add(shortName + lvl);
        }
        return lines;
    }
}
