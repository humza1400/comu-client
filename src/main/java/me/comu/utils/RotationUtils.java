package me.comu.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;

public final class RotationUtils {

    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static float[] getRotations(Entity entity) {
        final double x = entity.getX();
        final double y = entity.getY() + entity.getEyeHeight(entity.getPose());
        final double z = entity.getZ();
        return getRotationFromPosition(x, y, z);
    }

    public static float getDistanceBetweenAngles(float angle1, float angle2) {
        float diff = (angle1 - angle2) % 360.0f;
        if (diff < -180.0f) {
            diff += 360.0f;
        } else if (diff > 180.0f) {
            diff -= 360.0f;
        }
        return Math.abs(diff);
    }

    public static float[] getRotationFromPosition(double x, double y, double z) {
        if (mc.player == null) { return null; }

        double px = mc.player.getX();
        double py = mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose());
        double pz = mc.player.getZ();

        double dx = x - px;
        double dy = y - py;
        double dz = z - pz;

        double dist = Math.sqrt(dx * dx + dz * dz);

        float yaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0F);
        float pitch = (float) (-Math.toDegrees(Math.atan2(dy, dist)));

        return new float[]{yaw, pitch};
    }

    public static float getAngleDelta(float from, float to) {
        float delta = ((to - from + 540f) % 360f) - 180f;
        return delta;
    }


}
