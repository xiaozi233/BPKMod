package cn.xiaozi0721.bpk.mixin.entity.player;

import cn.xiaozi0721.bpk.config.ConfigHandler.GeneralConfig;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer extends EntityLivingBase{
    @Shadow protected float speedInAir;
    @Shadow protected boolean sleeping;
    @Shadow public PlayerCapabilities capabilities;

    @Unique protected boolean BPKMod$underBlock;
    @Unique protected boolean BPKMod$resizingAllowed;

    public MixinEntityPlayer(World worldIn) {
        super(worldIn);
    }
    
    @Inject(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;travel(FFF)V", ordinal = 1))
    private void sprintDelayInAir(CallbackInfo ci){
        if (!GeneralConfig.sprintDelayInAir){
            this.jumpMovementFactor = this.speedInAir;
            if (this.isSprinting()){
                this.jumpMovementFactor = (float)(this.jumpMovementFactor + this.speedInAir * 0.3D);
            }
        }
    }

    @ModifyExpressionValue(method = "getEyeHeight", at = @At(value = "CONSTANT", args = "floatValue=0.08"))
    private float setSneakEyeHeight(float sneakEyeHeight){
        return GeneralConfig.isBESneak ? 0.38F : 0.08F;
    }

    @ModifyExpressionValue(method = {"updateSize", "getEyeHeight"}, at = @At(value = "CONSTANT", args = "floatValue=1.65"))
    private float setSneakHeight(float sneakHeight){
        return GeneralConfig.sneakHeight;
    }

    @Inject(method = "updateSize", at = @At("HEAD"))
    private void updateUnderBlock(CallbackInfo ci){
        AxisAlignedBB normalAABB = this.getEntityBoundingBox();
        AxisAlignedBB sneakAABB = new AxisAlignedBB(normalAABB.minX, normalAABB.minY, normalAABB.minZ, normalAABB.minX + 0.6D, normalAABB.minY + GeneralConfig.sneakHeight - 1.0E-7D, normalAABB.minZ + 0.6D);
        normalAABB = new AxisAlignedBB(normalAABB.minX, normalAABB.minY, normalAABB.minZ, normalAABB.minX + 0.6D, normalAABB.minY + 1.8F - 1.0E-7D, normalAABB.minZ + 0.6D);
        this.BPKMod$resizingAllowed = !this.world.collidesWithAnyBlock(normalAABB);
        this.BPKMod$underBlock = !this.BPKMod$resizingAllowed && !this.world.collidesWithAnyBlock(sneakAABB);
    }
}
