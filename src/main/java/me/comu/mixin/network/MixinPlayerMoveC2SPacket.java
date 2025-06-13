package me.comu.mixin.network;

import me.comu.overrides.ducks.PlayerMovePacketDuck;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Final;

@Mixin(PlayerMoveC2SPacket.class)
public class MixinPlayerMoveC2SPacket implements PlayerMovePacketDuck {

    @Shadow @Final @Mutable protected double x;
    @Shadow @Final @Mutable protected double y;
    @Shadow @Final @Mutable protected double z;
    @Shadow @Final @Mutable protected float yaw;
    @Shadow @Final @Mutable protected float pitch;

    @Override public void setX(double x) { this.x = x; }
    @Override public void setY(double y) { this.y = y; }
    @Override public void setZ(double z) { this.z = z; }
    @Override public void setYaw(float yaw) { this.yaw = yaw; }
    @Override public void setPitch(float pitch) { this.pitch = pitch; }
}
