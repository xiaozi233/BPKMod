package cn.xiaozi0721.bpk.mixin.entity;

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
public abstract class MixinEntityLivingBase extends Entity{

    public MixinEntityLivingBase(World worldIn) {
        super(worldIn);
    }

    //Change Momentum Threshold
    @ModifyConstant(method = "onLivingUpdate", constant = @Constant(doubleValue = 0.003D))
    private double onLivingUpdate(double value){
        return 1.0E-4;
    }

    //

    @Shadow @Final public static IAttribute SWIM_SPEED;
    @Shadow public abstract IAttributeInstance getEntityAttribute(IAttribute attribute);

    @Inject(method = "moveRelative", at = @At("HEAD"), cancellable = true)
    private void moveRealative(float strafe, float up, float forward, float friction, CallbackInfo ci){
        float f = strafe * strafe + up * up + forward * forward;
        if (f >= 1.0E-4F)
        {
            f = MathHelper.sqrt(f);
            if (f < 1.0F) f = 1.0F;
            else friction *= 0.98F;
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
            this.motionX += (double)(strafe * f2 - forward * f1);
            this.motionY += (double)up;
            this.motionZ += (double)(forward * f2 + strafe * f1);
            ci.cancel();
        }
    }
}
