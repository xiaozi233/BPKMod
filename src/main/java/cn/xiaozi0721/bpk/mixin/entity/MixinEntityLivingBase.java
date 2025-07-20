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
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase extends Entity {
    @Shadow protected boolean isJumping;
    @Shadow public abstract boolean isOnLadder();

    @Unique private final float BEST_NEW_TOUCH_YAW = (float)Math.acos(0.98);

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
        if(!GeneralConfig.isNewTouch || !((EntityLivingBase)(Object)this instanceof EntityPlayer)){
            return;
        }

        float strafe = strafeRef.get();
        float forward = forwardRef.get();
        float product = strafe * forward;

        if(MathHelper.abs(product) >= 1.0E-4){
            final float sign = Math.signum(product);
            final float absPitch = MathHelper.abs(this.rotationPitch * 0.017453292F);
            float newTouchYaw = GeneralConfig.byPitch ? absPitch : this.BEST_NEW_TOUCH_YAW;
            float sinRotation = MathHelper.sin(sign * (45 * 0.017453292F - newTouchYaw));
            float cosRotation = MathHelper.cos(sign * (45 * 0.017453292F - newTouchYaw));

            strafeRef.set(strafe * cosRotation - forward * sinRotation);
            if(newTouchYaw < this.BEST_NEW_TOUCH_YAW){
                strafe *= 0.98F;
                forward *= 0.98F;
                sinRotation = MathHelper.sin(sign * 45 * 0.017453292F);
                cosRotation = MathHelper.cos(sign * 45 * 0.017453292F);
            }
            forwardRef.set(forward * cosRotation + strafe * sinRotation);
        }
    }

    @ModifyExpressionValue(method = "travel", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/EntityLivingBase;collidedHorizontally:Z", ordinal = 1))
    private boolean canClimbWhenJumping(boolean original){
        return GeneralConfig.isBELadder ? original || this.isJumping : original;
    }

    @ModifyExpressionValue(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;clamp(DDD)D", ordinal = 0))
    private double removeMotionXLimitOnLadder(double original){
        return GeneralConfig.isBELadder ? this.motionX : original;
    }

    @ModifyExpressionValue(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;clamp(DDD)D", ordinal = 1))
    private double removeMotionZLimitOnLadder(double original){
        return GeneralConfig.isBELadder ? this.motionZ : original;
    }

    @ModifyExpressionValue(method = "travel", at = @At(value = "CONSTANT", args = "doubleValue=-0.15"))
    private double modifyMotionYLimitOnLadder(double original){
        return GeneralConfig.isBELadder ? -0.2D : original;
    }

    @Inject(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/BlockPos$PooledMutableBlockPos;release()V"))
    private void setMotionYWhenClimbing(CallbackInfo ci){
        if (GeneralConfig.isBELadder && (this.collidedHorizontally || this.isJumping) && this.isOnLadder()){
            motionY = 0.2D;
        }
    }
}
