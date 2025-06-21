package me.comu.module.impl.combat;

import me.comu.api.registry.event.listener.Listener;
import me.comu.api.stopwatch.Stopwatch;
import me.comu.events.MotionEvent;
import me.comu.events.TickEvent;
import me.comu.module.Category;
import me.comu.module.ToggleableModule;
import me.comu.property.properties.BooleanProperty;
import me.comu.property.properties.NumberProperty;
import me.comu.utils.ItemUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static me.comu.utils.ItemUtils.isSplashHealthPotion;

public class AutoPotion extends ToggleableModule {

    private final NumberProperty<Float> health = new NumberProperty<>("Health", List.of("h", "<3", "hearts", "heart"), 8F, 1F, 20F, 1F);
    private final NumberProperty<Long> delay = new NumberProperty<>("Delay", List.of("d", "wait"), 200L, 0L, 1000L, 50L);
    private final BooleanProperty eatCheck = new BooleanProperty("Eat Check", List.of("eatcheck", "checkeat", "eat"), true);
    private final BooleanProperty ladderCheck = new BooleanProperty("Ladder Check", List.of("laddercheck", "ladder", "ladders", "ladderscheck"), true);
    private final BooleanProperty defensive = new BooleanProperty("Defensive", List.of("defensive"), false);

    public boolean isPotting;
    public boolean doPot;
    private Stopwatch stopwatch = new Stopwatch();
    public boolean pendingSwitch = false;
    private boolean pendingIsSoup = false;
    private int pendingPotSlot = -1;
    private int pendingOriginalSlot = -1;
    private float currentPitch;

    public AutoPotion() {
        super("Auto Potion", List.of("autopot", "autopotion", "ap", "ap2", "autoheal"), Category.COMBAT, "Pots for you when at low health");
        offerProperties(health, delay, defensive, eatCheck, ladderCheck);
        this.listeners.add(new Listener<>(MotionEvent.class) {
            @Override
            public void call(MotionEvent event) {
                if (isPlayerOrWorldNull()) return;
                setSuffix(Integer.toString(ItemUtils.getPotCount()));
                if (event.isPre()) {
                    if (pendingSwitch) {
                        event.setPitch(90);
                    }
                    final ItemThing potSlot = getHealingItemFromInventory();
                    if (potSlot == null) return;
                    if (stopwatch.hasCompleted(delay.getValue()) && mc.player.getHealth() <= health.getValue() && potSlot.getSlot() != -1) {
                        float basePitch = currentPitch;
                        float normalized = normalizePitch((float) (89 + getRandomInRange(-1, 1)), basePitch, 1.5F);
                        currentPitch = normalized;

                        event.setPitch(currentPitch);


                        boolean shouldPot;

                        if (eatCheck.getValue()) {
                            shouldPot = !(mc.player.isUsingItem() && (mc.player.getActiveItem().getItem() == Items.ENCHANTED_GOLDEN_APPLE || mc.player.getActiveItem().getItem() == Items.GOLDEN_APPLE));
                        } else if (ladderCheck.getValue()) {
                            shouldPot = !mc.player.isClimbing();
                        } else {
                            shouldPot = true;
                        }

                        if (shouldPot) {
                            isPotting = true;
                            mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(
                                    PlayerActionC2SPacket.Action.RELEASE_USE_ITEM,
                                    BlockPos.ORIGIN,
                                    Direction.DOWN
                            ));
                            doPot = true;
                        }

                    }

                }
            }
        });

        listeners.add(new Listener<>(TickEvent.class) {
            @Override
            public void call(TickEvent event) {
                if (event.getPhase() == TickEvent.Phase.PRE) {
                    if (pendingSwitch) {
                        mc.getNetworkHandler().sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, mc.player.getInventory().getSelectedSlot(), mc.player.getYaw(), mc.player.getPitch()));

                        if (pendingPotSlot != pendingOriginalSlot) {
                            mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(pendingOriginalSlot));
                        }

                        if (pendingIsSoup) {
                            mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(
                                    PlayerActionC2SPacket.Action.DROP_ITEM,
                                    BlockPos.ORIGIN,
                                    Direction.DOWN
                            ));
                        }

                        stopwatch.reset();
                        pendingSwitch = false;
                        pendingPotSlot = -1;
                        pendingOriginalSlot = -1;
                        return;
                    }

                    if (doPot) {
                        ItemThing potSlot = getHealingItemFromInventory();
                        if (potSlot == null) return;

                        if (potSlot.getSlot() == -1) {
                            doPot = false;
                            isPotting = false;
                            return;
                        }

                        int originalSlot = mc.player.getInventory().getSelectedSlot();
                        boolean isHotbar = potSlot.getSlot() >= 0 && potSlot.getSlot() <= 8;
                        int potHotbarIndex = isHotbar ? potSlot.getSlot() : -1;

                        if (isHotbar) {
                            if (potHotbarIndex != originalSlot) {
                                mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(potHotbarIndex));

                                pendingSwitch = true;
                                pendingPotSlot = potHotbarIndex;
                                pendingOriginalSlot = originalSlot;

                                doPot = false;
                            } else {
                                mc.getNetworkHandler().sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, mc.player.getInventory().getSelectedSlot(), mc.player.getYaw(), mc.player.getPitch()));

                                stopwatch.reset();
                                doPot = false;
                                isPotting = false;
                            }
                        } else {
                            swap(potSlot.getSlot(), 5);
                            mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(5));

                            pendingSwitch = true;
                            pendingPotSlot = 5;
                            pendingOriginalSlot = originalSlot;

                            doPot = false;
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onEnable() {
        super.onEnable();
        isPotting = false;
        doPot = false;
        pendingSwitch = false;
        pendingIsSoup = false;
        pendingPotSlot = -1;
        pendingOriginalSlot = -1;
        if (!isPlayerOrWorldNull()) currentPitch = mc.player.getPitch();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        isPotting = false;
        doPot = false;
        pendingSwitch = false;
        pendingIsSoup = false;
        pendingPotSlot = -1;
        pendingOriginalSlot = -1;
    }

    public float normalizePitch(float targetPitch, float basePitch, float smoothFactor) {
        double gcd = getGCD();

        float pitchDelta = targetPitch - basePitch;
        float basePitchChange = 8.0f;

        float maxPitchChange = addRandomization(basePitchChange, 1.5f);
        float smoothedPitchDelta = Math.max(-maxPitchChange, Math.min(pitchDelta * smoothFactor, maxPitchChange));

        float normalizedPitch = basePitch + Math.round(smoothedPitchDelta / (float) gcd) * (float) gcd;
        normalizedPitch = clamp(normalizedPitch, -90f, 90f);

        return normalizedPitch;
    }

    private float addRandomization(float value, float maxVariation) {
        return value + (ThreadLocalRandom.current().nextFloat() - 0.5f) * maxVariation;
    }

    public double getRandomInRange(double min, double max) {
        Random random = new Random();
        double range = max - min;
        double scaled = random.nextDouble() * range;
        if (scaled > max) {
            scaled = max;
        }
        double shifted = scaled + min;

        if (shifted > max) {
            shifted = max;
        }
        return shifted;
    }

    private double getGCD() {
        double sensitivity = mc.options.getMouseSensitivity().getValue();
        double f = sensitivity * 0.6 + 0.2;
        return f * f * f * 8.0 * 0.15;
    }

    private static float clamp(float v, float mn, float mx) {
        return v < mn ? mn : Math.min(v, mx);
    }

    public ItemThing getHealingItemFromInventory() {
        int itemSlot = -1;
        int counter = 0;
        for (int i = 0; i < 36; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (isSplashHealthPotion(stack)) {
                ++counter;
                itemSlot = i;
                return new ItemThing(itemSlot);
            }
        }
        return null;
    }

    private void swap(final int slot, final int hotbarSlot) {
        if (slot >= 0 && slot < 36) {
            mc.interactionManager.clickSlot(
                    mc.player.currentScreenHandler.syncId,
                    slot,
                    hotbarSlot,
                    SlotActionType.SWAP,
                    mc.player
            );
        }
    }

}

class ItemThing {
    private int slot;

    public ItemThing(final int slot) {
        this.slot = slot;
    }

    public int getSlot() {
        return this.slot;
    }
}