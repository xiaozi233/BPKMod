package cn.xiaozi0721.bpk.mixin.minecraft.entity;

import cn.xiaozi0721.bpk.config.ConfigHandler.GeneralConfig;
import cn.xiaozi0721.bpk.interfaces.IPlayerPressingSneak;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.util.math.AxisAlignedBB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class MixinEntity{
    @Shadow public double motionX;
    @Shadow public double motionZ;
    @Shadow public abstract boolean isSneaking();

    @ModifyExpressionValue(
            method = "move",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/AxisAlignedBB;offset(DDD)Lnet/minecraft/util/math/AxisAlignedBB;"),
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/AxisAlignedBB;offset(DDD)Lnet/minecraft/util/math/AxisAlignedBB;", ordinal = 1),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/AxisAlignedBB;offset(DDD)Lnet/minecraft/util/math/AxisAlignedBB;", ordinal = 3)
            )
    )
    private AxisAlignedBB shrinkAABB(AxisAlignedBB aabb){
        return GeneralConfig.beSneak ? aabb.grow(-0.025, 0, -0.025) : aabb;
    }

    @Inject(method = "move", at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z", ordinal = 0, shift = At.Shift.BY, by = 2))
    private void clearMotionX(MoverType type, double x, double y, double z, CallbackInfo ci){
        if(GeneralConfig.beSneak){
            this.motionX = 0;
        }
    }

    @Inject(method = "move", at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z", ordinal = 1, shift = At.Shift.BY, by = 2))
    private void clearMotionZ(MoverType type, double x, double y, double z, CallbackInfo ci){
        if(GeneralConfig.beSneak){
            this.motionZ = 0;
        }
    }

    @Inject(method = "move", at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z", ordinal = 2, shift = At.Shift.BY, by = 2))
    private void clearMotionXZ(MoverType type, double x, double y, double z, CallbackInfo ci){
        if(GeneralConfig.beSneak){
            this.motionX = 0;
            this.motionZ = 0;
        }
    }

    @Redirect(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isSneaking()Z"))
    public boolean isSneaking(Entity entity) {
        return GeneralConfig.beSneak && entity instanceof IPlayerPressingSneak ? ((IPlayerPressingSneak)entity).BPKMod$isSneakPressed() : this.isSneaking();
    }

//    @ModifyVariable(
//            method = "move",
//            at = @At("STORE"),
//            slice = @Slice(
//                    from = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isSneaking()Z", ordinal = 0),
//                    to = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/AxisAlignedBB;expand(DDD)Lnet/minecraft/util/math/AxisAlignedBB;", ordinal = 0)
//            ),
//            argsOnly = true,
//            ordinal = 0
//    )
//    private double clearMotionX(double x){
//        if(GeneralConfig.beSneak){
//            this.motionX = 0;
//            return 0;
//        }
//        return x;
//    }
//
//    @ModifyVariable(
//            method = "move",
//            at = @At(value = "STORE"),
//            slice = @Slice(
//                    from = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isSneaking()Z", ordinal = 0),
//                    to = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/AxisAlignedBB;expand(DDD)Lnet/minecraft/util/math/AxisAlignedBB;", ordinal = 0)
//            ),
//            argsOnly = true,
//            ordinal = 2
//    )
//    private double clearMotionZ(double z){
//        if(GeneralConfig.beSneak){
//            this.motionZ = 0;
//            return 0;
//        }
//        return z;
//    }

}
