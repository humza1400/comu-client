package me.comu.mixin.io;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.comu.Comu;
import me.comu.module.impl.movement.InventoryMove;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(KeyboardInput.class)
public abstract class MixinKeyboardInput{
    @Shadow
    @Final
    private GameOptions settings;

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;isPressed()Z"))
    private boolean allowInventoryMovement(KeyBinding key, Operation<Boolean> original) {
        boolean pressed = original.call(key);

        InventoryMove inventoryMove = Comu.getInstance().getModuleManager().getModule(InventoryMove.class);
        MinecraftClient mc = MinecraftClient.getInstance();

        if (inventoryMove.isEnabled()
                && mc.currentScreen != null
                && !(mc.currentScreen instanceof ChatScreen)
                && isMovementKey(key)) {

            long handle = mc.getWindow().getHandle();
            return pressed || InputUtil.isKeyPressed(handle, key.getDefaultKey().getCode());
        }

        return pressed;
    }

    private boolean isMovementKey(KeyBinding key) {
        return key == settings.forwardKey ||
                key == settings.backKey ||
                key == settings.leftKey ||
                key == settings.rightKey ||
                key == settings.jumpKey ||
                key == settings.sprintKey;
    }
}
