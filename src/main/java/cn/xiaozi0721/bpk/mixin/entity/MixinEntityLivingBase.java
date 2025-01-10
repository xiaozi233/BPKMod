package cn.xiaozi0721.bpk.mixin.entity;

import cn.xiaozi0721.bpk.config.ConfigHandler.GeneralConfig;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase extends Entity {

    public MixinEntityLivingBase(World worldIn) {
        super(worldIn);
    }

    @ModifyConstant(method = "onLivingUpdate", constant = @Constant(doubleValue = 0.003D))
    private double setInertiaThreshold(double value){
        return GeneralConfig.inertiaThreshold;
    }

    @SuppressWarnings({"ConstantValue"})
    @Inject(method = "moveRelative", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/EntityLivingBase;motionX:D", ordinal = 0))
    private void newTouchMovement(CallbackInfo ci, @Local(argsOnly = true, ordinal = 0) LocalFloatRef strafe, @Local(argsOnly = true, ordinal = 2) LocalFloatRef forward){
        if(GeneralConfig.isNewTouch && ((EntityLivingBase)(Object)this) instanceof EntityPlayer){
                float deltaYaw;
                if(GeneralConfig.byPitch){
                    deltaYaw = MathHelper.abs(this.rotationPitch) * 0.017453292F;
                } else {
                    deltaYaw = (float)Math.acos(0.98);
                }

                float newTouchSinYaw = MathHelper.sin(45 * 0.017453292F - deltaYaw);
                float newTouchCosYaw = MathHelper.cos(45 * 0.017453292F - deltaYaw);
                if(GeneralConfig.byPitch && MathHelper.abs(this.rotationPitch) * 0.017453292F < Math.acos(0.98)){
                    newTouchSinYaw *= (0.98F / MathHelper.cos(MathHelper.abs(this.rotationPitch) * 0.017453292F));
                    newTouchCosYaw *= (0.98F / MathHelper.cos(MathHelper.abs(this.rotationPitch) * 0.017453292F));
                }

                float strafeCopy = strafe.get();
                float forwardCopy = forward.get();
                if (strafeCopy * forwardCopy >= 1.0E-4F) {
                    strafe.set(strafeCopy * newTouchCosYaw - forwardCopy * newTouchSinYaw);
                    forward.set(forwardCopy * newTouchCosYaw + strafeCopy * newTouchSinYaw);
                } else if (strafeCopy * forwardCopy <= -1.0E-4F){
                    strafe.set(strafeCopy * newTouchCosYaw + forwardCopy * newTouchSinYaw);
                    forward.set(forwardCopy * newTouchCosYaw - strafeCopy * newTouchSinYaw);
                }
            }
    }
}
