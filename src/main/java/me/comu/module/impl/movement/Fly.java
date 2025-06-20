package me.comu.module.impl.movement;

import me.comu.api.registry.event.listener.Listener;
import me.comu.events.MotionEvent;
import me.comu.module.Category;
import me.comu.module.ToggleableModule;
import me.comu.property.properties.EnumProperty;
import me.comu.property.properties.NumberProperty;
import me.comu.utils.MoveUtils;

import java.util.List;

public class Fly extends ToggleableModule {

    private NumberProperty<Integer> speed = new NumberProperty<>("Speed", List.of("fs", "s"), 1, 1, 10, 1);
    private NumberProperty<Float> ySpeed = new NumberProperty<>("Y-Speed", List.of("y", "ySpeed"), 0.3f, 0f, 10f, 0.1f);
    private EnumProperty<Mode> mode = new EnumProperty<>("Mode", List.of("m"), Mode.VANILLA);

    public Fly() {
        super("Fly", List.of("flight"), Category.MOVEMENT, "I BELIEVE I CAN FLY");
        offerProperties(speed, mode, ySpeed);
        listeners.add(new Listener<>(MotionEvent.class) {
            @Override
            public void call(MotionEvent event) {
                if (isPlayerOrWorldNull()) return;
                switch (mode.getValue()) {
                    case CUSTOM:
                        float sped = speed.getValue();
                        if (mc.player != null && mc.world != null) {
                            mc.player.getAbilities().flying = false;
                            if (!mc.options.sneakKey.isPressed())
                                MoveUtils.setMoveSpeedFly(speed.getValue(), mc.options.jumpKey.isPressed() ? ySpeed.getValue() : 0);
                            if (mc.options.sneakKey.isPressed())
                                MoveUtils.setMoveSpeedFly(speed.getValue(), -ySpeed.getValue());
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
        if (isPlayerOrWorldNull()) { return; }
        mc.player.getAbilities().flying = false;
    }


    public enum Mode {VANILLA, CUSTOM, TESTING}
}
