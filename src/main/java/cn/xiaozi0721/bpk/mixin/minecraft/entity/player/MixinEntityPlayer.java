package cn.xiaozi0721.bpk.mixin.minecraft.entity.player;

import cn.xiaozi0721.bpk.config.ConfigHandler.GeneralConfig;
import cn.xiaozi0721.bpk.interfaces.IEntityPlayer;
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

    @Unique public final double sneakHeight = 1.5F;
    @Unique public boolean BPKMod$underBlock = false;

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

    @Inject(method = "updateSize", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;isSneaking()Z", shift = At.Shift.AFTER))
    private void updateUnderBlock(CallbackInfo ci){
        AxisAlignedBB normalAABB = this.getEntityBoundingBox();
        AxisAlignedBB sneakAABB = new AxisAlignedBB(normalAABB.minX, normalAABB.minY, normalAABB.minZ, normalAABB.minX + 0.6D, normalAABB.minY + this.sneakHeight - 1.0E-7, normalAABB.minZ + 0.6D);
        normalAABB = new AxisAlignedBB(normalAABB.minX, normalAABB.minY, normalAABB.minZ, normalAABB.minX + 0.6D, normalAABB.minY + 1.8D - 1.0E-7, normalAABB.minZ + 0.6D);
        BPKMod$underBlock = this.world.collidesWithAnyBlock(normalAABB) && !this.world.collidesWithAnyBlock(sneakAABB);
    }

//    @Redirect(method = "updateSize", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;isSneaking()Z"))
//    private boolean sneakPressing(EntityPlayer entityPlayer){
//        return entityPlayer.isSneaking() || BPKMod$underBlock;
//    }

    @Override
    public boolean BPKMod$getUnderBlock(){
        return BPKMod$underBlock;
    }
}
