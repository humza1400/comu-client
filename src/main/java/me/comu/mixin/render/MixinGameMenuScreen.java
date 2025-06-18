package me.comu.mixin.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameMenuScreen.class)
public abstract class MixinGameMenuScreen extends Screen {

    protected MixinGameMenuScreen() {
        super(Text.empty());
    }

    @Shadow
    @Nullable
    private ButtonWidget exitButton;

    @Inject(method = "initWidgets", at = @At("HEAD"), cancellable = true)
    private void cancelIfPlayerNull(CallbackInfo ci) {
        MinecraftClient mc = MinecraftClient.getInstance();

        if (mc.player == null) {
            Screen parent = new TitleScreen();

            if (mc.getCurrentServerEntry() != null && !mc.getCurrentServerEntry().isRealm()) {
                parent = new net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen(new TitleScreen());
            }

            mc.setScreen(parent);
            ci.cancel();
        }
    }


    @Inject(method = "initWidgets", at = @At("TAIL"))
    private void onInitWidgets(CallbackInfo ci) {
        MinecraftClient mc = MinecraftClient.getInstance();

        if (mc.player == null || mc.isInSingleplayer() || mc.getCurrentServerEntry() == null) {
            return;
        }

        ButtonWidget reconnectButton = ButtonWidget.builder(Text.of("Reconnect"), btn -> {
            ServerInfo info = mc.getCurrentServerEntry();
            if (info != null) {
                ServerAddress address = ServerAddress.parse(info.address);
                if (mc.getNetworkHandler() != null && mc.getNetworkHandler().getConnection() != null) {
                    mc.getNetworkHandler().getConnection().disconnect(Text.translatable("multiplayer.disconnect.quitting"));
                }
                mc.disconnect();
                ConnectScreen.connect(this, mc, address, info, false, null);
            }
        }).width(98).build();

        if (exitButton != null) {
            exitButton.setWidth(98);
            int exitX = exitButton.getX();
            int exitY = exitButton.getY();

            reconnectButton.setX(exitX + 98 + 8);
            reconnectButton.setY(exitY);
            this.addDrawableChild(reconnectButton);
        }
    }

}
