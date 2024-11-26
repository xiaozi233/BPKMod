package cn.xiaozi0721.bpk.mixin.minecraft.entity.player;

import cn.xiaozi0721.bpk.config.ConfigHandler.GeneralConfig;
import cn.xiaozi0721.bpk.interfaces.IEntityPlayer;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer extends EntityLivingBase implements IEntityPlayer {
    @Shadow protected float speedInAir;

    @Unique public boolean BPKMod$underBlock = false;
//    @Unique public boolean BPKMod$isSneakingPose = false;

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

    @ModifyConstant(method = {"updateSize", "getEyeHeight"}, constant = @Constant(floatValue = 1.65F))
    private float setSneakHeight(float sneakHeight){
        return 1.5F;
    }

    @ModifyConstant(method = "getEyeHeight", constant = @Constant(floatValue = 0.08F))
    private float setSneakEyeHeight(float sneakEyeHeight){
        return 0.38F;
    }

    @Inject(method = "updateSize", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;collidesWithAnyBlock(Lnet/minecraft/util/math/AxisAlignedBB;)Z"))
    private void updateUnderBlock(CallbackInfo ci){
        AxisAlignedBB axisalignedbb = this.getEntityBoundingBox();
        axisalignedbb = new AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ, axisalignedbb.minX + 0.6D, axisalignedbb.minY + 1.8D, axisalignedbb.minZ + 0.6D);
        BPKMod$underBlock = this.world.collidesWithAnyBlock(axisalignedbb);
    }

    @Inject(method = "updateSize", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/EntityPlayer;width:F"))
    private void updateUnderBlock(CallbackInfo ci, @Local(ordinal = 0) float width, @Local(ordinal = 1) float height){
        System.out.println("width:" + width + "height: " + height);
    }


    @Redirect(method = "updateSize", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;isSneaking()Z"))
    private boolean sneakPressing(EntityPlayer entityPlayer){
        return entityPlayer.isSneaking();
    }

    @Override
    public boolean BPKMod$getUnderBlock(){
        return BPKMod$underBlock;
    }
//
//    @Override
//    public boolean BPKMod$isSneakingPose(){
//        return BPKMod$isSneakingPose;
//    }
}
