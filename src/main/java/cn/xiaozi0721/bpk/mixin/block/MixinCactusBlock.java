package cn.xiaozi0721.bpk.mixin.block;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CactusBlock.class)
public abstract class MixinCactusBlock {
    @Shadow @Final private static VoxelShape SHAPE;

    @ModifyReturnValue(method = "getCollisionShape", at = @At("RETURN"))
    protected VoxelShape getCollisionShape(VoxelShape original) {
        return SHAPE;
    }
}
