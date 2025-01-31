package cn.xiaozi0721.bpk.mixin.block;

import net.minecraft.block.BlockChest;
import net.minecraft.util.math.AxisAlignedBB;
import org.spongepowered.asm.mixin.*;

@Mixin(BlockChest.class)
public abstract class MixinBlockChest{
    @Shadow @Final @Mutable protected static AxisAlignedBB NORTH_CHEST_AABB = new AxisAlignedBB(0.025D, 0.0D, 0.0D, 0.975D, 0.95D, 0.975D);
    @Shadow @Final @Mutable protected static AxisAlignedBB SOUTH_CHEST_AABB = new AxisAlignedBB(0.025D, 0.0D, 0.025D, 0.975D, 0.95D, 1.0D);
    @Shadow @Final @Mutable protected static AxisAlignedBB WEST_CHEST_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.025D, 0.975D, 0.95D, 0.975D);
    @Shadow @Final @Mutable protected static AxisAlignedBB EAST_CHEST_AABB = new AxisAlignedBB(0.025D, 0.0D, 0.025D, 1.0D, 0.95D, 0.975D);
    @Shadow @Final @Mutable protected static AxisAlignedBB NOT_CONNECTED_AABB = new AxisAlignedBB(0.025D, 0.0D, 0.025D, 0.975D, 0.95D, 0.975D);
}
