package me.comu.events;

import me.comu.api.registry.event.Event;
import net.minecraft.client.gui.DrawContext;

public class Render2DEvent extends Event {
    private final DrawContext context;
    private final float tickDelta;
    private final int screenWidth, screenHeight;

    public Render2DEvent(DrawContext context, float tickDelta, int screenWidth, int screenHeight) {
        this.context = context;
        this.tickDelta = tickDelta;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    public DrawContext getContext() { return context; }
    public float getTickDelta() { return tickDelta; }
    public int getScreenWidth() { return screenWidth; }
    public int getScreenHeight() { return screenHeight; }
}
