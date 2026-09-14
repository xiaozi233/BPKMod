package cn.xiaozi0721.bpk.mixin.block;

import java.util.Map;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.*;

@Mixin(ChestBlock.class)
public abstract class MixinChestBlock {
    @Shadow @Final @Mutable
    private static final VoxelShape SHAPE = Shapes.create(0.025D, 0.0D, 0.025D, 0.975D, 0.95D, 0.975D);

    @Shadow @Final @Mutable
    private static final Map<Direction, VoxelShape> HALF_SHAPES = Shapes.rotateHorizontal(Shapes.create(0.025D, 0.0D, 0.0D, 0.975D, 0.95D, 0.975D));

}
