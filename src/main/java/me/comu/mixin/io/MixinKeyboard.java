package me.comu.mixin.io;

import me.comu.Comu;
import me.comu.events.InputEvent;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class MixinKeyboard {

    @Inject(method = "onKey", at = @At("HEAD"), cancellable = true)
    private void onKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        if (action == GLFW.GLFW_PRESS) {
            if (MinecraftClient.getInstance().currentScreen != null) {
                return;
            }

            InputEvent inputEvent = new InputEvent(InputEvent.Type.KEYBOARD_KEY_PRESS, key);
            Comu.getInstance().getEventManager().dispatch(inputEvent);
            if (inputEvent.isCancelled()) {
                ci.cancel();
            }
        }
    }
}
