package me.comu.module.impl.movement;

import me.comu.api.registry.event.listener.Listener;
import me.comu.events.MotionEvent;
import me.comu.events.PacketEvent;
import me.comu.events.SprintAttackEvent;
import me.comu.module.Category;
import me.comu.module.ToggleableModule;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;

import java.util.List;

public class Sprint extends ToggleableModule {

    boolean multiDir = true;
    boolean keepSprint = true;

    public Sprint() {
        super("Sprint", List.of("autorun", "autosprint"), Category.MOVEMENT, "Automatically sprints for you");
        listeners.add(new Listener<>(MotionEvent.class) {
            @Override
            public void call(MotionEvent event) {
                if (canSprint() && mc.player != null) {
                    mc.player.setSprinting(true);
                }
            }
        });

        listeners.add(new Listener<>(PacketEvent.class) {
            @Override
            public void call(PacketEvent event) {
                if (event.getDirection() == PacketEvent.Direction.OUTGOING) {
                    Packet<?> packet = event.getPacket();
                    if (packet instanceof ClientCommandC2SPacket cmdPacket) {
                        ClientCommandC2SPacket.Mode mode = cmdPacket.getMode();
                        if (mode == ClientCommandC2SPacket.Mode.START_SPRINTING || mode == ClientCommandC2SPacket.Mode.STOP_SPRINTING) {
                            event.setCancelled(true);
                        }
                    }
                }
            }
        });

        listeners.add(new Listener<>(SprintAttackEvent.class) {
            @Override
            public void call(SprintAttackEvent event) {
                if (keepSprint) {
                    event.setCancelled(true);
                }
            }
        });
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (mc.player != null) {
            mc.player.setSprinting(false);
        }
    }

    public boolean canSprint() {
        if (mc.player == null || mc.player.isSneaking() || mc.player.horizontalCollision || mc.player.getHungerManager().getFoodLevel() <= 6)
            return false;

        return multiDir
                ? mc.player.input.getMovementInput().x != 0.0f || mc.player.input.getMovementInput().y != 0.0f
                : mc.options.forwardKey.isPressed() && mc.player.input.getMovementInput().y > 0;

    }
}
