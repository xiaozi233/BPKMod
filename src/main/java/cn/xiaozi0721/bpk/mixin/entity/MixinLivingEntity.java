package cn.xiaozi0721.bpk.mixin.entity;

import cn.xiaozi0721.bpk.config.ConfigHandler;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(net.minecraft.world.entity.LivingEntity.class)
public abstract class MixinLivingEntity {

    @ModifyExpressionValue(method = "aiStep", at = @At(value = "CONSTANT", args = "doubleValue=0.003"))
    private double bpk$inertiaThreshold(double original) {
        return ConfigHandler.generalConfig.inertiaThreshold;
    }

    @ModifyExpressionValue(method = "aiStep", at = @At(value = "CONSTANT", args = "doubleValue=9.0E-6"))
    private double bpk$inertiaThresholdSqr(double original) {
        double threshold = ConfigHandler.generalConfig.inertiaThreshold;
        return threshold * threshold;
    }

    @ModifyReturnValue(method = "trapdoorUsableAsLadder", at = @At("RETURN"))
    private boolean bpk$climbableTrapdoor(boolean original, BlockPos pos, BlockState state) {
        if (original || !ConfigHandler.generalConfig.climbableTrapdoor || !state.getValue(TrapDoorBlock.OPEN)) {
            return original;
        }
        return ((Entity) (Object) this).level().getBlockState(pos.below()).is(Blocks.LADDER);
    }
}
