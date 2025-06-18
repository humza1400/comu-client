package me.comu.module.impl.miscellaneous;

import me.comu.Comu;
import me.comu.api.registry.event.listener.Listener;
import me.comu.api.stopwatch.Stopwatch;
import me.comu.events.PacketEvent;
import me.comu.events.TickEvent;
import me.comu.module.Category;
import me.comu.module.ToggleableModule;
import me.comu.notification.Notification;
import me.comu.notification.NotificationType;
import me.comu.property.properties.BooleanProperty;
import me.comu.property.properties.InputProperty;
import me.comu.property.properties.NumberProperty;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class LagDetector extends ToggleableModule {

    private final NumberProperty<Integer> threshold = new NumberProperty<>("Threshold (ms)", List.of("threshold", "delay", "timeout"), 2000, 500, 10000, 100);

    private final Stopwatch stopwatch = new Stopwatch();
    private boolean lagging = false;

    public LagDetector() {
        super("Lag Detector", List.of("lagdetector", "lagnotifier", "lagnotify", "lagdetect"), Category.MISCELLANEOUS, "Notifies you when you stop receiving packets from the server");
        offerProperties(threshold);
        listeners.add(new Listener<>(PacketEvent.class) {
            @Override
            public void call(PacketEvent event) {
                if (isPlayerOrWorldNull()) return;
                stopwatch.reset();
                lagging = false;
            }
        });

        listeners.add(new Listener<>(TickEvent.class) {
            @Override
            public void call(TickEvent event) {
                if (isPlayerOrWorldNull()) return;

                if (!lagging && stopwatch.hasCompleted(threshold.getValue())) {
                    lagging = true;
                    Comu.getInstance().getNotificationManager().notify(new Notification(NotificationType.WARNING, "Lag Detector", "No packets received for " + (threshold.getValue() / 1000.0) + "s!"));
                }
            }
        });
    }

    @Override
    public String getSuffix() {
        return threshold.getValue() / 1000 + "s";
    }
}
