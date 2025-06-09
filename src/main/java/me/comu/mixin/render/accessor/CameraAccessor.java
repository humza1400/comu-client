package me.comu.mixin.render.accessor;

import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Camera.class)
public interface CameraAccessor {
    @Accessor("pitch")
    float getPitch();

    @Accessor("yaw")
    float getYaw();
}
