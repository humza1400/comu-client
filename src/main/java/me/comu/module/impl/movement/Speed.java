package me.comu.module.impl.movement;

import me.comu.api.registry.event.listener.Listener;
import me.comu.overrides.ducks.PlayerMovePacketDuck;
import me.comu.events.JumpEvent;
import me.comu.events.MotionEvent;
import me.comu.events.MoveEvent;
import me.comu.events.PacketEvent;
import me.comu.module.Category;
import me.comu.module.ToggleableModule;
import me.comu.property.properties.EnumProperty;
import me.comu.property.properties.NumberProperty;
import me.comu.utils.MoveUtils;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.potion.Potion;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.List;

import static me.comu.utils.MoveUtils.setMoveSpeed;

public class Speed extends ToggleableModule {

    NumberProperty<Float> speed = new NumberProperty<Float>("speed", List.of(), 1f, 0f, 2f, 0.1f);
    EnumProperty<Mode> mode = new EnumProperty<>("Mode", List.of("m"), Mode.VULCAN);

    public enum Mode {
        VULCAN, MODZ, TEST, BHOP
    }

    public Speed() {
        super("Speed", List.of(), Category.MOVEMENT, "You Go Zoooom");
        offerProperties(speed, mode);
        listeners.add(new Listener<>(MotionEvent.class) {
            @Override
            public void call(MotionEvent event) {
                switch (mode.getValue()) {
                    case MODZ:
                        if (MoveUtils.isMoving()) {
                            Vec3d velocity = mc.player.getVelocity();
                            EntityAttributeInstance attribute = mc.player.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
                            attribute.setBaseValue(speed.getValue());
                            if (mc.player.isOnGround()) {
                                mc.player.jump();
                            }
                        }
                        break;

                    case BHOP:
                        if(MoveUtils.isMoving()) {
                            if(mc.player.isOnGround()) {
                                mc.player.jump();
                            }
                            setMoveSpeed(speed.getValue());
                        }
                        break;

                    case VULCAN:
                        EntityAttributeInstance attribute = mc.player.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);

                        if (!MoveUtils.isMoving() || !mc.player.verticalCollision || mc.player.isHoldingOntoLadder())
                            return;

                        float speed;
                        float normalSpeed = 0.42f;
                        float strafeSpeed = 0.41f;
                        float potionSpeed = 0.56f;

                        if (mc.player.hasStatusEffect(StatusEffects.SPEED)) {
                            StatusEffectInstance effect = mc.player.getStatusEffect(StatusEffects.SPEED);
                            int amplifier = effect != null ? effect.getAmplifier() : 0;

                            speed = (amplifier > 0) ? potionSpeed : normalSpeed;
                            MoveUtils.setStrafe(speed);
                        } else if (mc.player.sidewaysSpeed != 0) {
                            speed = strafeSpeed;
                        } else {
                            speed = normalSpeed;
                        }

                        MoveUtils.setStrafe(speed);

                        double randomOffset = 0.0045 + Math.random() * 0.001;
                        mc.player.setVelocity(mc.player.getVelocity().x, randomOffset, mc.player.getVelocity().z);
                        break;
                }
            }
        });

        listeners.add(new Listener<>(PacketEvent.class) {
            @Override
            public void call(PacketEvent event) {
                if (mode.getValue() == Mode.VULCAN) {
                    if (event.getPacket() instanceof PlayerMoveC2SPacket.Full packet) {
                        ((PlayerMovePacketDuck) packet).setY(packet.getY(mc.player.getY()) + 0.005);
                    }
                }
            }
        });

        listeners.add(new Listener<>(JumpEvent.class) {
            @Override
            public void call(JumpEvent event) {
                if (mode.getValue() == Mode.VULCAN) {
                    event.setCancelled(true);
                }
            }
        });


        listeners.add(new Listener<>(MoveEvent.class) {
            @Override
            public void call(MoveEvent event) {
                if (mode.getValue() == Mode.TEST && MoveUtils.isMoving()) {
                    Vec3d movement = event.getMovement();
                    Vec3d direction = movement.normalize().multiply(2);
//                    event.setMotionX(2);
//                    event.setMotionZ(2);
                    event.setMovement(direction);
                }
            }
        });

    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (isPlayerOrWorldNull()) {
            return;
        }
        mc.options.getFovEffectScale().setValue(Math.min(1.0, Math.max(0.0, 0.0)));
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (isPlayerOrWorldNull()) {
            return;
        }
        EntityAttributeInstance attribute = mc.player.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
        attribute.setBaseValue(0.1);
        mc.options.getFovEffectScale().setValue(Math.min(1.0, Math.max(0.0, 1.0)));
    }

    @Override
    public String getSuffix() {
        return mode.getFormattedValue();
    }
}
