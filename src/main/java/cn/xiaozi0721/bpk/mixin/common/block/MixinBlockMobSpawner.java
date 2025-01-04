package cn.xiaozi0721.bpk.mixin.common.block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockMobSpawner;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@SuppressWarnings({"deprecation", "NullableProblems"})
@Mixin(BlockMobSpawner.class)
public abstract class MixinBlockMobSpawner extends BlockContainer {
    @Unique private static final AxisAlignedBB MOB_SPAWNER_AABB = FULL_BLOCK_AABB.shrink(1.0E-4);

    protected MixinBlockMobSpawner(Material materialIn) {
        super(materialIn);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos){
        return MOB_SPAWNER_AABB;
    }
}
