package me.comu.module.impl.movement;

import me.comu.api.registry.event.listener.Listener;
import me.comu.events.PacketEvent;
import me.comu.events.TickEvent;
import me.comu.module.Category;
import me.comu.module.ToggleableModule;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.world.tick.Tick;

import java.util.List;

public class Velocity extends ToggleableModule {
    public Velocity() {
        super("Velocity", List.of(), Category.MOVEMENT, "makes you take less Velocity");
        listeners.add(new Listener<>(PacketEvent.class) {
            @Override
            public void call(final PacketEvent event) {
                var p = event.getPacket();
                if(p instanceof EntityVelocityUpdateS2CPacket){
                    event.setCancelled(true);
                }

            }
        });
    }
}
