package cn.xiaozi0721.bpk.mixin.aquaacrobatics.client.entity;

import cn.xiaozi0721.bpk.config.ConfigHandler.GeneralConfig;
import cn.xiaozi0721.bpk.mixin.accessor.EntityLivingBaseAccessor;
import com.fuzs.aquaacrobatics.core.mixin.client.EntityPlayerSPMixin;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityPlayerSP.class, priority = 2000)
public abstract class MixinEntityPlayerSP extends AbstractClientPlayer {
    @Shadow public abstract boolean isSneaking();

    @Unique private boolean BPKMod$prevSprinting;

    public MixinEntityPlayerSP(World worldIn, GameProfile playerProfile) {
        super(worldIn, playerProfile);
    }

    @ModifyExpressionValue(method = "stopSprinting", at = @At(value = "FIELD", target = "Lnet/minecraft/util/MovementInput;moveForward:F"))
    @Dynamic(value = "For aquaacrobatics", mixin = EntityPlayerSPMixin.class)
    private float sprintBackward(float moveForward){
        return GeneralConfig.sprintBackward && !isSneaking() && moveForward != 0 ? 1 : moveForward;
    }

    @ModifyExpressionValue(method = "stopSprinting", at = @At(value = "FIELD", target = "Lnet/minecraft/client/entity/EntityPlayerSP;collidedHorizontally:Z"))
    @Dynamic(value = "For aquaacrobatics", mixin = EntityPlayerSPMixin.class)
    private boolean ignoreCollidedHorizontally(boolean collidedHorizontally){
        return !GeneralConfig.ignoreCollidedHorizontally && collidedHorizontally;
    }

    @Inject(method = "onLivingUpdate", at = @At(value = "FIELD", target = "Lnet/minecraft/client/entity/EntityPlayerSP;onGround:Z", ordinal = 0))
    private void updatePrevSprint(CallbackInfo ci){
        BPKMod$prevSprinting = isSprinting();
    }

    @Inject(method = "onLivingUpdate", at = @At(value = "FIELD", target = "Lnet/minecraft/client/entity/EntityPlayerSP;wasFallFlying:Z"))
    private void sprintDelayOnGround(CallbackInfo ci, @Local(ordinal = 2) boolean prevMovedForward){
        if (GeneralConfig.sprintDelayOnGround && !BPKMod$prevSprinting && isSprinting() && EntityLivingBaseAccessor.getSpringSpeedBoostID() != null){
            this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(EntityLivingBaseAccessor.getSpringSpeedBoost());
        }
    }
}
