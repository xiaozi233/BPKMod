package cn.xiaozi0721.bpk.mixin.entity;

import cn.xiaozi0721.bpk.config.ConfigHandler;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity {
    @ModifyExpressionValue(method = "aiStep", at = @At(value = "CONSTANT", args = "doubleValue=0.003"))
    private double bpk$inertiaThreshold(double original) {
        return ConfigHandler.generalConfig.oldInertiaThreshold ? ConfigHandler.generalConfig.inertiaThreshold : original;
    }

    // Old (pre-1.14) behaviour: no player special-case, the per-axis threshold applies to players too.
    @ModifyExpressionValue(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;is(Ljava/lang/Object;)Z"))
    private boolean bpk$oldPlayerInertia(boolean original) {
        return original && !ConfigHandler.generalConfig.oldInertiaThreshold;
    }
}
