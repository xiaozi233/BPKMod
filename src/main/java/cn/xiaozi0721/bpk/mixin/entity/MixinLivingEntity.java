package cn.xiaozi0721.bpk.mixin.entity;

import cn.xiaozi0721.bpk.config.ConfigHandler;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends Entity {
    @Shadow
    public abstract double getAttributeValue(Holder<Attribute> attribute);

    @Shadow
    public abstract boolean onClimbable();

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

    // Bedrock clamps ladder/vine descent to 0.2 blocks/tick: its ladder movement function does
    // moveRelative and then vy = max(vy, -0.2) (JE clamps the same spot at -0.15). Scaffolding
    // keeps 0.15 -- that is also Bedrock's scaffolding descend speed (its descend action writes
    // vy = -0.15). The clamp below is the only Math.max in handleOnClimbable; the x/z clamps are
    // Mth.clamp and must not be touched.
    @ModifyArg(method = "handleOnClimbable", at = @At(value = "INVOKE", target = "Ljava/lang/Math;max(DD)D"), index = 1)
    private double bedrockClimbDownSpeed(final double original) {
        return !ConfigHandler.generalConfig.beLadder || this.getInBlockState().is(Blocks.SCAFFOLDING) ? original : -0.2;
    }

    // Bedrock has no horizontal speed limit on ladders/vines -- its ladder movement function only
    // clamps the vertical component (and the in-game check confirms it: walking while standing on
    // a block inside a ladder is limited by JE's clamp, not by anything Bedrock does). Scaffolding
    // keeps the +-0.15 clamp: descending through it is a separate Bedrock system with no evidence
    // either way, so it stays on JE behaviour.
    @WrapOperation(method = "handleOnClimbable", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;clamp(DDD)D"))
    private double bedrockClimbHorizontal(final double value, final double min, final double max, final Operation<Double> original) {
        return !ConfigHandler.generalConfig.beLadder || this.getInBlockState().is(Blocks.SCAFFOLDING) ? original.call(value, min, max) : value;
    }

    // Bedrock writes vy = +0.2 straight into the state vector, so a climb-up is 0.2 blocks/tick
    // (scaffolding: 0.15, matching its -0.15 descend). JE assigns the value in
    // handleRelativeFrictionAndCalculateMovement *before* gravity and the 0.98 vertical drag are
    // applied to it, so the stored value -- and therefore the ascent -- is only
    // (0.2 - 0.08) * 0.98 = 0.1176, which is what the game shows. Invert that transform here.
    @ModifyExpressionValue(method = "handleRelativeFrictionAndCalculateMovement", at = @At(value = "CONSTANT", args = "doubleValue=0.2"))
    private double bedrockClimbUpSpeed(final double original) {
        return !ConfigHandler.generalConfig.beLadder ? original : this.bedrockClimbSpeed() / 0.98 + this.getEffectiveGravity();
    }

    // The climb wins over a jump, as in Bedrock: there the climb handler writes the climb speed into
    // the state vector, so pressing jump inside a ladder/vine/scaffolding only climbs. JE instead
    // runs the jump first (vy = 0.42) and that tick's move uses it. So give the jump the climb speed
    // instead and let the climb branch above keep it there; the sprint-jump kick goes too.
    @WrapMethod(method = "jumpFromGround")
    private void bedrockClimbOverJump(Operation<Void> original) {
        if (!ConfigHandler.generalConfig.beLadder || !this.onClimbable()) {
            original.call();
            return;
        }

        Vec3 movement = this.getDeltaMovement();
        // Mirrors the shape vanilla's jump uses (Math.max), so only the jump's own 0.42 impulse is
        // replaced -- an already larger upward velocity (bounce, levitation) is left alone.
        this.setDeltaMovement(movement.x, Math.max(this.bedrockClimbSpeed(), movement.y), movement.z);
        this.needsSync = true;
    }

    @Unique
    private double bedrockClimbSpeed() {
        return this.getInBlockState().is(Blocks.SCAFFOLDING) ? 0.15 : 0.2;
    }
}
