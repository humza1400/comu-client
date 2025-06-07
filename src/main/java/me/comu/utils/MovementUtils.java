package me.comu.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class MovementUtils {
    MinecraftClient mc = MinecraftClient.getInstance();

    private void setMoveSpeedFly(double speed, double motionY) {
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

    private void setMoveSpeed(double speed) {
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

}
