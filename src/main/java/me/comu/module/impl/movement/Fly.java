package me.comu.module.impl.movement;

import me.comu.api.registry.event.listener.Listener;
import me.comu.events.MotionEvent;
import me.comu.module.Category;
import me.comu.module.ToggleableModule;
import me.comu.property.properties.EnumProperty;
import me.comu.property.properties.NumberProperty;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class Fly extends ToggleableModule {

    private NumberProperty<Integer> speed = new NumberProperty<Integer>("Speed", List.of("fs", "s"), 1, 1, 10, 1);
    private EnumProperty<Mode> mode = new EnumProperty<>("Mode", List.of("m"), Mode.VANILLA);

    public Fly() {
        super("Fly", List.of("flight"), Category.MOVEMENT, "I BELIEVE I CAN FLY");
        offerProperties(speed, mode);
        listeners.add(new Listener<>(MotionEvent.class) {
            @Override
            public void call(MotionEvent event) {

                switch (mode.getValue()) {
                    case CUSTOM:
                        float sped = speed.getValue();
                        if (mc.player != null && mc.world != null) {
                            mc.player.getAbilities().flying = false;
                            if (!mc.options.sneakKey.isPressed())
                                setMoveSpeedFly(speed.getValue(), mc.options.jumpKey.isPressed() ? 0.3f : 0);
                            if (mc.options.sneakKey.isPressed())
                                setMoveSpeedFly(speed.getValue(), -0.3F);
                        }
                        return;
                        case VANILLA:
                            mc.player.getAbilities().flying = true;
                            return;
                }
            }
        });
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        mc.player.getAbilities().flying = false;
    }

    private void setMoveSpeedFly(double speed, double motionY) {
        ClientPlayerEntity player = mc.player;

        if (player == null) return;

        Vec2f movementInput = player.input.getMovementInput();
        float forward = movementInput.y;
        float strafe = movementInput.x;
        float yaw = player.getYaw();

        if (forward == 0.0F && strafe == 0.0F) {
            player.setVelocity(Vec3d.ZERO);
        } else {
            if (forward != 0.0F) {
                if (strafe > 0.0F) {
                    yaw += (forward > 0.0F ? -45F : 45F);
                } else if (strafe < 0.0F) {
                    yaw += (forward > 0.0F ? 45F : -45F);
                }

                strafe = 0.0F;

                if (forward > 0.0F) {
                    forward = 1.0F;
                } else if (forward < 0.0F) {
                    forward = -1.0F;
                }
            }

            double rad = Math.toRadians(yaw);
            double sin = -Math.sin(rad);
            double cos = Math.cos(rad);

            double motionX = forward * speed * sin + strafe * speed * cos;
            double motionZ = forward * speed * cos - strafe * speed * sin;

            player.setVelocity(motionX, motionY, motionZ);
        }
    }

    public enum Mode {VANILLA, CUSTOM, TESTING}
}
