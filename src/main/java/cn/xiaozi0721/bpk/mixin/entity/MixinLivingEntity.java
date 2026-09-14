package cn.xiaozi0721.bpk.mixin.entity;

import cn.xiaozi0721.bpk.config.ConfigHandler;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends Entity {
    @Shadow
    public abstract double getAttributeValue(Holder<Attribute> attribute);

    protected MixinLivingEntity(final EntityType<?> entityType, final Level level) {
        super(entityType, level);
    }

    @ModifyExpressionValue(method = "aiStep", at = @At(value = "CONSTANT", args = "doubleValue=0.003"))
    private double inertiaThreshold(double original) {
        return ConfigHandler.generalConfig.oldInertiaThreshold ? ConfigHandler.generalConfig.inertiaThreshold : original;
    }

    // Old (pre-1.14) behaviour: no player special-case, the per-axis threshold applies to players too.
    @ModifyExpressionValue(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;is(Ljava/lang/Object;)Z"))
    private boolean oldPlayerInertia(boolean original) {
        return original && !ConfigHandler.generalConfig.oldInertiaThreshold;
    }

    // Bedrock's soul sand slowdown lives in the acceleration, not in the speed: it multiplies the
    // block friction by 1.225, so the ground travel formula (0.546/(friction*0.91))^3 gives
    // (0.6/0.735)^3 = 1/1.225^3 = 0.54399 of the normal acceleration, while the per-tick drag
    // still uses the raw 0.6 (0.546 either way) -- hence the terminal speed is that same 54.4%.
    // JE's separate speedFactor 0.4 (a per-tick velocity multiplier that compounds with the drag)
    // is dropped in MixinBlocks. Soul Speed grants MOVEMENT_EFFICIENCY = 1, Java's counterpart of
    // Bedrock's "has soul speed -> skip the x1.225" gate, so it cancels the penalty completely.
    @ModifyReturnValue(method = "getFrictionInfluencedSpeed", at = @At(value = "RETURN", ordinal = 0))
    private float soulSandAcceleration(float original) {
        if (!this.level().getBlockState(this.getBlockPosBelowThatAffectsMyMovement()).is(Blocks.SOUL_SAND)) {
            return original;
        }

        final float soulSandFriction = 1.225F;
        float soulSpeed = (float) this.getAttributeValue(Attributes.MOVEMENT_EFFICIENCY);
        return original * Mth.lerp(soulSpeed, 1.0F / (soulSandFriction * soulSandFriction * soulSandFriction), 1.0F);
    }
}
