package me.comu.module.impl.movement;

import me.comu.api.registry.event.Event;
import me.comu.api.registry.event.listener.Listener;
import me.comu.events.MotionEvent;
import me.comu.logging.Logger;
import me.comu.module.Category;
import me.comu.module.ToggleableModule;
import me.comu.property.properties.EnumProperty;
import me.comu.property.properties.NumberProperty;
import me.comu.utils.ClientUtils;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.List;

public class NoSlowdown extends ToggleableModule {

    EnumProperty<Mode> mode = new EnumProperty<>("Mode", List.of("m"), Mode.VULCAN);

    public enum Mode {
        OLD, NCP, VULCAN
    }

    public NoSlowdown() {
        super("NoSlowdown", List.of("noslow"), Category.WORLD, "Consume items without slowing down");
        offerProperties(mode);
        listeners.add(new Listener<>(MotionEvent.class) {
            @Override
            public void call(MotionEvent event) {
                if (mc.player == null || mc.world == null) return;
                switch (mode.getValue()) {
                    case OLD:
                        if (mc.player.isUsingItem()) {
                            if (ClientUtils.isMoving()) {
                                switch (event.getPhase()) {
                                    case PRE:
                                        mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, Direction.DOWN));
                                        break;

                                    case POST:
                                        mc.player.networkHandler.sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, 0, mc.player.getYaw(), mc.player.getPitch()));

                                        break;
                                }
                            } else {
                                mc.player.networkHandler.sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, 0, mc.player.getYaw(), mc.player.getPitch()));
                            }
                        }
                        break;
                    case NCP:
                        if (mc.player.isUsingItem() && ClientUtils.isMoving() && ClientUtils.isOnGround(0.42)) {
                            if (event.getPhase() == MotionEvent.Phase.PRE) {
                                mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, Direction.DOWN));
                            } else if (event.getPhase() == MotionEvent.Phase.POST) {
                                mc.player.networkHandler.sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, 0, mc.player.getYaw(), mc.player.getPitch()));
                            }
                            break;
                        }
                        break;
                    case VULCAN:
                        if (mc.player.getItemUseTime() <= 2 || mc.player.getItemUseTimeLeft() == 0) {
                            BlockPos pos = mc.player.getBlockPos();
                            mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.RELEASE_USE_ITEM, pos, Direction.UP));
                        }
                        break;
                }
            }
        });
    }
}
