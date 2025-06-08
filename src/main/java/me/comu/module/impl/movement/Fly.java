package me.comu.module.impl.movement;

import me.comu.api.registry.event.listener.Listener;
import me.comu.events.MotionEvent;
import me.comu.module.Category;
import me.comu.module.ToggleableModule;
import me.comu.property.properties.EnumProperty;
import me.comu.property.properties.NumberProperty;
import me.comu.utils.MovementUtils;
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
                if (isPlayerOrWorldNull(mc)) return;
                switch (mode.getValue()) {
                    case CUSTOM:
                        float sped = speed.getValue();
                        if (mc.player != null && mc.world != null) {
                            mc.player.getAbilities().flying = false;
                            if (!mc.options.sneakKey.isPressed())
                                MovementUtils.setMoveSpeedFly(speed.getValue(), mc.options.jumpKey.isPressed() ? 0.3f : 0);
                            if (mc.options.sneakKey.isPressed())
                                MovementUtils.setMoveSpeedFly(speed.getValue(), -0.3F);
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
        if (isPlayerOrWorldNull(mc)) { return; }
        mc.player.getAbilities().flying = false;
    }


    public enum Mode {VANILLA, CUSTOM, TESTING}
}
