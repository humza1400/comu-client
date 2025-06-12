package me.comu.module.impl.render;

import com.mojang.authlib.GameProfile;
import me.comu.Comu;
import me.comu.api.registry.event.Event;
import me.comu.api.registry.event.listener.Listener;
import me.comu.events.InputEvent;
import me.comu.events.MotionEvent;
import me.comu.events.MoveEvent;
import me.comu.module.Category;
import me.comu.module.ToggleableModule;
import me.comu.property.properties.NumberProperty;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.UUID;

public class Freecam extends ToggleableModule {

    private final NumberProperty<Double> speed = new NumberProperty<>("Speed", List.of("speed", "multiplier", "s"), 1.5, 0.1, 5.0, 0.1);
    private OtherClientPlayerEntity dummyPlayer;
    private Vec3d originalPos;
    private float originalYaw, originalPitch;

    public Freecam() {
        super("Freecam", List.of("noclip"), Category.RENDER, "Fly and clip through blocks freely");
        offerProperties(speed);
        listeners.add(new Listener<>(MotionEvent.class) {
            @Override
            public void call(MotionEvent event) {
                if (mc.player != null) {
                    mc.player.noClip = true;
                    mc.player.setVelocity(0, 0, 0);
                    mc.player.setPos(mc.player.getX(), mc.player.getY(), mc.player.getZ());
                }
            }
        });

//        listeners.add(new Listener<>(MoveEvent.class, event -> {
//            double moveSpeed = speed.getValue();
//            if (mc.options.jumpKey.isPressed()) {
//                event.setY(moveSpeed);
//            } else if (mc.options.sneakKey.isPressed()) {
//                event.setY(-moveSpeed);
//            } else {
//                event.setY(0);
//            }
//            event.setX(event.getX() * moveSpeed);
//            event.setZ(event.getZ() * moveSpeed);
//        }));

        listeners.add(new Listener<>(InputEvent.class) {
            @Override
            public void call(InputEvent event) {
                if (event.getType() == InputEvent.Type.MOUSE_SCROLL_UP) {
                    speed.setValue(Math.min(speed.getValue() + 0.1, speed.getMax()));
                } else if (event.getType() == InputEvent.Type.MOUSE_SCROLL_DOWN) {
                    speed.setValue(Math.max(speed.getValue() - 0.1, speed.getMin()));
                }
            }
        });
    }

    @Override
    public void onEnable() {
        if (mc.player == null || mc.world == null) return;

        originalPos = mc.player.getPos();
        originalYaw = mc.player.getYaw();
        originalPitch = mc.player.getPitch();

        dummyPlayer = new OtherClientPlayerEntity(mc.world, new GameProfile(UUID.randomUUID(), mc.player.getName().getString()));
        dummyPlayer.copyPositionAndRotation(mc.player);
        dummyPlayer.setYaw(mc.player.getYaw());
        dummyPlayer.setPitch(mc.player.getPitch());

        mc.world.addEntity(dummyPlayer);
    }

    @Override
    public void onDisable() {
        if (mc.player == null || mc.world == null) return;

        mc.player.setPos(originalPos.x, originalPos.y, originalPos.z);
        mc.player.setYaw(originalYaw);
        mc.player.setPitch(originalPitch);
        mc.player.noClip = false;

        if (dummyPlayer != null) {
            mc.world.removeEntity(dummyPlayer.getId(), Entity.RemovalReason.DISCARDED);
            dummyPlayer = null;
        }
    }
}
