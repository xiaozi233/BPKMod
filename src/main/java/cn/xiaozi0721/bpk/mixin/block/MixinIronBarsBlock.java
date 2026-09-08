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
    // Collision matching the TrueModels pane geometry: 2px-wide arms reaching the center
    // line, combined as a union so the 1px inner-corner notch of adjacent arms stays open.
    // Index bits follow 1.12 BlockPane#getBoundingBoxIndex: north=1, east=2, south=4, west=8.
    @Unique
    private static final VoxelShape[] COLLISION_SHAPES = makeShapes();

    @Unique
    private static VoxelShape[] makeShapes() {
        VoxelShape post = shape(0.4375D, 0.4375D, 0.5625D, 0.5625D);
        VoxelShape north = shape(0.4375D, 0.0D, 0.5625D, 0.5D);
        VoxelShape south = shape(0.4375D, 0.5D, 0.5625D, 1.0D);
        VoxelShape west = shape(0.0D, 0.4375D, 0.5D, 0.5625D);
        VoxelShape east = shape(0.5D, 0.4375D, 1.0D, 0.5625D);
        return new VoxelShape[] {
            post,
            north,
            east,
            Shapes.or(north, east),
            south,
            Shapes.or(north, south),
            Shapes.or(east, south),
            Shapes.or(north, east, south),
            west,
            Shapes.or(north, west),
            Shapes.or(east, west),
            Shapes.or(north, east, west),
            Shapes.or(south, west),
            Shapes.or(north, south, west),
            Shapes.or(east, south, west),
            Shapes.or(north, east, south, west)
        };
    }

    @Unique
    private static VoxelShape shape(double minX, double minZ, double maxX, double maxZ) {
        return Shapes.create(minX, 0.0D, minZ, maxX, 1.0D, maxZ);
    }

    @Unique
    private static int index(BlockState state) {
        int i = 0;
        if (state.getValue(IronBarsBlock.NORTH)) {
            i |= 1;
        }
        if (state.getValue(IronBarsBlock.EAST)) {
            i |= 2;
        }
        if (state.getValue(IronBarsBlock.SOUTH)) {
            i |= 4;
        }
        if (state.getValue(IronBarsBlock.WEST)) {
            i |= 8;
        }
        return i;
    }

    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return COLLISION_SHAPES[index(state)];
    }
}
