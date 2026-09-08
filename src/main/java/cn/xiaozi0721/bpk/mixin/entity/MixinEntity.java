package cn.xiaozi0721.bpk.mixin.entity;

import cn.xiaozi0721.bpk.config.ConfigHandler;
import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public abstract class MixinEntity {
    @Unique
    private static final ImmutableList<Direction.Axis> YXZ_ORDER = ImmutableList.of(Direction.Axis.Y, Direction.Axis.X, Direction.Axis.Z);

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
}
