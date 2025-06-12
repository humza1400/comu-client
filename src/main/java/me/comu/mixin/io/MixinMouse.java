package me.comu.mixin.io;

import me.comu.Comu;
import me.comu.events.InputEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MixinMouse {

    @Inject(method = "onMouseButton", at = @At("HEAD"), cancellable = true)
    private void onMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
        if (action == GLFW.GLFW_PRESS && MinecraftClient.getInstance().currentScreen == null) {
            InputEvent.Type type = switch (button) {
                case GLFW.GLFW_MOUSE_BUTTON_LEFT -> InputEvent.Type.MOUSE_LEFT_CLICK;
                case GLFW.GLFW_MOUSE_BUTTON_RIGHT -> InputEvent.Type.MOUSE_RIGHT_CLICK;
                case GLFW.GLFW_MOUSE_BUTTON_MIDDLE -> InputEvent.Type.MOUSE_MIDDLE_CLICK;
                case GLFW.GLFW_MOUSE_BUTTON_4 -> InputEvent.Type.MOUSE_BUTTON_4_CLICK;
                case GLFW.GLFW_MOUSE_BUTTON_5 -> InputEvent.Type.MOUSE_BUTTON_5_CLICK;
                default -> InputEvent.Type.MOUSE_EXTRA_BUTTON;
            };
            InputEvent event = new InputEvent(type, button);
            Comu.getInstance().getEventManager().dispatch(event);
            if (event.isCancelled()) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "onMouseScroll", at = @At("HEAD"), cancellable = true)
    private void onMouseScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        if (MinecraftClient.getInstance().currentScreen == null && vertical != 0) {
            InputEvent.Type type = vertical > 0 ? InputEvent.Type.MOUSE_SCROLL_UP : InputEvent.Type.MOUSE_SCROLL_DOWN;
            InputEvent event = new InputEvent(type, (int) vertical);
            Comu.getInstance().getEventManager().dispatch(event);
            if (event.isCancelled()) {
                ci.cancel();
            }
        }
    }
}
