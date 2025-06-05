package me.comu.module.impl.movement;

import me.comu.Comu;
import me.comu.api.registry.event.listener.Listener;
import me.comu.events.MotionEvent;
import me.comu.module.Category;
import me.comu.module.ToggleableModule;

import java.util.List;

public class Sprint extends ToggleableModule {

    public Sprint() {
        super("Sprint", List.of("autorun", "autosprint"), Category.MOVEMENT, "Automatically sprints for you");
        listeners.add(new Listener<>(MotionEvent.class) {
            @Override
            public void call(MotionEvent event) {
                if (event.isPre() && mc.player != null && mc.player.input.hasForwardMovement() && !mc.player.isSneaking() && !mc.player.isUsingItem() && !mc.player.horizontalCollision) {
                    mc.player.setSprinting(true);
                }
            }
        });
    }
}
