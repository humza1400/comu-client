package me.comu.rotation;

import net.minecraft.client.MinecraftClient;

public final class RotationManager {
    public enum Priority {
        LOW(0),
        MEDIUM(1),
        HIGH(2);

        private final int level;

        Priority(int level) {
            this.level = level;
        }

        public int getLevel() {
            return level;
        }
    }

    private float yaw, pitch;
    private boolean isRotating;
    private Priority currentPriority = Priority.LOW;
    private Class<?> source;

    public void setRotations(float yaw, float pitch, Priority priority, Class<?> source) {
        if (!isRotating || priority.getLevel() >= currentPriority.getLevel()) {
            this.yaw = yaw;
            this.pitch = pitch;
            this.isRotating = true;
            this.currentPriority = priority;
            this.source = source;
        }
    }

    public void reset() {
        this.isRotating = false;
        this.currentPriority = Priority.LOW;
    }

    public boolean isRotating() {
        return isRotating;
    }

    public float getRenderYaw() {
        return isRotating ? yaw : MinecraftClient.getInstance().player.getYaw();
    }

    public float getRenderPitch() {
        return isRotating ? pitch : MinecraftClient.getInstance().player.getPitch();
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public Priority getCurrentPriority() {
        return currentPriority;
    }

    public Class<?> getSource() {
        return source;
    }
}
