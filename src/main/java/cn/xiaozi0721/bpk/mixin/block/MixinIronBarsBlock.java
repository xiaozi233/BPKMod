package cn.xiaozi0721.bpk.mixin.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(IronBarsBlock.class)
public abstract class MixinIronBarsBlock {
    // Faithful port of the 1.12 AABB_BY_INDEX collision table (arms 2px wide, reaching the center line).
    // Index bits follow the 1.12 BlockPane order: south=1, west=2, north=4, east=8.
    @Unique
    private static final VoxelShape[] BPK$COLLISION_SHAPES = {
        bpk$shape(0.4375D, 0.4375D, 0.5625D, 0.5625D), // none: post
        bpk$shape(0.4375D, 0.5D, 0.5625D, 1.0D),       // south
        bpk$shape(0.0D, 0.4375D, 0.5D, 0.5625D),       // west
        bpk$shape(0.0D, 0.4375D, 0.5D, 1.0D),          // south + west
        bpk$shape(0.4375D, 0.0D, 0.5625D, 0.5D),       // north
        bpk$shape(0.4375D, 0.0D, 0.5625D, 1.0D),       // north + south
        bpk$shape(0.0D, 0.0D, 0.5625D, 0.5625D),       // north + west
        bpk$shape(0.0D, 0.0D, 0.5625D, 1.0D),          // north + south + west
        bpk$shape(0.5D, 0.4375D, 1.0D, 0.5625D),       // east
        bpk$shape(0.4375D, 0.4375D, 1.0D, 1.0D),       // east + south
        bpk$shape(0.0D, 0.4375D, 1.0D, 0.5625D),       // east + west
        bpk$shape(0.0D, 0.4375D, 1.0D, 1.0D),          // east + south + west
        bpk$shape(0.4375D, 0.0D, 1.0D, 0.5D),          // east + north
        bpk$shape(0.4375D, 0.0D, 1.0D, 1.0D),          // east + north + south
        bpk$shape(0.0D, 0.0D, 1.0D, 0.5D),             // east + north + west
        bpk$shape(0.0D, 0.0D, 1.0D, 1.0D)              // all
    };

    @Unique
    private static VoxelShape bpk$shape(double minX, double minZ, double maxX, double maxZ) {
        return Shapes.create(minX, 0.0D, minZ, maxX, 1.0D, maxZ);
    }

    @Unique
    private static int bpk$index(BlockState state) {
        int i = 0;
        if (state.getValue(IronBarsBlock.SOUTH)) {
            i |= 1;
        }
        if (state.getValue(IronBarsBlock.WEST)) {
            i |= 2;
        }
        if (state.getValue(IronBarsBlock.NORTH)) {
            i |= 4;
        }
        if (state.getValue(IronBarsBlock.EAST)) {
            i |= 8;
        }
        return i;
    }

    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return BPK$COLLISION_SHAPES[bpk$index(state)];
    }
}
