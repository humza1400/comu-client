package me.comu.module.impl.movement;

import me.comu.api.registry.event.listener.Listener;
import me.comu.events.PacketEvent;
import me.comu.events.TickEvent;
import me.comu.logging.Logger;
import me.comu.module.Category;
import me.comu.module.ToggleableModule;
import me.comu.property.properties.BooleanProperty;
import me.comu.property.properties.EnumProperty;
import me.comu.property.properties.NumberProperty;
import me.comu.utils.ItemUtils;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.common.CommonPongC2SPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.world.tick.Tick;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Velocity extends ToggleableModule {

    private final NumberProperty<Integer> percent = new NumberProperty<>("Percent", List.of("p", "%"), 0, 0, 100, 5);
    private final NumberProperty<Long> transactionDelay = new NumberProperty<>("Transaction-Delay", List.of("transdelay", "canceltrans", "canceltransaction", "td"), 44000L, 1L, 100000L, 5000L);
    private final BooleanProperty bowboost = new BooleanProperty("Bow Boost", List.of("bowboost", "bow", "boost", "bboost"), false);
    private final EnumProperty<Mode> mode = new EnumProperty<>("Mode", List.of("m"), Mode.VULCAN);
    private final BooleanProperty debug = new BooleanProperty("debug", true);


    private long firstQueuedTime = 0;
    private final CopyOnWriteArrayList<CommonPongC2SPacket> queuedPackets = new CopyOnWriteArrayList<>();
    private long lastVelocityTime = 0;

    private enum Mode {
        NCP, VULCAN,
    }

    public Velocity() {
        super("Velocity", List.of("antivelocity", "vel", "antikb"), Category.MOVEMENT, "Modifies how much velocity you take");
        offerProperties(percent, transactionDelay, bowboost, mode, debug);
        listeners.add(new Listener<>(PacketEvent.class) {
            @Override
            public void call(final PacketEvent event) {
                if (isPlayerOrWorldNull()) return;
                if (bowboost.getValue() && ItemUtils.isHeldItemInstanceOf(Items.BOW) && ItemUtils.getEnchantmentLevel(Enchantments.PUNCH, ItemUtils.getHeldItem()) > 0) {
                    return;
                }

                var p = event.getPacket();
                if (p instanceof EntityVelocityUpdateS2CPacket packet) {
                    if (packet.getEntityId() == mc.player.getId()) {
                        lastVelocityTime = System.currentTimeMillis();
                        switch (mode.getValue()) {
                            case NCP -> {
                                int pct = percent.getValue();

                                if (pct == 0) {
                                    event.setCancelled(true);
                                    return;
                                }

                                if (pct != 100) {
                                    event.setCancelled(true);
                                    double scale = pct / 100.0;

                                    double newVelX = packet.getVelocityX() * scale;
                                    double newVelY = packet.getVelocityY() * scale;
                                    double newVelZ = packet.getVelocityZ() * scale;

                                    mc.player.addVelocity(newVelX, newVelY, newVelZ);
                                }
                            }

                            case VULCAN -> {
                                double rawY = packet.getVelocityY() / 8000.0;
                                event.setCancelled(true);

                                if (rawY > 0.42) {
                                    mc.player.addVelocity(0, rawY, 0);
                                }
                            }
                        }
                    }
                }
                if (event.getPacket() instanceof CommonPongC2SPacket) {
                    if (mode.getValue() == Mode.VULCAN) {
                        long now = System.currentTimeMillis();

                        if (queuedPackets.isEmpty()) {
                            firstQueuedTime = now;
                        }

                        if (now - lastVelocityTime < 2000) {
                            event.setCancelled(true);
                            queuedPackets.add((CommonPongC2SPacket) event.getPacket());

                        }
                    }
                }
            }
        });
        listeners.add(new Listener<>(TickEvent.class) {
            @Override
            public void call(final TickEvent event) {
                if (isPlayerOrWorldNull()) return;
                if (mode.getValue() == Mode.VULCAN) {
                    long now = System.currentTimeMillis();
                    boolean twoSecPassed = now - lastVelocityTime >= 2000;
                    boolean transactionDelayPassed = now - firstQueuedTime >= transactionDelay.getValue();

                    if (!queuedPackets.isEmpty() && (twoSecPassed || transactionDelayPassed)) {
                        if (debug.getValue())
                            Logger.getLogger().printToChat("Flushed queue. 2: \247a" + twoSecPassed + "\2477 (" + (now - lastVelocityTime) + ")" + ", max: \247a" + transactionDelayPassed + "\2477 (" + (now - firstQueuedTime) + "), size: \247a" + queuedPackets.size());
                        for (CommonPongC2SPacket tx : queuedPackets) {
                            mc.getNetworkHandler().sendPacket(tx);
                        }
                        queuedPackets.clear();
                    }
                }
            }
        });
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public String getSuffix() {
        return super.getSuffix();
    }
}