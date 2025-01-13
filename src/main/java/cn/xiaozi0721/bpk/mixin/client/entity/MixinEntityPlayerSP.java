package cn.xiaozi0721.bpk.mixin.client.entity;

import cn.xiaozi0721.bpk.config.ConfigHandler.GeneralConfig;
import cn.xiaozi0721.bpk.interfaces.IPlayerPressingSneak;
import cn.xiaozi0721.bpk.interfaces.IPlayerResizable;
import cn.xiaozi0721.bpk.interfaces.ILerpSneakCameraEntity;
import cn.xiaozi0721.bpk.mixin.accessor.EntityLivingBaseAccessor;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP extends AbstractClientPlayer implements ILerpSneakCameraEntity, IPlayerPressingSneak {
    @Shadow public MovementInput movementInput;

    @Unique private float BPKMod$lastCameraY;
    @Unique private float BPKMod$cameraY;

    public MixinEntityPlayerSP(World worldIn, GameProfile playerProfile) {
        super(worldIn, playerProfile);
    }

    @ModifyExpressionValue(method = "onLivingUpdate", at = @At(value = "FIELD", target = "Lnet/minecraft/util/MovementInput;moveForward:F", ordinal = 5))
    private float sprintBackward(float moveForward){
        return GeneralConfig.sprintBackward && !isSneaking() && moveForward != 0 ? 1 : moveForward;
    }

    @ModifyConstant(
            method = "onLivingUpdate",
            constant = @Constant(floatValue = 0.8F),
            slice = @Slice(
                    from = @At(value = "FIELD", target = "Lnet/minecraft/util/MovementInput;moveForward:F", ordinal = 0),
                    to = @At(value = "FIELD", target = "Lnet/minecraft/client/entity/EntityPlayerSP;collidedHorizontally:Z")
            )
    )
    private float hasForwardImpulse(float origin){
        return 1.0E-5F;
    }

    @ModifyExpressionValue(method = "onLivingUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/settings/KeyBinding;isKeyDown()Z", ordinal = 1))
    private boolean setSprintTrueConsiderSneak(boolean origin){
        return origin && !this.movementInput.sneak;
    }

    @ModifyExpressionValue(method = "onLivingUpdate", at = @At(value = "FIELD", target = "Lnet/minecraft/client/entity/EntityPlayerSP;collidedHorizontally:Z"))
    private boolean setSprintFalseConsiderSneak(boolean origin){
        return origin || this.movementInput.sneak;
    }

    @Inject(method = "onLivingUpdate", at = @At(value = "FIELD", target = "Lnet/minecraft/client/entity/EntityPlayerSP;onGround:Z", ordinal = 0))
    private void updatePrevSprinting(CallbackInfo ci, @Share("prevSprinting") LocalBooleanRef prevSprintingRef){
        prevSprintingRef.set(this.isSprinting());
    }

    @SuppressWarnings("ConstantValue")
    @Inject(method = "onLivingUpdate", at = @At(value = "FIELD", target = "Lnet/minecraft/client/entity/EntityPlayerSP;capabilities:Lnet/minecraft/entity/player/PlayerCapabilities;", ordinal = 1))
    private void sprintDelayOnGround(CallbackInfo ci, @Local(ordinal = 2) boolean prevMovedForward, @Share("prevSprinting") LocalBooleanRef prevSprintingRef){
        if (GeneralConfig.sprintDelayOnGround && !prevSprintingRef.get() && this.isSprinting() && EntityLivingBaseAccessor.getSpringSpeedBoostID() != null){
            this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(EntityLivingBaseAccessor.getSpringSpeedBoost());
        }
    }

    @ModifyExpressionValue(method = "onLivingUpdate", at = @At(value = "FIELD", target = "Lnet/minecraft/client/entity/EntityPlayerSP;collidedHorizontally:Z"))
    private boolean ignoreCollidedHorizontally(boolean collidedHorizontally){
        return !GeneralConfig.ignoreCollidedHorizontally && collidedHorizontally;
    }

    @Inject(method = "onLivingUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/MovementInput;updatePlayerMoveState()V", shift = At.Shift.AFTER))
    private void updateSneakInput(CallbackInfo ci) {
        if (GeneralConfig.beSneak && !this.movementInput.sneak && (((IPlayerResizable)this).BPKMod$getUnderBlock() || BPKMod$isSneakingPose()) && !this.capabilities.isFlying) {
            this.movementInput.moveStrafe *= 0.3F;
            this.movementInput.moveForward *= 0.3F;
        }
    }

    @ModifyVariable(method = "isSneaking", at = @At("STORE"))
    private boolean isSneaking(boolean isSneaking){
        return isSneaking || (GeneralConfig.beSneak && ((IPlayerResizable)this).BPKMod$getUnderBlock() || BPKMod$isSneakingPose() && !((IPlayerResizable)this).BPKMod$getResizingAllowed());
    }

//    @Redirect(method = "onUpdateWalkingPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;isSneaking()Z"))
//    private boolean onUpdateWalkingPlayerIsSneaking(EntityPlayerSP playerIn) {
//        return BPKMod$isSneakPressed();
//    }

    @Override
    public boolean BPKMod$isSneakPressed(){
        boolean sneakPressed = movementInput != null && movementInput.sneak;
        return sneakPressed && !sleeping;
    }

    @Unique
    private boolean BPKMod$isSneakingPose(){
        return height - ((IPlayerResizable)this).BPKMod$getSneakHeight() < 1.0E-4F;
    }

    @ModifyVariable(method = "pushOutOfBlocks", at = @At("HEAD"), argsOnly = true, ordinal = 1)
    private double considerInaccuracy(double y){
        return y - 1.0E-7D;
    }

    @Override
    public float BPKMod$getLastCameraY() {
        return BPKMod$lastCameraY;
    }

    @Override
    public float BPKMod$getCameraY() {
        return BPKMod$cameraY;
    }

    @Override
    public void BPKMod$updateCameraHeight(double tickDelta) {
        this.BPKMod$lastCameraY = this.BPKMod$getCameraY();
        BPKMod$cameraY = (float) MathHelper.clampedLerp(this.BPKMod$getLastCameraY(), getEyeHeight(), tickDelta / 2);
    }
}
