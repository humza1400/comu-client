package me.comu.module.impl.movement;

import me.comu.api.registry.event.Event;
import me.comu.api.registry.event.listener.Listener;
import me.comu.events.InputEvent;
import me.comu.events.MotionEvent;
import me.comu.module.Category;
import me.comu.module.ToggleableModule;
import me.comu.property.properties.BooleanProperty;
import me.comu.property.properties.EnumProperty;
import me.comu.utils.ClientUtils;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.option.KeyBinding;
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
                if (mc.player == null || mc.currentScreen == null || mc.currentScreen instanceof ChatScreen) {
                    return;
                }
                if (keyRotate.getValue()) {
                    float currentPitch = mc.player.getPitch();
                    float currentYaw = mc.player.getYaw();

                    float targetPitch = currentPitch;
                    float targetYaw = currentYaw;

                    if (ClientUtils.isKeyPressed(GLFW.GLFW_KEY_UP)) {
                        targetPitch -= 4f;
                    }
                    if (ClientUtils.isKeyPressed(GLFW.GLFW_KEY_DOWN)) {
                        targetPitch += 4f;
                    }
                    if (ClientUtils.isKeyPressed(GLFW.GLFW_KEY_LEFT)) {
                        targetYaw -= 5f;
                    }
                    if (ClientUtils.isKeyPressed(GLFW.GLFW_KEY_RIGHT)) {
                        targetYaw += 5f;
                    }

                    targetPitch = Math.max(-90f, Math.min(90f, targetPitch));

                    float smoothFactor = 0.4f;
                    float newPitch = currentPitch + (targetPitch - currentPitch) * smoothFactor;
                    float newYaw = currentYaw + (targetYaw - currentYaw) * smoothFactor;

                    mc.player.setPitch(newPitch);
                    mc.player.setYaw(newYaw);
                }
            }
        });
    }
}
