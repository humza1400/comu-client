package me.comu.module.impl.miscellaneous;

import me.comu.Comu;
import me.comu.api.registry.event.listener.Listener;
import me.comu.events.PacketEvent;
import me.comu.logging.Logger;
import me.comu.module.Category;
import me.comu.module.ToggleableModule;
import me.comu.notification.Notification;
import me.comu.notification.NotificationType;
import me.comu.property.properties.BooleanProperty;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class AntiVanish extends ToggleableModule {

    private final BooleanProperty experimental = new BooleanProperty("Experimental", List.of("debug"), true);
    private final CopyOnWriteArrayList<UUID> vanished = new CopyOnWriteArrayList<>();

    public AntiVanish() {
        super("Anti Vanish", List.of("antivanish", "novanish", "vanish"), Category.MISCELLANEOUS, "Notifies you when a player goes into vanish");
        offerProperties(experimental);
        listeners.add(new Listener<>(PacketEvent.class) {
            @Override
            public void call(PacketEvent event) {
                if (isPlayerOrWorldNull()) return;
                if (event.getPacket() instanceof PlayerListS2CPacket packet) {
                    PlayerListS2CPacket.Action action = packet.getActions().iterator().next();
                    for (PlayerListS2CPacket.Entry entry : packet.getEntries()) {
                        UUID uuid = entry.profileId();
                        String name;
                        if (entry.profile() == null) name = "null";
                        else name = entry.profile().getName();
                        Logger.getLogger().printToChat(name);
                        if (!experimental.getValue() && !Comu.getInstance().getStaffManager().isStaff(name)) continue;

                        switch (action) {
                            case ADD_PLAYER -> {
                                if (vanished.contains(uuid)) {
                                    sendNotification(name + " left vanish.");
                                    vanished.remove(uuid);
                                } else {
                                    sendNotification(name + " is in vanish.");
                                    vanished.add(uuid);
                                }
                            }
                            case UPDATE_LATENCY -> {
                                PlayerListEntry info = MinecraftClient.getInstance().getNetworkHandler().getPlayerListEntry(uuid);
                                if (info == null && !checkList(uuid)) {
                                    sendNotification(name + " is vanished. (" + getTimeString() + ")");
                                }
                            }

                            case UPDATE_LISTED -> {
                                if (!entry.listed() && !vanished.contains(uuid)) {
                                    sendNotification(name + " is no longer listed (possibly vanished).");
                                    vanished.add(uuid);
                                } else if (entry.listed() && vanished.contains(uuid)) {
                                    sendNotification(name + " is now listed again (returned from vanish).");
                                    vanished.remove(uuid);
                                }
                            }
                        }
                    }
                }
            }
        });

    }

    private void sendNotification(String message) {
        Comu.getInstance().getNotificationManager().notify(new Notification(NotificationType.WARNING, "AntiVanish", message, 5));
        Logger.getLogger().printToChat("§8" + message);
    }

    private boolean checkList(UUID uuid) {
        if (vanished.contains(uuid)) {
            vanished.remove(uuid);
            return true;
        }
        vanished.add(uuid);
        return false;
    }

    private String getName(UUID uuid) {
        PlayerListEntry entry = MinecraftClient.getInstance().getNetworkHandler().getPlayerListEntry(uuid);
        return entry != null ? entry.getProfile().getName() : "Unknown";
    }

    private String getTimeString() {
        return new SimpleDateFormat("hh:mm a").format(new Date());
    }

    @Override
    public String getSuffix() {
        return Integer.toString(vanished.size());
    }
}
