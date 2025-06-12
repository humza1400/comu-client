package me.comu.module.impl.movement;

import me.comu.api.registry.event.listener.Listener;
import me.comu.events.MotionEvent;
import me.comu.module.Category;
import me.comu.module.ToggleableModule;
import me.comu.property.properties.BooleanProperty;
import me.comu.utils.ClientUtils;
import net.minecraft.client.gui.screen.ChatScreen;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class InventoryMove extends ToggleableModule {

    private final BooleanProperty keyRotate = new BooleanProperty("Key Rotate", List.of("keyrotate", "key", "rotate"), true);

    public InventoryMove() {
        super("Inventory Move", List.of("invmove", "invwalk", "inventorymove", "inventorywalk"), Category.MOVEMENT, "Allows you to move while in inventory screens");
        offerProperties(keyRotate);
        listeners.add(new Listener<>(MotionEvent.class) {
            @Override
            public void call(MotionEvent event) {
                if (mc.player == null || mc.currentScreen == null || mc.currentScreen instanceof ChatScreen)
                    return;

                if (!keyRotate.getValue()) return;

                float yawDelta = 0f;
                float pitchDelta = 0f;

                if (ClientUtils.isKeyPressed(GLFW.GLFW_KEY_UP)) pitchDelta -= 8f;
                if (ClientUtils.isKeyPressed(GLFW.GLFW_KEY_DOWN)) pitchDelta += 8f;
                if (ClientUtils.isKeyPressed(GLFW.GLFW_KEY_LEFT)) yawDelta -= 10f;
                if (ClientUtils.isKeyPressed(GLFW.GLFW_KEY_RIGHT)) yawDelta += 10f;

                if (yawDelta == 0f && pitchDelta == 0f) return;
                float smoothFactor = 0.2f;

                float newYaw = mc.player.getYaw() + yawDelta * smoothFactor;
                float newPitch = mc.player.getPitch() + pitchDelta * smoothFactor;
                newPitch = Math.max(-90f, Math.min(90f, newPitch));

                mc.player.setYaw(newYaw);
                mc.player.lastYaw = newYaw;
                mc.player.setPitch(newPitch);
                mc.player.lastPitch = newPitch;
            }
        });
    }
}