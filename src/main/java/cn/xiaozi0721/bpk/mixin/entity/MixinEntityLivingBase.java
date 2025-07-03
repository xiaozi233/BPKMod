package cn.xiaozi0721.bpk.mixin.entity;

import cn.xiaozi0721.bpk.config.ConfigHandler.GeneralConfig;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase extends Entity {
    @Unique private static final float newTouchYaw = (float)Math.acos(0.98);

    public MixinEntityLivingBase(World worldIn) {
        super(worldIn);
    }

    @ModifyExpressionValue(method = "onLivingUpdate", at = @At(value = "CONSTANT", args = "doubleValue=0.003"))
    private double setInertiaThreshold(double value){
        return GeneralConfig.inertiaThreshold;
    }

    @SuppressWarnings({"ConstantValue"})
    @Inject(method = "moveRelative", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/EntityLivingBase;motionX:D", opcode = Opcodes.GETFIELD))
    private void newTouchMovement(CallbackInfo ci, @Local(argsOnly = true, ordinal = 0) LocalFloatRef strafeRef, @Local(argsOnly = true, ordinal = 2) LocalFloatRef forwardRef){
        if(GeneralConfig.isNewTouch && (EntityLivingBase)(Object)this instanceof EntityPlayer){
            float absPitch = MathHelper.abs(this.rotationPitch);
            float deltaYaw = GeneralConfig.byPitch ? absPitch * 0.017453292F : newTouchYaw;
            float newTouchSinYaw = MathHelper.sin(45 * 0.017453292F - deltaYaw);
            float newTouchCosYaw = MathHelper.cos(45 * 0.017453292F - deltaYaw);

            if(GeneralConfig.byPitch && absPitch * 0.017453292F < newTouchYaw){
                newTouchSinYaw *= (0.98F / MathHelper.cos(absPitch * 0.017453292F));
                newTouchCosYaw *= (0.98F / MathHelper.cos(absPitch * 0.017453292F));
            }

            float strafe = strafeRef.get();
            float forward = forwardRef.get();
            if (strafe * forward >= 1.0E-4F) {
                strafeRef.set(strafe * newTouchCosYaw - forward * newTouchSinYaw);
                forwardRef.set(forward * newTouchCosYaw + strafe * newTouchSinYaw);
            } else if (strafe * forward <= -1.0E-4F){
                strafeRef.set(strafe * newTouchCosYaw + forward * newTouchSinYaw);
                forwardRef.set(forward * newTouchCosYaw - strafe * newTouchSinYaw);
            }
        }
    }

    @ModifyExpressionValue(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;clamp(DDD)D", ordinal = 0))
    private double removeMotionXLimit(double original){
        return GeneralConfig.removeHorizontalSpeedLimitOnLadder ? this.motionX : original;
    }

    @ModifyExpressionValue(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;clamp(DDD)D", ordinal = 1))
    private double removeMotionZLimit(double original){
        return GeneralConfig.removeHorizontalSpeedLimitOnLadder ? this.motionZ : original;
    }
}
