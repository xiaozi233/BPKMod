package cn.xiaozi0721.bpk.mixin.block;

import java.util.Map;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ChestBlock.class)
public abstract class MixinChestBlock {
    @Unique
    private static final VoxelShape NOT_CONNECTED_SHAPE = Shapes.create(0.025D, 0.0D, 0.025D, 0.975D, 0.95D, 0.975D);

    @Unique
    private static final Map<Direction, VoxelShape> HALF_SHAPES = Shapes.rotateHorizontal(Shapes.create(0.025D, 0.0D, 0.0D, 0.975D, 0.95D, 0.975D));

    @ModifyReturnValue(method = "getShape", at = @At("RETURN"))
    protected VoxelShape getShape(VoxelShape original, final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
        return switch (state.getValue(ChestBlock.TYPE)) {
            case SINGLE -> NOT_CONNECTED_SHAPE;
            case LEFT, RIGHT -> HALF_SHAPES.get(ChestBlock.getConnectedDirection(state));
        };
    }
}
