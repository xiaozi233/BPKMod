package cn.xiaozi0721.bpk.mixin.entity;

import cn.xiaozi0721.bpk.config.ConfigHandler;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Player.class)
public abstract class MixinPlayer {
    @Unique
    private static final double SNEAK_TEST_BOX_MARGIN = 0.025D;

    @Unique
    private static final double BACKOFF_STEP = 0.05D;

    // Port of the BE sneak edge protection in Entity.move: while
    // sneaking on ground, movement is shaved back in 0.05 steps until the
    // hitbox (shrunk by 0.025 on X/Z) would still land on support one step
    // height down, so the player stops 0.025 blocks short of the edge.
    @WrapMethod(method = "maybeBackOffFromEdge")
    private Vec3 bpk$beSneakEdge(Vec3 delta, MoverType moverType, Operation<Vec3> original) {
        if (!ConfigHandler.generalConfig.beSneak) {
            return original.call(delta, moverType);
        }

        Entity self = (Entity) (Object) this;
        if ((moverType != MoverType.SELF && moverType != MoverType.PLAYER)
            || !self.onGround()
            || !self.isShiftKeyDown()) {
            return delta;
        }

        AABB testBox = this.getBpkSneakTestBox(self.getBoundingBox());
        double maxDownStep = -self.maxUpStep() * 1.01D;
        double x = delta.x;
        double z = delta.z;

        while (x != 0.0D && self.level().noCollision(self, testBox.move(x, maxDownStep, 0.0D))) {
            if (x < BACKOFF_STEP && x >= -BACKOFF_STEP) {
                x = 0.0D;
            } else if (x > 0.0D) {
                x -= BACKOFF_STEP;
            } else {
                x += BACKOFF_STEP;
            }
        }

        while (z != 0.0D && self.level().noCollision(self, testBox.move(0.0D, maxDownStep, z))) {
            if (z < BACKOFF_STEP && z >= -BACKOFF_STEP) {
                z = 0.0D;
            } else if (z > 0.0D) {
                z -= BACKOFF_STEP;
            } else {
                z += BACKOFF_STEP;
            }
        }

        while (x != 0.0D && z != 0.0D && self.level().noCollision(self, testBox.move(x, maxDownStep, z))) {
            if (x < BACKOFF_STEP && x >= -BACKOFF_STEP) {
                x = 0.0D;
            } else if (x > 0.0D) {
                x -= BACKOFF_STEP;
            } else {
                x += BACKOFF_STEP;
            }

            if (z < BACKOFF_STEP && z >= -BACKOFF_STEP) {
                z = 0.0D;
            } else if (z > 0.0D) {
                z -= BACKOFF_STEP;
            } else {
                z += BACKOFF_STEP;
            }
        }

        Vec3 deltaMovement = self.getDeltaMovement();
        if (Math.abs(x) <= 1E-7) {
            deltaMovement.with(Direction.Axis.X, 0.0D);
            self.setDeltaMovement(deltaMovement);
        }

        if (Math.abs(z) <= 1E-7) {
            deltaMovement.with(Direction.Axis.Z, 0.0D);
            self.setDeltaMovement(deltaMovement);
        }

        return new Vec3(x, delta.y, z);
    }

    @Unique
    private AABB getBpkSneakTestBox(AABB bb) {
        double minX = bb.minX + SNEAK_TEST_BOX_MARGIN;
        double maxX = bb.maxX - SNEAK_TEST_BOX_MARGIN;
        double minZ = bb.minZ + SNEAK_TEST_BOX_MARGIN;
        double maxZ = bb.maxZ - SNEAK_TEST_BOX_MARGIN;

        if (minX > maxX) {
            minX = maxX = (bb.minX + bb.maxX) * 0.5D;
        }

        if (minZ > maxZ) {
            minZ = maxZ = (bb.minZ + bb.maxZ) * 0.5D;
        }

        return new AABB(minX, bb.minY, minZ, maxX, bb.maxY, maxZ);
    }
}
