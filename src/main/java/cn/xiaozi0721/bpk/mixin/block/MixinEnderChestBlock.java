package cn.xiaozi0721.bpk.mixin.block;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.level.block.EnderChestBlock;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnderChestBlock.class)
public abstract class MixinEnderChestBlock {
    @Unique
    private static final VoxelShape BPK$ENDER_CHEST_SHAPE = Shapes.create(0.025D, 0.0D, 0.025D, 0.975D, 0.95D, 0.975D);

    @ModifyReturnValue(method = "getShape", at = @At("RETURN"))
    protected VoxelShape getShape(VoxelShape original) {
        return BPK$ENDER_CHEST_SHAPE;
    }
}
