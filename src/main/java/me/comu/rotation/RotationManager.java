package me.comu.rotation;

import me.comu.module.ToggleableModule;
import net.minecraft.client.MinecraftClient;

public class RotationManager {

    public enum Priority {
        LOW(0), NORMAL(1), HIGH(2);

        private final int level;

        Priority(int level) {
            this.level = level;
        }

        public int getLevel() {
            return level;
        }
    }

    private float serverYaw, serverPitch;
    private float cameraYaw, cameraPitch;
    private boolean isRotating;
    private Priority currentPriority = Priority.LOW;
    private ToggleableModule provider;

    private final MinecraftClient mc = MinecraftClient.getInstance();

    public void setRotation(float yaw, float pitch, Priority priority, ToggleableModule source) {
        if (priority.getLevel() >= currentPriority.getLevel() || provider == source) {
            this.serverYaw = yaw;
            this.serverPitch = pitch;
            this.currentPriority = priority;
            this.provider = source;
            this.isRotating = true;
        }
    }

    public void setCameraRotation(float yaw, float pitch) {
        this.cameraYaw = yaw;
        this.cameraPitch = pitch;
    }

    public void syncCameraToPlayer() {
        if (mc.player != null) {
            this.cameraYaw = mc.player.getYaw();
            this.cameraPitch = mc.player.getPitch();
        }
    }

    public float getServerYaw() {
        return serverYaw;
    }

    public float getServerPitch() {
        return serverPitch;
    }

    public float getCameraYaw() {
        return cameraYaw;
    }

    public float getCameraPitch() {
        return cameraPitch;
    }

    public boolean isRotating() {
        return isRotating;
    }

    public Priority getPriority() {
        return currentPriority;
    }

    public ToggleableModule getProvider() {
        return provider;
    }

    public void reset() {
        this.isRotating = false;
        this.currentPriority = Priority.LOW;
        this.provider = null;
    }
}
