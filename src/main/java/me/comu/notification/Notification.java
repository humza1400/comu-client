package me.comu.notification;

public class Notification {
    private final String title;
    private final String message;
    private final NotificationType type;
    private final long startTime;
    private final long duration;

    public Notification(NotificationType type, String title, String message, int durationSeconds) {
        this.title = title;
        this.message = message;
        this.type = type;
        this.duration = durationSeconds * 1000L;
        this.startTime = System.currentTimeMillis();
    }

    public Notification(NotificationType type, String title, String message) {
        this.title = title;
        this.message = message;
        this.type = type;
        this.duration = 3000L;
        this.startTime = System.currentTimeMillis();
    }

    public boolean isAlive(long now) {
        return now - startTime < duration;
    }

    public float getProgress(long now) {
        long elapsed = now - startTime;
        if (elapsed >= duration) return 0;
        return elapsed < 200 ? elapsed / 200f : (duration - elapsed < 200 ? (duration - elapsed) / 200f : 1f);
    }

    public NotificationType getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }
}