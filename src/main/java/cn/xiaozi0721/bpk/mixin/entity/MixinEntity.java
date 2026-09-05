package cn.xiaozi0721.bpk.mixin.entity;

import cn.xiaozi0721.bpk.config.ConfigHandler.GeneralConfig;
import cn.xiaozi0721.bpk.interfaces.IPlayerPressingSneak;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.entity.Entity;
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
        return GeneralConfig.isBESneak ? aabb.grow(-0.025, 0, -0.025) : aabb;
    }

    @Inject(method = "move", at = @At(value = "INVOKE_ASSIGN", target = "Ljava/util/List;isEmpty()Z", ordinal = 0, shift = At.Shift.AFTER))
    private void clearMotionX(CallbackInfo ci){
        if(GeneralConfig.isBESneak){
            this.motionX = 0;
        }
    }

    @Inject(method = "move", at = @At(value = "INVOKE_ASSIGN", target = "Ljava/util/List;isEmpty()Z", ordinal = 1, shift = At.Shift.AFTER))
    private void clearMotionZ(CallbackInfo ci){
        if(GeneralConfig.isBESneak){
            this.motionZ = 0;
        }
    }

    @Inject(method = "move", at = @At(value = "INVOKE_ASSIGN", target = "Ljava/util/List;isEmpty()Z", ordinal = 2, shift = At.Shift.AFTER))
    private void clearMotionXZ(CallbackInfo ci){
        if(GeneralConfig.isBESneak){
            this.motionX = 0;
            this.motionZ = 0;
        }
    }

    @ModifyExpressionValue(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isSneaking()Z"))
    public boolean isPlayerPressingSneak(boolean original) {
        return GeneralConfig.isBESneak && this instanceof IPlayerPressingSneak ? ((IPlayerPressingSneak)this).BPKMod$isSneakPressed() : original;
    }


}
