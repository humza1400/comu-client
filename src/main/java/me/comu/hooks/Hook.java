package me.comu.hooks;

import me.comu.Comu;
import me.comu.events.TickEvent;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public final class Hook {

    // todo: refactor these into KeybindProperties
    private static KeyBinding zoomKeybind;
    private static KeyBinding freelookKeybind;

    private Hook() {
    }

    public static void init() {
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            Comu.getInstance().getEventManager().dispatch(new TickEvent(TickEvent.Phase.PRE));
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            Comu.getInstance().getEventManager().dispatch(new TickEvent(TickEvent.Phase.POST));
        });

        zoomKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.comu.zoom", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_CONTROL, "key.categories.gameplay"));
        freelookKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.comu.freelook", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_ALT, "key.categories.gameplay"));
    }

    public static KeyBinding getZoomKeybind() {
        return zoomKeybind;
    }

    public static KeyBinding getFreelookKeybind() {
        return freelookKeybind;
    }
}
