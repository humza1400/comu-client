package me.comu.module.impl.combat;

import me.comu.api.registry.event.listener.Listener;
import me.comu.api.stopwatch.Stopwatch;
import me.comu.events.TickEvent;
import me.comu.logging.Logger;
import me.comu.module.Category;
import me.comu.module.ToggleableModule;
import me.comu.property.properties.NumberProperty;
import me.comu.utils.ItemUtils;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;

import java.util.*;

public class AutoArmor extends ToggleableModule {

    private final NumberProperty<Long> delay = new NumberProperty<>("Delay", List.of("d", "wait"), 50L, 0L, 1000L, 25L);
    private final Stopwatch stopwatch = new Stopwatch();

    public AutoArmor() {
        super("Auto Armor", List.of("autoarmor", "armorauto", "aa"), Category.COMBAT, "Automatically equips the best armor in your inventory.");
        offerProperties(delay);

        this.listeners.add(new Listener<>(TickEvent.class) {
            @Override
            public void call(TickEvent event) {
                if (mc.player == null || mc.world == null || mc.currentScreen != null) return;
                if (!stopwatch.hasCompleted(delay.getValue(), true)) return;

                for (int armorSlot = 0; armorSlot < 4; armorSlot++) {
                    int armorIndex = 36 + armorSlot;
                    ItemStack equipped = mc.player.getInventory().getStack(armorIndex);
                    int currentScore = ItemUtils.getArmorScore(equipped, armorSlot);

                    int bestSlot = -1;
                    int bestScore = currentScore;

                    for (int i = 0; i < 36; i++) {
                        ItemStack stack = mc.player.getInventory().getStack(i);
                        if (!ItemUtils.isArmor(stack)) continue;
                        int score = ItemUtils.getArmorScore(stack, armorSlot);
                        if (score > bestScore) {
                            bestScore = score;
                            bestSlot = i;
                        }
                    }


                    if (bestSlot != -1) {
                        ItemStack current = mc.player.getInventory().getStack(36 + armorSlot);
                        if (!current.isEmpty() && mc.player.getInventory().getEmptySlot() == -1) return;

                        int syncId = mc.player.currentScreenHandler.syncId;
                        int networkSlot = bestSlot < 9 ? bestSlot + 36 : bestSlot;

                        if (!current.isEmpty()) {
                            int armorEquipSlot = 8 - armorSlot;
                            mc.interactionManager.clickSlot(syncId, armorEquipSlot, 0, SlotActionType.QUICK_MOVE, mc.player);
                        }

                        mc.interactionManager.clickSlot(syncId, networkSlot, 0, SlotActionType.QUICK_MOVE, mc.player);
                    }

                }
            }
        });
    }
}
