package me.comu.events;

import me.comu.api.registry.event.Event;
import me.comu.render.Renderer3D;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;


public class Render3DEvent extends Event {

    public MatrixStack matrixStack;
    private final VertexConsumerProvider.Immediate consumers;
    public float tickDelta;
    public double offsetX, offsetY, offsetZ;

    public Render3DEvent(MatrixStack matrixStack, VertexConsumerProvider.Immediate consumers, float tickDelta, double offsetX, double offsetY, double offsetZ) {
        this.matrixStack = matrixStack;
        this.consumers = consumers;
        this.tickDelta = tickDelta;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
    }

    public MatrixStack getMatrixStack() {
        return matrixStack;
    }

    public VertexConsumerProvider.Immediate getConsumers() {
        return consumers;
    }

    public float getTickDelta() {
        return tickDelta;
    }

    public double getCameraX() {
        return offsetX;
    }

    public double getCameraY() {
        return offsetY;
    }

    public double getCameraZ() {
        return offsetZ;
    }
}
