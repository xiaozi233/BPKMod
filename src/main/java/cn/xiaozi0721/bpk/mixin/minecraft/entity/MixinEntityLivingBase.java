package cn.xiaozi0721.bpk.mixin.minecraft.entity;

import cn.xiaozi0721.bpk.config.ConfigHandler.GeneralConfig;
import cn.xiaozi0721.bpk.interfaces.IEntityPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase extends Entity implements IEntityPlayer {
    @Shadow(remap = false) @Final public static IAttribute SWIM_SPEED;
    @Shadow public abstract IAttributeInstance getEntityAttribute(IAttribute attribute);
    @Shadow public abstract void fall(float distance, float damageMultiplier);

    public MixinEntityLivingBase(World worldIn) {
        super(worldIn);
    }

    @ModifyConstant(method = "onLivingUpdate", constant = @Constant(doubleValue = 0.003D))
    private double setInertiaThreshold(double value){
        return GeneralConfig.inertiaThreshold;
    }

    @Inject(method = "moveRelative", at = @At("HEAD"), cancellable = true)
    private void moveRelative(float strafe, float up, float forward, float friction, CallbackInfo ci){
        float distance = strafe * strafe + up * up + forward * forward;
        if (distance >= 1.0E-4F) {
            distance = MathHelper.sqrt(distance);
            if (GeneralConfig.isNewTouch){
                if (distance < 1) distance = 1.0F;
            }
            else {
                if (Math.abs(strafe) <= 1.0E-4F || Math.abs(forward) <= 1.0E-4F) distance = 1.0F;
                else {
                    friction *= 0.98F;
                    if (isSneaking()) friction *= 0.3F;
                }
            }
            distance = friction / distance;
            strafe = strafe * distance;
            up = up * distance;
            forward = forward * distance;
            if(this.isInWater() || this.isInLava()) {
                strafe = strafe * (float)this.getEntityAttribute(SWIM_SPEED).getAttributeValue();
                up = up * (float)this.getEntityAttribute(SWIM_SPEED).getAttributeValue();
                forward = forward * (float)this.getEntityAttribute(SWIM_SPEED).getAttributeValue();
            }
            float sinYaw = MathHelper.sin(this.rotationYaw * 0.017453292F);
            float cosYaw = MathHelper.cos(this.rotationYaw * 0.017453292F);
            if(GeneralConfig.isNewTouch){
                float deltaYaw;
                if(GeneralConfig.byPitch){
                    deltaYaw = MathHelper.abs(this.rotationPitch) * 0.017453292F;
                }
                else{
                    deltaYaw = (float)Math.acos(0.98);
                }
                float newTouchSinYaw = MathHelper.sin(45 * 0.017453292F - deltaYaw);
                float newTouchCosYaw = MathHelper.cos(45 * 0.017453292F - deltaYaw);
                if(GeneralConfig.byPitch && MathHelper.abs(this.rotationPitch) * 0.017453292F < Math.acos(0.98)){;
                    newTouchSinYaw *= (float) (0.98/MathHelper.cos(MathHelper.abs(this.rotationPitch) * 0.017453292F));
                    newTouchCosYaw *= (float) (0.98/MathHelper.cos(MathHelper.abs(this.rotationPitch) * 0.017453292F));
                }

                float tmp = strafe;
                if (strafe * forward > 1.0E-4){
                    strafe = tmp * newTouchCosYaw - forward * newTouchSinYaw;
                    forward =  forward * newTouchCosYaw + tmp * newTouchSinYaw;
                }
                else if (strafe * forward < -1.0E-4){
                    strafe = tmp * newTouchCosYaw + forward * newTouchSinYaw;
                    forward =  forward * newTouchCosYaw - tmp * newTouchSinYaw;
                }
            }
            this.motionX += (double)(strafe * cosYaw - forward * sinYaw);
            this.motionY += (double)up;
            this.motionZ += (double)(forward * cosYaw + strafe * sinYaw);
        }
        ci.cancel();
    }
}
