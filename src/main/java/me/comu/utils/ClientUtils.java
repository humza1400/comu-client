package me.comu.utils;

import me.comu.Comu;
import me.comu.render.Renderer2D;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.Packet;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import org.lwjgl.glfw.GLFW;

public class ClientUtils {

    private static MinecraftClient mc = MinecraftClient.getInstance();

    public static int getKeyCodeByName(String name) {
        try {
            return (int) GLFW.class.getField("GLFW_KEY_" + name).get(null);
        } catch (Exception ignored) {
            return GLFW.GLFW_KEY_UNKNOWN;
        }
    }

    public static String getKeyName(int keyCode) {
        for (var field : GLFW.class.getFields()) {
            try {
                if (field.getName().startsWith("GLFW_KEY_") && (int) field.get(null) == keyCode) {
                    return field.getName().replace("GLFW_KEY_", "");
                }
            } catch (Exception ignored) {
            }
        }
        return "UNKNOWN";
    }

    public static boolean isKeyPressed(int key) {
        long windowHandle = MinecraftClient.getInstance().getWindow().getHandle();
        return GLFW.glfwGetKey(windowHandle, key) == GLFW.GLFW_PRESS;
    }


    public static boolean isMouseButtonDown(int button) {
        long windowHandle = MinecraftClient.getInstance().getWindow().getHandle();
        return GLFW.glfwGetMouseButton(windowHandle, button) == GLFW.GLFW_PRESS;
    }

    public record Triple<A, B, C>(A first, B second, C third) {
    }

    public static void packet(Packet<?> packet) {
        if (mc.getNetworkHandler() == null) {
            return;
        }
        mc.getNetworkHandler().sendPacket(packet);
    }

    public static boolean isOnGround(double height) {
        if (mc.player == null || mc.world == null) return false;

        Box box = mc.player.getBoundingBox().offset(0.0, -height, 0.0);
        return mc.world.getBlockCollisions(mc.player, box).iterator().hasNext();
    }

    public static String trimTextToFit(String text, int maxWidth) {
        int i = text.length();
        while (i > 0 && Renderer2D.getStringWidth(text.substring(0, i)) > maxWidth) i--;
        return text.substring(0, i);
    }

    public static Identifier identifier(String path) {
        return Identifier.of(Comu.getClientName(), path);
    }

}
