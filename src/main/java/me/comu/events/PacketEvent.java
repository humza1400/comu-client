package me.comu.events;

import me.comu.api.registry.event.Event;
import net.minecraft.network.packet.Packet;

public class PacketEvent extends Event {
    private final Packet<?> packet;
    private final Direction direction;
    public enum Direction { INCOMING, OUTGOING }

    public PacketEvent(Packet<?> packet, Direction direction) {
        this.packet = packet;
        this.direction = direction;
    }

    public Packet<?> getPacket() {
        return packet;
    }

    public Direction getDirection() {
        return direction;
    }

    public boolean isIncoming() {
        return direction == Direction.INCOMING;
    }

    public boolean isOutgoing() {
        return direction == Direction.OUTGOING;
    }
}
