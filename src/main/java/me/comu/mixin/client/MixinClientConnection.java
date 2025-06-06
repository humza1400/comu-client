package me.comu.mixin.client;

import me.comu.Comu;
import me.comu.events.PacketEvent;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BundleS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class MixinClientConnection {

    @Shadow
    protected static <T extends PacketListener> void handlePacket(Packet<T> packet, PacketListener listener) {}

    @Inject(method = "send", at = @At("HEAD"), cancellable = true)
    private void onSend(Packet<?> packet, CallbackInfo ci) {
        PacketEvent event = new PacketEvent(packet, PacketEvent.Direction.OUTGOING);
        Comu.getInstance().getEventManager().dispatch(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "handlePacket", at = @At("HEAD"), cancellable = true)
    private static void onReceive(Packet<?> packet, PacketListener listener, CallbackInfo ci) {
        if (packet instanceof BundleS2CPacket bundle) {
            ci.cancel();
            for (Packet<?> subPacket : bundle.getPackets()) {
                try {
                    handlePacket(subPacket, listener);
                } catch (Exception ignored) {

                }
            }




            return;
        }

        PacketEvent event = new PacketEvent(packet, PacketEvent.Direction.INCOMING);
        Comu.getInstance().getEventManager().dispatch(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }
}