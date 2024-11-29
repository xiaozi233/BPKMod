package cn.xiaozi0721.bpk.mixin.minecraft.entity.player;

import cn.xiaozi0721.bpk.config.ConfigHandler.GeneralConfig;
import cn.xiaozi0721.bpk.interfaces.IEntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer extends EntityLivingBase implements IEntityPlayer {
    @Shadow protected float speedInAir;

    @Unique public double sneakHeight = 1.5D;

    public MixinEntityPlayer(World worldIn) {
        super(worldIn);
    }
    
    @Inject(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;travel(FFF)V", ordinal = 1))
    private void sprintDelay(float strafe, float vertical, float forward, CallbackInfo ci){
        if (!GeneralConfig.sprintDelay){
            this.jumpMovementFactor = this.speedInAir;
            if (this.isSprinting()){
                this.jumpMovementFactor = (float)((double)this.jumpMovementFactor + (double)this.speedInAir * 0.3D);
            }
        }
    }

    @ModifyConstant(method = "getEyeHeight", constant = @Constant(floatValue = 0.08F))
    private float setSneakEyeHeight(float sneakEyeHeight){
        return 0.38F;
    }

    @ModifyConstant(method = {"updateSize", "getEyeHeight"}, constant = @Constant(floatValue = 1.65F))
    private float setSneakHeight(float sneakHeight){
        return (float) this.sneakHeight;
    }

    @Override
    public double BPKMod$getSneakHeight(){
        return sneakHeight;
    }
}
