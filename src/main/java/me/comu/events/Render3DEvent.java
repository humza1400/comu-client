package me.comu.events;

import me.comu.api.registry.event.Event;
import net.minecraft.client.util.math.MatrixStack;

public class Render3DEvent extends Event {
    private final MatrixStack matrixStack;
    private final float tickDelta;
    private final double cameraX, cameraY, cameraZ;

    public Render3DEvent(MatrixStack matrixStack, float tickDelta, double cameraX, double cameraY, double cameraZ) {
        this.matrixStack = matrixStack;
        this.tickDelta = tickDelta;
        this.cameraX = cameraX;
        this.cameraY = cameraY;
        this.cameraZ = cameraZ;
    }

    public MatrixStack getMatrixStack() {
        return matrixStack;
    }

    public float getTickDelta() {
        return tickDelta;
    }

    public double getCameraX() {
        return cameraX;
    }

    public double getCameraY() {
        return cameraY;
    }

    public double getCameraZ() {
        return cameraZ;
    }
}

