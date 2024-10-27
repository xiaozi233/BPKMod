package cn.xiaozi0721.bpk.mixin.block;

import net.minecraft.block.BlockChest;
import net.minecraft.util.math.AxisAlignedBB;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockChest.class)
public abstract class MixinBlockChest{
    @Mutable @Shadow @Final protected static AxisAlignedBB NORTH_CHEST_AABB;
    @Mutable @Shadow @Final protected static AxisAlignedBB SOUTH_CHEST_AABB;
    @Mutable @Shadow @Final protected static AxisAlignedBB WEST_CHEST_AABB;
    @Mutable @Shadow @Final protected static AxisAlignedBB EAST_CHEST_AABB;
    @Mutable @Shadow @Final protected static AxisAlignedBB NOT_CONNECTED_AABB;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void setAABB(CallbackInfo ci){
        NORTH_CHEST_AABB = new AxisAlignedBB(0.025D, 0.0D, 0.0D, 0.975D, 0.95D, 0.975D);
        SOUTH_CHEST_AABB = new AxisAlignedBB(0.025D, 0.0D, 0.025D, 0.975D, 0.95D, 1.0D);
        WEST_CHEST_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.025D, 0.975D, 0.95D, 0.975D);
        EAST_CHEST_AABB = new AxisAlignedBB(0.025D, 0.0D, 0.025D, 1.0D, 0.95D, 0.975D);
        NOT_CONNECTED_AABB = new AxisAlignedBB(0.025D, 0.0D, 0.025D, 0.975D, 0.95D, 0.975D);
    }
}
