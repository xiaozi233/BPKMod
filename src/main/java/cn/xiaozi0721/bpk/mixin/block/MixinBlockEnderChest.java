package cn.xiaozi0721.bpk.mixin.block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.AxisAlignedBB;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEnderChest.class)
public abstract class MixinBlockEnderChest{

    @Mutable @Shadow @Final protected static AxisAlignedBB ENDER_CHEST_AABB;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void setAABB(CallbackInfo ci){
        ENDER_CHEST_AABB = new AxisAlignedBB(0.025D, 0.0D, 0.025D, 0.975D, 0.95D, 0.975D);
    }
}
