package cn.xiaozi0721.bpk.mixin.minecraft.entity;

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

import static cn.xiaozi0721.bpk.config.ConfigHandler.GeneralConfig.*;


@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase extends Entity{
    @Shadow(remap = false) @Final public static IAttribute SWIM_SPEED;
    @Shadow public abstract IAttributeInstance getEntityAttribute(IAttribute attribute);
    @Shadow public abstract void fall(float distance, float damageMultiplier);

    public MixinEntityLivingBase(World worldIn) {
        super(worldIn);
    }

    //Change Inertia Threshold
    @ModifyConstant(method = "onLivingUpdate", constant = @Constant(doubleValue = 0.003D))
    private double setInertiaThreshold(double value){
        return inertiaThreshold;
    }

    //45 strafe-related method
    @Inject(method = "moveRelative", at = @At("HEAD"), cancellable = true)
    private void moveRelative(float strafe, float up, float forward, float friction, CallbackInfo ci){
        float f = strafe * strafe + up * up + forward * forward;
        if (f >= 1.0E-4F)
        {
            f = MathHelper.sqrt(f);
            if (!isNewTouch){
                if (Math.abs(strafe) <= 1.0E-4F || Math.abs(forward) <= 1.0E-4F)
                    f = 1.0F;
                else {
                    if (isSneaking())
                        friction *= 0.3F;
                    friction *= 0.98F;
                }
            }
            else {
                if (f < 1) f = 1.0F;
            }
            f = friction / f;
            strafe = strafe * f;
            up = up * f;
            forward = forward * f;
            if(this.isInWater() || this.isInLava())
            {
                strafe = strafe * (float)this.getEntityAttribute(SWIM_SPEED).getAttributeValue();
                up = up * (float)this.getEntityAttribute(SWIM_SPEED).getAttributeValue();
                forward = forward * (float)this.getEntityAttribute(SWIM_SPEED).getAttributeValue();
            }
            float f1 = MathHelper.sin(this.rotationYaw * 0.017453292F);
            float f2 = MathHelper.cos(this.rotationYaw * 0.017453292F);
            if(isNewTouch){
                float deltaYaw;
                if(!byPitch){
                    deltaYaw = (float)Math.acos(0.98);
                }else{
                    deltaYaw = MathHelper.abs(this.rotationPitch) * 0.017453292F;
                }
                float f3 = MathHelper.sin(45 * 0.017453292F - deltaYaw);
                float f4 = MathHelper.cos(45 * 0.017453292F - deltaYaw);
                if(byPitch && MathHelper.abs(this.rotationPitch) * 0.017453292F < Math.acos(0.98)){;
                    f3 *= (float) (0.98/MathHelper.cos(MathHelper.abs(this.rotationPitch) * 0.017453292F));
                    f4 *= (float) (0.98/MathHelper.cos(MathHelper.abs(this.rotationPitch) * 0.017453292F));
                }

                float tmp = strafe;
                if (strafe * forward > 1.0E-4){
                    strafe = tmp * f4 - forward * f3;
                    forward =  forward * f4 + tmp * f3;
                }
                else if (strafe * forward < -1.0E-4){
                    strafe = tmp * f4 + forward * f3;
                    forward =  forward * f4 - tmp * f3;
                }
            }
            this.motionX += (double)(strafe * f2 - forward * f1);
            this.motionY += (double)up;
            this.motionZ += (double)(forward * f2 + strafe * f1);
        }
        ci.cancel();
    }
}
