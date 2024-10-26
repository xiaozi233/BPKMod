package cn.xiaozi0721.bpk.mixin.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCactus;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.AxisAlignedBB;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockCactus.class)
public abstract class MixinBlockCactus{
    @Mutable @Shadow @Final protected static AxisAlignedBB CACTUS_COLLISION_AABB;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void setAABB(CallbackInfo ci){
        CACTUS_COLLISION_AABB = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 1D, 0.9375D);
    }
}
