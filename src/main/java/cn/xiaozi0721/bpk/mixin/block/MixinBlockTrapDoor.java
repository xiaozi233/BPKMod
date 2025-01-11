package cn.xiaozi0721.bpk.mixin.block;

import net.minecraft.block.BlockTrapDoor;
import net.minecraft.util.math.AxisAlignedBB;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockTrapDoor.class)
public class MixinBlockTrapDoor{
    @Shadow @Final @Mutable protected static AxisAlignedBB EAST_OPEN_AABB;
    @Shadow @Final @Mutable protected static AxisAlignedBB WEST_OPEN_AABB;
    @Shadow @Final @Mutable protected static AxisAlignedBB SOUTH_OPEN_AABB;
    @Shadow @Final @Mutable protected static AxisAlignedBB NORTH_OPEN_AABB;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void setAABB(CallbackInfo ci){
        EAST_OPEN_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.1825D, 1.0D, 1.0D);
        WEST_OPEN_AABB = new AxisAlignedBB(0.8175D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
        SOUTH_OPEN_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.1825D);
        NORTH_OPEN_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.8175D, 1.0D, 1.0D, 1.0D);
    }
}
