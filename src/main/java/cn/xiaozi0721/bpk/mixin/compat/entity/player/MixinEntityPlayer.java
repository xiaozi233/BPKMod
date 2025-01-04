package cn.xiaozi0721.bpk.mixin.compat.entity.player;

import cn.xiaozi0721.bpk.config.ConfigHandler.GeneralConfig;
import cn.xiaozi0721.bpk.interfaces.IPlayerResizable;
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
public abstract class MixinEntityPlayer extends EntityLivingBase implements IPlayerResizable {
    @Shadow protected float speedInAir;

    @Unique public double BPKMod$sneakHeight;
    @Unique public boolean BPKMod$underBlock;
    @Unique public boolean BPKMod$resizingAllowed;

    public MixinEntityPlayer(World worldIn) {
        super(worldIn);
    }
    
    @Inject(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;travel(FFF)V", ordinal = 1))
    private void sprintDelayInAir(float strafe, float vertical, float forward, CallbackInfo ci){
        if (!GeneralConfig.sprintDelayInAir){
            this.jumpMovementFactor = this.speedInAir;
            if (this.isSprinting()){
                this.jumpMovementFactor = (float)((double)this.jumpMovementFactor + (double)this.speedInAir * 0.3D);
            }
        }
    }

    @ModifyConstant(method = "getEyeHeight", constant = @Constant(floatValue = 0.08F))
    private float setSneakEyeHeight(float sneakEyeHeight){
        return GeneralConfig.beSneak ? 0.38F : 0.08F;
    }

    @ModifyConstant(method = {"updateSize", "getEyeHeight"}, constant = @Constant(floatValue = 1.65F))
    private float setSneakHeight(float sneakHeight){
        return (float) BPKMod$sneakHeight;
    }

    @Inject(method = "updateSize", at = @At("HEAD"))
    private void updateUnderBlock(CallbackInfo ci){
        AxisAlignedBB normalAABB = this.getEntityBoundingBox();
        AxisAlignedBB sneakAABB = new AxisAlignedBB(normalAABB.minX, normalAABB.minY, normalAABB.minZ, normalAABB.minX + 0.6D, normalAABB.minY + BPKMod$getSneakHeight() - 1.0E-7, normalAABB.minZ + 0.6D);
        normalAABB = new AxisAlignedBB(normalAABB.minX, normalAABB.minY, normalAABB.minZ, normalAABB.minX + 0.6D, normalAABB.minY + 1.8D - 1.0E-7, normalAABB.minZ + 0.6D);
        BPKMod$resizingAllowed = !this.world.collidesWithAnyBlock(normalAABB);
        BPKMod$underBlock = !BPKMod$resizingAllowed && !this.world.collidesWithAnyBlock(sneakAABB);
        BPKMod$sneakHeight = GeneralConfig.beSneak ? 1.5D : 1.65D;
    }

    @Override
    public double BPKMod$getSneakHeight(){
        return BPKMod$sneakHeight;
    }

    @Override
    public boolean BPKMod$getUnderBlock(){
        return BPKMod$underBlock;
    }

    @Override
    public boolean BPKMod$getResizingAllowed(){
        return BPKMod$resizingAllowed;
    }
}
