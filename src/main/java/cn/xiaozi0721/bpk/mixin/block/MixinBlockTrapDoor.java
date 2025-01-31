package cn.xiaozi0721.bpk.mixin.block;

import net.minecraft.block.BlockTrapDoor;
import net.minecraft.util.math.AxisAlignedBB;
import org.spongepowered.asm.mixin.*;

@Mixin(BlockTrapDoor.class)
public abstract class MixinBlockTrapDoor{
    @Shadow @Final @Mutable protected static AxisAlignedBB EAST_OPEN_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.1825D, 1.0D, 1.0D);
    @Shadow @Final @Mutable protected static AxisAlignedBB WEST_OPEN_AABB = new AxisAlignedBB(0.8175D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
    @Shadow @Final @Mutable protected static AxisAlignedBB SOUTH_OPEN_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.1825D);
    @Shadow @Final @Mutable protected static AxisAlignedBB NORTH_OPEN_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.8175D, 1.0D, 1.0D, 1.0D);
}
