package me.comu.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class MovementUtils {

    public static void setMoveSpeedFly(double speed, double motionY) {
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientPlayerEntity player = mc.player;

        if (player == null) return;

        Vec2f movementInput = player.input.getMovementInput();
        float forward = movementInput.y;
        float strafe = movementInput.x;
        float yaw = player.getYaw();

        if (forward == 0.0F && strafe == 0.0F) {
            player.setVelocity(Vec3d.ZERO);
        } else {
            if (forward != 0.0F) {
                if (strafe > 0.0F) {
                    yaw += (forward > 0.0F ? -45F : 45F);
                } else if (strafe < 0.0F) {
                    yaw += (forward > 0.0F ? 45F : -45F);
                }

                strafe = 0.0F;

                if (forward > 0.0F) {
                    forward = 1.0F;
                } else if (forward < 0.0F) {
                    forward = -1.0F;
                }
            }

            double rad = Math.toRadians(yaw);
            double sin = -Math.sin(rad);
            double cos = Math.cos(rad);

            double motionX = forward * speed * sin + strafe * speed * cos;
            double motionZ = forward * speed * cos - strafe * speed * sin;

            player.setVelocity(motionX, motionY, motionZ);
        }
    }

    public static void setMoveSpeed(double speed) {
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientPlayerEntity player = mc.player;

        if (player == null) return;

        Vec2f movementInput = player.input.getMovementInput();
        float forward = movementInput.y;
        float strafe = movementInput.x;
        float yaw = player.getYaw();

        if (forward == 0.0F && strafe == 0.0F) {
            player.setVelocity(Vec3d.ZERO);
        } else {
            if (forward != 0.0F) {
                if (strafe > 0.0F) {
                    yaw += (forward > 0.0F ? -45F : 45F);
                } else if (strafe < 0.0F) {
                    yaw += (forward > 0.0F ? 45F : -45F);
                }

                strafe = 0.0F;

                if (forward > 0.0F) {
                    forward = 1.0F;
                } else if (forward < 0.0F) {
                    forward = -1.0F;
                }
            }

            double rad = Math.toRadians(yaw);
            double sin = -Math.sin(rad);
            double cos = Math.cos(rad);

            double motionX = forward * speed * sin + strafe * speed * cos;
            double motionZ = forward * speed * cos - strafe * speed * sin;

            player.setVelocity(motionX, player.getVelocity().y, motionZ);
        }
    }

    public static void setStrafe(float speed) {
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientPlayerEntity player = mc.player;
        if (player == null) return;

        float yaw = player.getYaw();
        Vec2f moveVec = mc.player.input.getMovementInput();
        float forward = moveVec.y;
        float strafe = moveVec.x;

        if (forward == 0.0f && strafe == 0.0f) {
            player.setVelocity(0.0, player.getVelocity().y, 0.0);
            return;
        }

        if (forward != 0.0f) {
            if (strafe > 0.0f) {
                yaw += (forward > 0.0f) ? -45.0f : 45.0f;
            } else if (strafe < 0.0f) {
                yaw += (forward > 0.0f) ? 45.0f : -45.0f;
            }
            strafe = 0.0f;

            forward = (forward > 0.0f) ? 1.0f : -1.0f;
        }

        double rad = Math.toRadians(yaw + 90.0f);
        double sin = Math.sin(rad);
        double cos = Math.cos(rad);

        double motionX = forward * speed * cos + strafe * speed * sin;
        double motionZ = forward * speed * sin - strafe * speed * cos;

        player.setVelocity(motionX, player.getVelocity().y, motionZ);
    }

}
