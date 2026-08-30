package cn.xiaozi0721.bpk.mixin.entity;

import cn.xiaozi0721.bpk.config.ConfigHandler;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public abstract class MixinEntity {
    @Shadow
    public abstract void setDeltaMovement(Vec3 deltaMovement);

    @Shadow
    public abstract void setDeltaMovement(double xd, double yd, double zd);

    @Shadow
    public abstract Vec3 getDeltaMovement();

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
