package me.comu.events;

import me.comu.api.registry.event.Event;
import net.minecraft.util.math.Vec3d;

public class MotionEvent extends Event {
    private double x, y, z;
    private float yaw, pitch;
    private boolean onGround;

    private Phase phase;

    public enum Phase {PRE, POST}

    public MotionEvent(Phase phase, double x, double y, double z, float yaw, float pitch, boolean onGround) {
        this.phase = phase;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
    }

    public Vec3d getPos() {
        return new Vec3d(x, y, z);
    }

    public Event setPhase(Phase phase) {
        this.phase = phase;
        return this;
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

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }
}
