package me.comu.utils;

import me.comu.logging.Logger;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ItemUtils {

    private static MinecraftClient mc = MinecraftClient.getInstance();

    private static final List<String> ENCHANTMENT_PRIORITY_LIST = List.of(
            "sharpness",
            "fire_aspect",
            "knockback",
            "looting",
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
        for (ClientUtils.Triple<String, Integer, Integer> stringIntegerIntegerTriple : enchList) {
            String id = stringIntegerIntegerTriple.first();
            int lvl = stringIntegerIntegerTriple.second();
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

    public static ItemStack getEquipmentItem(PlayerEntity player, int index) {
        return switch (index) {
            case 0 -> player.getMainHandStack();
            case 1 -> player.getEquippedStack(EquipmentSlot.HEAD);
            case 2 -> player.getEquippedStack(EquipmentSlot.CHEST);
            case 3 -> player.getEquippedStack(EquipmentSlot.LEGS);
            case 4 -> player.getEquippedStack(EquipmentSlot.FEET);
            case 5 -> player.getOffHandStack();
            default -> ItemStack.EMPTY;
        };
    }

    public static ItemStack getHeldItem() {
        return mc.player != null ? mc.player.getMainHandStack() : ItemStack.EMPTY;
    }

    public static boolean isHeldItemInstanceOf(Item item) {
        ItemStack heldItem = getHeldItem();
        return !heldItem.isEmpty() && heldItem.isOf(item);
    }

    public static int getEnchantmentLevel(RegistryKey<Enchantment> enchantmentKey, ItemStack stack) {
        ItemEnchantmentsComponent enchantments = stack.getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);

        for (RegistryEntry<Enchantment> entry : enchantments.getEnchantments()) {
            if (entry.getKey().orElse(null) == enchantmentKey) {
                return enchantments.getLevel(entry);
            }
        }

        return 0;
    }

    public static boolean isSword(ItemStack stack) {
        return stack.isOf(Items.WOODEN_SWORD)
                || stack.isOf(Items.STONE_SWORD)
                || stack.isOf(Items.IRON_SWORD)
                || stack.isOf(Items.GOLDEN_SWORD)
                || stack.isOf(Items.DIAMOND_SWORD)
                || stack.isOf(Items.NETHERITE_SWORD);
    }

    public static boolean isGapple(ItemStack stack) {
        return stack.isOf(Items.ENCHANTED_GOLDEN_APPLE);
    }

    public static boolean isAxe(ItemStack stack) {
        return stack.getItem() instanceof AxeItem;
    }

    public static boolean isOmega(ItemStack stack) {
        return isAxe(stack) && getEnchantmentLevel(Enchantments.SHARPNESS, stack) >= 90;
    }

    public static boolean isPurpleGodSword(ItemStack stack) {
        return isSword(stack) && getEnchantmentLevel(Enchantments.SHARPNESS, stack) >= 90;
    }

    public static boolean hasCurseOfVanishing(ItemStack stack) {
        return getEnchantmentLevel(Enchantments.VANISHING_CURSE, stack) > 0;
    }

    public static boolean isPunch2OrBetter(ItemStack stack) {
        return getEnchantmentLevel(Enchantments.PUNCH, stack) >= 2;
    }

    public static boolean isPunch3OrBetter(ItemStack stack) {
        return getEnchantmentLevel(Enchantments.PUNCH, stack) >= 3;
    }

    public static int getPotCount() {
        if (mc.player == null) return 0;

        int count = 0;

        for (int i = 0; i < mc.player.getInventory().size(); i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (isSplashHealthPotion(stack)) {
                count += stack.getCount();
            }
        }

        return count;
    }

    public static boolean isSplashHealthPotion(ItemStack stack) {
        if (stack.getItem() != Items.SPLASH_POTION) return false;

        PotionContentsComponent potionComponent = stack.get(DataComponentTypes.POTION_CONTENTS);
        if (potionComponent == null) return false;

        RegistryEntry<Potion> potionEntry = potionComponent.potion().orElse(null);
        if (potionEntry == null) return false;

        Potion potion = potionEntry.value();
        return potion.getBaseName().equalsIgnoreCase(Potions.HEALING.getIdAsString().replaceFirst("minecraft:", "")) || potion.getBaseName().equalsIgnoreCase(Potions.STRONG_HEALING.getIdAsString().replaceFirst("minecraft:", ""));
    }
}
