package me.comu.mixin.entity;

import me.comu.Comu;
import me.comu.module.impl.active.Overlay;
import me.comu.overrides.EntityOverride;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class MixinEntity implements EntityOverride {

    @Unique
    private float cameraPitch;

    @Unique
    private float cameraYaw;

    @Inject(method = "changeLookDirection", at = @At("HEAD"), cancellable = true)
    private void onChangeLookDirection(double cursorDeltaX, double cursorDeltaY, CallbackInfo ci) {
        Overlay overlay = Comu.getInstance().getModuleManager().getModule(Overlay.class);
        if (overlay != null && overlay.isFreelookActive() && (Object) this instanceof ClientPlayerEntity) {
            double yawDelta = (cursorDeltaX * 0.15);
            double pitchDelta = (cursorDeltaY * 0.15);

            this.cameraYaw += (float) yawDelta;
            this.cameraPitch = MathHelper.clamp(this.cameraPitch + (float) pitchDelta, -90.0f, 90.0f);
            ci.cancel();
        }
    }

    @Override
    @Unique
    public float comu$getCameraPitch() {
        return this.cameraPitch;
    }

    @Override
    @Unique
    public float comu$getCameraYaw() {
        return this.cameraYaw;
    }

    @Override
    @Unique
    public void comu$setCameraPitch(float pitch) {
        this.cameraPitch = pitch;
    }

    @Override
    @Unique
    public void comu$setCameraYaw(float yaw) {
        this.cameraYaw = yaw;
    }

}
