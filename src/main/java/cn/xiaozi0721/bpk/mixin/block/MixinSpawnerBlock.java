package cn.xiaozi0721.bpk.mixin.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SpawnerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(SpawnerBlock.class)
public abstract class MixinSpawnerBlock {
    @Unique
    private static final VoxelShape BPK$MOB_SPAWNER_SHAPE = Shapes.create(1.0E-4D, 0.0D, 1.0E-4D, 1.0D - 1.0E-4D, 1.0D, 1.0D - 1.0E-4D);

    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return BPK$MOB_SPAWNER_SHAPE;
    }
}
