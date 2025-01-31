package cn.xiaozi0721.bpk.mixin.block;

import net.minecraft.block.BlockEnderChest;
import net.minecraft.util.math.AxisAlignedBB;
import org.spongepowered.asm.mixin.*;

@Mixin(BlockEnderChest.class)
public abstract class MixinBlockEnderChest{
    @Shadow @Final @Mutable protected static AxisAlignedBB ENDER_CHEST_AABB = new AxisAlignedBB(0.025D, 0.0D, 0.025D, 0.975D, 0.95D, 0.975D);
}
