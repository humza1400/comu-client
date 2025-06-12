package me.comu.module.impl.miscellaneous;

import me.comu.api.registry.event.listener.Listener;
import me.comu.api.stopwatch.Stopwatch;
import me.comu.events.MotionEvent;
import me.comu.module.Category;
import me.comu.module.ToggleableModule;
import me.comu.property.properties.EnumProperty;
import me.comu.property.properties.NumberProperty;
import me.comu.utils.ClientUtils;
import me.comu.utils.ItemUtils;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.*;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

import java.util.List;

public class InventoryCleaner extends ToggleableModule {

    private final NumberProperty<Long> delay = new NumberProperty<>("Delay", List.of("d", "wait"), 40L, 0L, 1000L, 50L);
    private final EnumProperty<Mode> mode = new EnumProperty<>("Mode", List.of("m"), Mode.POTION);
    private final Stopwatch stopwatch = new Stopwatch();

    public enum Mode {
        EVERYTHING, POTION, PURPLE
    }

    public InventoryCleaner() {
        super("Inventory Cleaner", List.of("invcleaner", "inventorycleaner", "invclean"), Category.MISCELLANEOUS, "Automatically drops items in your inventory for you");
        offerProperties(delay, mode);
        setSuffix(mode.getFormattedValue());
        listeners.add(new Listener<>(MotionEvent.class) {
            @Override
            public void call(MotionEvent event) {
                if (mc.player == null || mc.world == null || !event.isPre()) return;
                for (int i = 9; i < 45; i++) {
                    Slot slot = mc.player.currentScreenHandler.slots.get(i);
                    ItemStack stack = slot.getStack();

                    if (stack.isEmpty()) continue;

                    boolean shouldDrop = switch (mode.getValue()) {
                        case EVERYTHING -> true;
                        case POTION -> stack.getItem() instanceof PotionItem;
                        case PURPLE -> {
                            Item item = stack.getItem();
                            boolean keepWeapon = ItemUtils.isOmega(stack) || ItemUtils.isPurpleGodSword(stack);
                            boolean keepBow = ItemUtils.isPunch2OrBetter(stack);
                            boolean keepGapple = ItemUtils.isGapple(stack);
                            yield !(keepWeapon || keepBow || keepGapple);
                        }
                    };

                    if (shouldDrop && stopwatch.hasCompleted(delay.getValue(), true)) {
                        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, i, 1, SlotActionType.THROW, mc.player);
                        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, -999, 1, SlotActionType.THROW, mc.player);
                        stopwatch.reset();
                    }
                }
            }
        });
    }
}
