package me.comu.events;

import me.comu.api.registry.event.Event;

public class MotionEvent extends Event {
    private final Phase phase;
    public enum Phase { PRE, POST}

    public MotionEvent(Phase phase) {
        this.phase = phase;
    }

    public Phase getPhase() {
        return phase;
    }

    public boolean isPre() {
        return phase == Phase.PRE;
    }

    public boolean isPost() {
        return phase == Phase.POST;
    }
}
