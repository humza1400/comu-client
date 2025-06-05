package me.comu.mixin.network;

import me.comu.Comu;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {

    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    private void sendChatMessage(String message, CallbackInfo ci) {
        if (message != null && message.startsWith(Comu.getInstance().getCommandManager().getPrefix())) {
            boolean handled = Comu.getInstance().getCommandManager().tryDispatch(message);
            if (handled) ci.cancel();
        }
    }
}
