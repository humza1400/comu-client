package me.comu.events;

import me.comu.api.registry.event.Event;
import net.minecraft.util.math.Vec3d;

public class MoveEvent extends Event {

    private Vec3d movement;

    public MoveEvent(Vec3d movement) {
        this.movement = movement;
    }

    public Vec3d getMovement() {
        return movement;
    }

    public void setMovement(Vec3d movement) {
        this.movement = movement;
    }

    public double getMotionX() {
        return movement.x;
    }

    public void setMotionX(double x) {
        this.movement = new Vec3d(x, movement.y, movement.z);
    }

    public double getMotionY() {
        return movement.y;
    }

    public void setMotionY(double y) {
        this.movement = new Vec3d(movement.x, y, movement.z);
    }

    public double getMotionZ() {
        return movement.z;
    }

    public void setMotionZ(double z) {
        this.movement = new Vec3d(movement.x, movement.y, z);
    }
}
