package cn.xiaozi0721.bpk.mixin.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.EnderChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EnderChestBlock.class)
public abstract class MixinEnderChestBlock {
    @Unique
    private static final VoxelShape BPK$ENDER_CHEST_SHAPE = Shapes.create(0.025D, 0.0D, 0.025D, 0.975D, 0.95D, 0.975D);

    @Overwrite
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return BPK$ENDER_CHEST_SHAPE;
    }
}
