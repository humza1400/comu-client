package me.comu.events;

import me.comu.api.registry.event.Event;
import net.minecraft.entity.Entity;

public class SprintAttackEvent extends Event {
    private final Entity target;

    public SprintAttackEvent(Entity target) {
        this.target = target;
    }

    public Entity getTarget() {
        return target;
    }
}
