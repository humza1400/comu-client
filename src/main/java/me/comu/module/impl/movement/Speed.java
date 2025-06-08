package me.comu.module.impl.movement;

import me.comu.api.registry.event.listener.Listener;
import me.comu.events.MotionEvent;
import me.comu.events.PacketEvent;
import me.comu.events.TickEvent;
import me.comu.logging.Logger;
import me.comu.module.Category;
import me.comu.module.ToggleableModule;
import me.comu.property.properties.NumberProperty;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class Speed extends ToggleableModule{

    NumberProperty<Float> speed = new NumberProperty<Float>("speed", List.of(), 1f, 0f, 2f, 0.1f);
    public Speed() {
        super("Speed", List.of(), Category.MOVEMENT, "You Go Zoooom");
        offerProperties(speed);
        listeners.add(new Listener<>(MotionEvent.class) {
            @Override
            public void call(MotionEvent event) {
                if(isMoving()) {
                    Vec3d velocity = mc.player.getVelocity();
                    EntityAttributeInstance attribute = mc.player.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
                    attribute.setBaseValue(speed.getValue());
                    if(mc.player.isOnGround()) {
                        mc.player.jump();
                    }
                }
            }
        });
    }
    @Override
    public void onEnable() {
        super.onEnable();
        if (isPlayerOrWorldNull(mc)) { return; }
        mc.options.getFovEffectScale().setValue(Math.min(1.0, Math.max(0.0, 0.0)));
    }
    @Override
    public void onDisable() {
        super.onDisable();
        if (isPlayerOrWorldNull(mc)) { return; }
        EntityAttributeInstance attribute = mc.player.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
        attribute.setBaseValue(0.1);
        mc.options.getFovEffectScale().setValue(Math.min(1.0, Math.max(0.0, 1.0)));
    }

    private boolean isMoving(){
        assert mc.player != null;
        return mc.player.input.getMovementInput().x != 0.0f || mc.player.input.getMovementInput().y != 0.0f;

    }

}
