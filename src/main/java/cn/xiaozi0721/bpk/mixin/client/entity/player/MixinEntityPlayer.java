package cn.xiaozi0721.bpk.mixin.client.entity.player;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static cn.xiaozi0721.bpk.config.GeneralConfig.sprintDelay;

@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer extends EntityLivingBase {

    @Shadow protected float speedInAir;

    @Shadow(remap = false) public float eyeHeight;

    public MixinEntityPlayer(World worldIn) {
        super(worldIn);
    }
    
    @Inject(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;travel(FFF)V", ordinal = 1))
    private void setJumpMovementFactor(float strafe, float vertical, float forward, CallbackInfo ci){
        if (!sprintDelay){
            this.jumpMovementFactor = this.speedInAir;
            if (this.isSprinting())
            {
                this.jumpMovementFactor = (float)((double)this.jumpMovementFactor + (double)this.speedInAir * 0.3D);
            }
        }
    }

    @ModifyConstant(method = {"updateSize", "getEyeHeight"}, constant = @Constant(floatValue = 1.65F))
    private float setSneakHeight(float sneakHeight){
        return 1.5F;
    }

    @ModifyConstant(method = "getEyeHeight", constant = @Constant(floatValue = 0.08F))
    private float setSneakEyeHeight(float sneakEyeHeight){
        return 0.38F;
    }
}
