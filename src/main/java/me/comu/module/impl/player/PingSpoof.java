package me.comu.module.impl.player;

import me.comu.api.registry.event.listener.Listener;
import me.comu.events.PacketEvent;
import me.comu.logging.Logger;
import me.comu.module.Category;
import me.comu.module.ToggleableModule;
import me.comu.property.properties.NumberProperty;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.common.CommonPongC2SPacket;
import net.minecraft.network.packet.c2s.common.KeepAliveC2SPacket;
import net.minecraft.util.Pair;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class PingSpoof extends ToggleableModule {

    NumberProperty<Integer> waitTime = new NumberProperty<>("Wait Time", List.of(), 100, 0, 10000, 100);
    Queue<Pair<Packet<?>, Long>> packetQueue = new LinkedList<>();

    public PingSpoof() {
        super("Ping Spoof", List.of("pingspoof"), Category.PLAYER, "Makes your ping higher");
        offerProperties(waitTime);

        listeners.add(new Listener<>(PacketEvent.class) {
            @Override
            public void call(PacketEvent event) {
                if (event.getPacket() instanceof CommonPongC2SPacket || event.getPacket() instanceof KeepAliveC2SPacket) {
                    event.setCancelled(true);
                    packetQueue.add(new Pair<>(event.getPacket(), System.currentTimeMillis()));
                }
            }
        });

        listeners.add(new Listener<>(me.comu.events.TickEvent.class) {
            @Override
            public void call(me.comu.events.TickEvent event) {
                long now = System.currentTimeMillis();
                while (!packetQueue.isEmpty()) {
                    Pair<Packet<?>, Long> pair = packetQueue.peek();
                    if (now - pair.getRight() >= waitTime.getValue()) {
                        Logger.getLogger().printToChat("Sending " + pair.getLeft().getPacketType().id());
                        mc.getNetworkHandler().sendPacket(pair.getLeft());
                        packetQueue.poll();
                    } else {
                        break;
                    }
                }
            }
        });
    }
}
