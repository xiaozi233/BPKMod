package cn.xiaozi0721.bpk.mixin.block;

import net.minecraft.world.level.block.EnderChestBlock;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.*;

@Mixin(EnderChestBlock.class)
public abstract class MixinEnderChestBlock {
    @Shadow @Final @Mutable
    private static final VoxelShape SHAPE = Shapes.create(0.025D, 0.0D, 0.025D, 0.975D, 0.95D, 0.975D);
}
