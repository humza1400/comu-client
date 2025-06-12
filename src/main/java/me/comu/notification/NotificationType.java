package me.comu.notification;

import java.awt.*;

public enum NotificationType {
    POSITIVE(new Color(140, 215, 144)),
    WARNING(new Color(255, 224, 102)),
    NEGATIVE(new Color(255, 107, 107));

    private final Color color;

    NotificationType(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}