package cn.xiaozi0721.bpk.mixin.entity;

import cn.xiaozi0721.bpk.config.ConfigHandler;
import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public abstract class MixinEntity {
    @Shadow
    public abstract void setDeltaMovement(Vec3 deltaMovement);

    @Shadow
    public abstract void setDeltaMovement(double xd, double yd, double zd);

    @Shadow
    public abstract Vec3 getDeltaMovement();

    @Unique
    private static final ImmutableList<Direction.Axis> BPK$YXZ_ORDER = ImmutableList.of(Direction.Axis.Y, Direction.Axis.X, Direction.Axis.Z);

    @WrapOperation(
        method = "collideWithShapes",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/core/Direction;axisStepOrder(Lnet/minecraft/world/phys/Vec3;)Lcom/google/common/collect/ImmutableList;"
        )
    )
    private static ImmutableList<Direction.Axis> bpk$fixedAxisOrder(Vec3 movement, Operation<ImmutableList<Direction.Axis>> original) {
        if (ConfigHandler.generalConfig.oldCollisionOrder) {
            return BPK$YXZ_ORDER;
        }
        return original.call(movement);
    }

    @WrapOperation(
        method = "move",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;maybeBackOffFromEdge(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/entity/MoverType;)Lnet/minecraft/world/phys/Vec3;"
        )
    )
    private Vec3 bpk$clearMotion(Entity instance, Vec3 delta, MoverType moverType, Operation<Vec3> original) {
        Vec3 backedOff = original.call(delta, moverType);
        if (ConfigHandler.generalConfig.isBESneak && !backedOff.equals(delta)) {
            instance.setDeltaMovement(0.0D, instance.getDeltaMovement().y, 0.0D);
        }
        return backedOff;
    }
}
