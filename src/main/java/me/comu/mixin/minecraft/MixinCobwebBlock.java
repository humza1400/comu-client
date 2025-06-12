package me.comu.mixin.minecraft;

import me.comu.Comu;
import me.comu.module.impl.movement.NoSlowdown;
import net.minecraft.block.BlockState;
import net.minecraft.block.CobwebBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CobwebBlock.class)
public abstract class MixinCobwebBlock {

    @Inject(method = "onEntityCollision", at = @At("HEAD"), cancellable = true)
    private void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, EntityCollisionHandler handler, CallbackInfo info) {
        NoSlowdown noSlow = Comu.getInstance().getModuleManager().getModule(NoSlowdown.class);
        if (entity == MinecraftClient.getInstance().player && noSlow.isEnabled() && noSlow.getCobWebs().getValue()) info.cancel();
    }
}