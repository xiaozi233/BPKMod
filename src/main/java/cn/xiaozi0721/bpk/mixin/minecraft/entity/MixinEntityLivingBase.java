package cn.xiaozi0721.bpk.mixin.minecraft.entity;

import cn.xiaozi0721.bpk.config.ConfigHandler.GeneralConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase extends Entity {
    @Shadow(remap = false) @Final public static IAttribute SWIM_SPEED;
    @Shadow public abstract IAttributeInstance getEntityAttribute(IAttribute attribute);

    public MixinEntityLivingBase(World worldIn) {
        super(worldIn);
    }

    @ModifyConstant(method = "onLivingUpdate", constant = @Constant(doubleValue = 0.003D))
    private double setInertiaThreshold(double value){
        return GeneralConfig.inertiaThreshold;
    }

    /*
     *  This function is responsible for the existence of 45° strafe.
     *  Note that:
     *      - Sprint multiplier is contained within "friction"
     *      - Sneak multiplier is contained within "strafe" and "forward"
     *      - Only the parrot used "up". Kind useless.
     *      - Horse's strafe and forward are unequaled.
     */
    @Inject(method = "moveRelative", at = @At("HEAD"), cancellable = true)
    private void moveRelative(float strafe, float up, float forward, float friction, CallbackInfo ci){
        float distance = strafe * strafe + up * up + forward * forward;
        if (distance >= 1.0E-4F) {
            boolean isPlayer = ((EntityLivingBase)(Object)this) instanceof EntityPlayer;
            distance = MathHelper.sqrt(distance);
            if (!isPlayer || isPlayer && (GeneralConfig.isNewTouch || GeneralConfig.strafeAccelerateAllowed)){
                if (distance < 1) {
                    distance = 1.0F;
                }
            } else if (MathHelper.abs(strafe) < 1.0E-4F || MathHelper.abs(forward) < 1.0E-4F) {
                distance = 1.0F;
            } else {
                distance /= MathHelper.abs(forward);
            }
            friction /= distance;
            strafe = strafe * friction;
            up = up * friction;
            forward = forward * friction;
            if(this.isInWater() || this.isInLava()) {
                strafe = strafe * (float)this.getEntityAttribute(SWIM_SPEED).getAttributeValue();
                up = up * (float)this.getEntityAttribute(SWIM_SPEED).getAttributeValue();
                forward = forward * (float)this.getEntityAttribute(SWIM_SPEED).getAttributeValue();
            }
            float sinYaw = MathHelper.sin(this.rotationYaw * 0.017453292F);
            float cosYaw = MathHelper.cos(this.rotationYaw * 0.017453292F);
            if(GeneralConfig.isNewTouch && isPlayer){
                float deltaYaw;
                if(GeneralConfig.byPitch){
                    deltaYaw = MathHelper.abs(this.rotationPitch) * 0.017453292F;
                } else{
                    deltaYaw = (float)Math.acos(0.98);
                }
                float newTouchSinYaw = MathHelper.sin(45 * 0.017453292F - deltaYaw);
                float newTouchCosYaw = MathHelper.cos(45 * 0.017453292F - deltaYaw);
                if(GeneralConfig.byPitch && MathHelper.abs(this.rotationPitch) * 0.017453292F < Math.acos(0.98)){
                    newTouchSinYaw *= (float) (0.98 / MathHelper.cos(MathHelper.abs(this.rotationPitch) * 0.017453292F));
                    newTouchCosYaw *= (float) (0.98 / MathHelper.cos(MathHelper.abs(this.rotationPitch) * 0.017453292F));
                }

                float tmp = strafe;
                if (strafe * forward > 1.0E-4){
                    strafe = tmp * newTouchCosYaw - forward * newTouchSinYaw;
                    forward =  forward * newTouchCosYaw + tmp * newTouchSinYaw;
                } else if (strafe * forward < -1.0E-4){
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
