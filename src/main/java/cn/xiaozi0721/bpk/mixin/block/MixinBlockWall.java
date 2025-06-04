package cn.xiaozi0721.bpk.mixin.block;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockWall;
import net.minecraft.block.state.BlockFaceShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockWall.class)
public abstract class MixinBlockWall{
    @ModifyReturnValue(method = "canConnectTo", at = @At("RETURN"))
    private boolean canConnectToPane(boolean original, @Local BlockFaceShape blockFaceShape){
        return original || blockFaceShape == BlockFaceShape.MIDDLE_POLE_THIN || blockFaceShape == BlockFaceShape.CENTER_SMALL;
    }
}
