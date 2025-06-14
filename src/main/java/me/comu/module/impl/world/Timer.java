package me.comu.module.impl.world;

import me.comu.api.registry.event.listener.Listener;
import me.comu.events.TickEvent;
import me.comu.module.Category;
import me.comu.module.ToggleableModule;
import me.comu.property.properties.BooleanProperty;
import me.comu.property.properties.NumberProperty;

import java.util.List;

public class Timer extends ToggleableModule {

    NumberProperty<Float> timer = new NumberProperty<>("Multiplier", List.of("speed", "t", "s", "timerspeed", "timer"), 1.0f, 0.1f, 10.0f, 0.25f);
    NumberProperty<Float> pulseSpeed = new NumberProperty<>("Pulse Speed", List.of("pulsespeed", "pspeed", "ps"), 1.25f, 0.1f, 10.0f, 0.25f);
    NumberProperty<Integer> pulseTicks = new NumberProperty<>("Pulse Ticks", List.of("pulseticks", "pticks", "pt", "pulsetick"), 10, 1, 100, 5);
    NumberProperty<Integer> pulseCooldown = new NumberProperty<>("Pulse Cooldown", List.of("pulsecooldown", "pulsecd", "pcd", "pc", "cooldown", "cd"), 40, 1, 200, 10);
    BooleanProperty pulse = new BooleanProperty("Pulse", List.of("p"), false);

    float timerSpeed = 1.0f;
    int tickCounter = 0;

    public Timer() {
        super("Timer", List.of("timerspeed"), Category.WORLD, "Speeds up everything in the game");
        offerProperties(timer, pulse, pulseSpeed, pulseCooldown);
        listeners.add(new Listener<>(TickEvent.class) {
            @Override
            public void call(TickEvent event) {
                if (pulse.getValue()) {
                    tickCounter++;

                    if (tickCounter <= pulseTicks.getValue()) {
                        timerSpeed = pulseSpeed.getValue();
                    } else if (tickCounter <= pulseTicks.getValue() + pulseCooldown.getValue()) {
                        timerSpeed = 1.0f;
                    } else {
                        tickCounter = 0;
                    }

                    timer.setValue(timerSpeed);
                }
            }
        });
    }

    @Override
    public String getSuffix() {
        return timer.getValue().toString();
    }
}
