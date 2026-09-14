package cn.xiaozi0721.bpk.mixin.entity;

import cn.xiaozi0721.bpk.config.ConfigHandler;
import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public abstract class MixinEntity {
    @Shadow
    protected abstract double getEntityBounciness();

    @Shadow
    protected abstract double getEffectiveGravity();

    @Unique
    private static final ImmutableList<Direction.Axis> YXZ_ORDER = ImmutableList.of(Direction.Axis.Y, Direction.Axis.X, Direction.Axis.Z);

    // Bedrock 1.26.45.01 LandingBounceHandler threshold: |vy| >= 0.08
    @Unique
    private static final double BE_BOUNCE_THRESHOLD = 0.08D;

    @WrapOperation(
        method = "collideWithShapes",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/core/Direction;axisStepOrder(Lnet/minecraft/world/phys/Vec3;)Lcom/google/common/collect/ImmutableList;"
        )
    )
    private static ImmutableList<Direction.Axis> fixedAxisOrder(Vec3 movement, Operation<ImmutableList<Direction.Axis>> original) {
        if (ConfigHandler.generalConfig.oldCollisionOrder) {
            return YXZ_ORDER;
        }
        return original.call(movement);
    }

    // Port of the BE slime bounce (LandingBounceHandler_apply + sub_149036740, see
    // mc-slime-analysis/bedrock_bounce_source.md). BE spans a bounce over two ticks: the impact
    // tick only clamps the position and records the distance actually fallen; the reflection plus
    // a gravity compensation happen on the NEXT tick and replace that tick's regular gravity,
    // followed by the flat 0.98 vertical drag. Java applies gravity *after* move() (in
    // travelInAir), so writing restitution*u + |g|*tau here lets the vanilla pipeline complete the
    // bounce step: it subtracts the full gravity and applies the drag, yielding exactly BE's
    // (restitution*u - |g|*(1-tau)) * 0.98 for the next tick's movement.
    @WrapMethod(method = "restituteMovementAfterCollisions")
    private void bedrockSlimeBounce(BlockState effectState, boolean xCollision, boolean zCollision, Vec3 movement, Operation<Void> original) {
        Entity self = (Entity) (Object) this;

        // Everything BE does not cover keeps vanilla semantics: "no bounce" means stopping on the
        // top face, which vanilla already implements. Non-living entities apply gravity *before*
        // move(), so the compensation below would be double-counted there.
        if (!ConfigHandler.generalConfig.beSlimeBounce
            || !(self instanceof LivingEntity)
            || !self.verticalCollision
            || !self.verticalCollisionBelow
            || self.isSuppressingBounce()
            || effectState.is(BlockTags.SUPPRESSES_BOUNCE)) {
            original.call(effectState, xCollision, zCollision, movement);
            return;
        }

        // BE reads the coefficient straight off the block (slime = 1.0); LivingEntity has no 0.8 nerf
        double entityBounciness = this.getEntityBounciness();
        float blockBounciness = effectState.getBlock().getBounceRestitution();
        if (entityBounciness <= 0.0D && blockBounciness <= 0.0F) {
            original.call(effectState, xCollision, zCollision, movement);
            return;
        }

        double restitution = Math.max(entityBounciness, blockBounciness);
        Vec3 currentMovement = self.getDeltaMovement();
        Vec3 movementAfterBounce = currentMovement;
        if (xCollision) {
            movementAfterBounce = movementAfterBounce.with(Direction.Axis.X, -currentMovement.x * entityBounciness);
        }
        if (zCollision) {
            movementAfterBounce = movementAfterBounce.with(Direction.Axis.Z, -currentMovement.z * entityBounciness);
        }

        double fallingSpeed = -currentMovement.y;
        if (fallingSpeed >= BE_BOUNCE_THRESHOLD) {
            // tau: fraction of the tick elapsed at the moment of impact under uniform acceleration,
            // v_end = sqrt(u^2 + 2|g|s) with s the distance fallen this tick (the clipped movement)
            double gravity = this.getEffectiveGravity();
            double tau = 0.0D;
            if (gravity > 1.0E-7D) {
                double fallen = Math.max(0.0D, -movement.y);
                double impactSpeed = Math.sqrt(fallingSpeed * fallingSpeed + 2.0D * gravity * fallen);
                tau = (impactSpeed - fallingSpeed) / gravity;
            }

            movementAfterBounce = movementAfterBounce.with(Direction.Axis.Y, restitution * fallingSpeed + gravity * tau);
            self.gameEvent(GameEvent.BOUNCE);
            self.syncPosition = true;
        } else {
            // Below BE's threshold there is no reflection; vanilla's stop on the top face matches
            // BE's observed standing steady state (vy cycling 0 <-> -0.0784)
            movementAfterBounce = movementAfterBounce.with(Direction.Axis.Y, 0.0D);
        }

        self.setDeltaMovement(movementAfterBounce);
    }
}
