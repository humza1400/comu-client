package me.comu.notification;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Formatting;

import java.awt.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class NotificationManager {

    private final List<Notification> notifications = new CopyOnWriteArrayList<>();
    private static final int WIDTH = 180;
    private static final int HEIGHT = 30;
    private static final int SPACING = 5;

    public void notify(Notification notification) {
        notifications.add(notification);
    }

    public void render(DrawContext context) {
        long now = System.currentTimeMillis();
        MinecraftClient mc = MinecraftClient.getInstance();

        int screenWidth = context.getScaledWindowWidth();
        int screenHeight = context.getScaledWindowHeight();

        int x = screenWidth - WIDTH - 10;
        int y = screenHeight - 50;

        for (int i = notifications.size() - 1; i >= 0; i--) {
            Notification notification = notifications.get(i);
            if (!notification.isAlive(now)) {
                continue;
            }

            float progress = notification.getProgress(now);
            int alpha = (int) (200 * progress);
            int fullAlpha = (int) (255 * progress);

            int notifY = y - HEIGHT;

            int bgColor = new Color(30, 30, 30, alpha).getRGB();
            context.fill(x, notifY, x + WIDTH, notifY + HEIGHT, bgColor);

            int borderColor = new Color(255, 107, 107, alpha).getRGB();
            context.fill(x, notifY, x + WIDTH, notifY + 1, borderColor);
            context.fill(x, notifY + HEIGHT - 1, x + WIDTH, notifY + HEIGHT, borderColor);
            context.fill(x, notifY, x + 1, notifY + HEIGHT, borderColor);
            context.fill(x + WIDTH - 1, notifY, x + WIDTH, notifY + HEIGHT, borderColor);

            Color typeColor = notification.getType().getColor();
            int typeColorWithAlpha = (typeColor.getRGB() & 0x00FFFFFF) | (fullAlpha << 24);
            context.fill(x + 2, notifY + 2, x + 5, notifY + HEIGHT - 2, typeColorWithAlpha);

            context.drawText(mc.textRenderer, Formatting.BOLD + notification.getTitle(), x + 8, notifY + 5, 0xFFFFFF | (fullAlpha << 24), true);
            context.drawText(mc.textRenderer, notification.getMessage(), x + 8, notifY + 17, 0xAAAAAA | (fullAlpha << 24), true);

            y -= HEIGHT + SPACING;
        }

        notifications.removeIf(notification -> !notification.isAlive(now));
    }
}