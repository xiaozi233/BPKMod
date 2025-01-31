package cn.xiaozi0721.bpk.mixin.block;

import net.minecraft.block.BlockCactus;
import net.minecraft.util.math.AxisAlignedBB;
import org.spongepowered.asm.mixin.*;

@Mixin(BlockCactus.class)
public abstract class MixinBlockCactus{
    @Shadow @Final protected static AxisAlignedBB CACTUS_AABB;
    @Shadow @Final @Mutable protected static AxisAlignedBB CACTUS_COLLISION_AABB = CACTUS_AABB;
}
