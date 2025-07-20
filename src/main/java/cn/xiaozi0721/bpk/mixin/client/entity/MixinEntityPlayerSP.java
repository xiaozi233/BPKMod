package cn.xiaozi0721.bpk.mixin.client.entity;

import cn.xiaozi0721.bpk.config.ConfigHandler.GeneralConfig;
import cn.xiaozi0721.bpk.interfaces.IPlayerPressingSneak;
import cn.xiaozi0721.bpk.interfaces.ILerpSneakCameraEntity;
import cn.xiaozi0721.bpk.mixin.accessor.EntityLivingBaseAccessor;
import cn.xiaozi0721.bpk.mixin.entity.player.MixinEntityPlayer;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
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
public abstract class MixinEntityPlayerSP extends MixinEntityPlayer implements ILerpSneakCameraEntity, IPlayerPressingSneak {
    @Shadow public MovementInput movementInput;

    @Unique private float BPKMod$lastCameraY;
    @Unique private float BPKMod$cameraY;

    public MixinEntityPlayerSP(World worldIn) {
        super(worldIn);
    }

    @ModifyExpressionValue(method = "onLivingUpdate", at = @At(value = "FIELD", target = "Lnet/minecraft/util/MovementInput;moveForward:F", ordinal = 5))
    private float sprintBackward(float moveForward){
        return GeneralConfig.sprintBackward && !this.isSneaking() && moveForward != 0 ? 1 : moveForward;
    }

    @ModifyExpressionValue(method = "onLivingUpdate", at = {
            @At(value = "CONSTANT", args = "floatValue=0.8", ordinal = 3),
            @At(value = "CONSTANT", args = "floatValue=0.8", ordinal = 4)
    })
    private float cannotSprintWhenStrafe(float origin){
        return GeneralConfig.nonSprintingStrafe ? 1F : origin;
    }


    @ModifyExpressionValue(
            method = "onLivingUpdate",
            at = @At(value = "CONSTANT", args = "floatValue=0.8"),
            slice = @Slice(
                    from = @At(value = "CONSTANT", args = "floatValue=0.8", ordinal = 1),
                    to = @At(value = "CONSTANT", args = "floatValue=0.8", ordinal = 5)
            )
    )
    private float hasForwardImpulse(float origin){
        return 1.0E-5F;
    }

    @ModifyExpressionValue(method = "onLivingUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/settings/KeyBinding;isKeyDown()Z", ordinal = 1))
    private boolean setSprintTrueConsiderSneak(boolean origin){
        return origin && !this.BPKMod$isSneakingPose();
    }

    @ModifyExpressionValue(method = "onLivingUpdate", at = @At(value = "FIELD", target = "Lnet/minecraft/client/entity/EntityPlayerSP;collidedHorizontally:Z"))
    private boolean setSprintFalseConsiderSneak(boolean origin){
        return origin || this.BPKMod$isSneakingPose();
    }

    @Inject(method = "onLivingUpdate", at = @At(value = "FIELD", target = "Lnet/minecraft/client/entity/EntityPlayerSP;onGround:Z", ordinal = 0))
    private void updatePrevSprinting(CallbackInfo ci, @Share("prevSprinting") LocalBooleanRef prevSprintingRef){
        prevSprintingRef.set(this.isSprinting());
    }

    @Inject(method = "onLivingUpdate", at = @At(value = "FIELD", target = "Lnet/minecraft/client/entity/EntityPlayerSP;capabilities:Lnet/minecraft/entity/player/PlayerCapabilities;", ordinal = 1))
    private void sprintDelayOnGround(CallbackInfo ci, @Share("prevSprinting") LocalBooleanRef prevSprintingRef){
        if (GeneralConfig.sprintDelayOnGround && !prevSprintingRef.get() && this.isSprinting()){
            this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(EntityLivingBaseAccessor.getSpringSpeedBoost());
        }
    }

    @ModifyExpressionValue(method = "onLivingUpdate", at = @At(value = "FIELD", target = "Lnet/minecraft/client/entity/EntityPlayerSP;collidedHorizontally:Z"))
    private boolean ignoreCollidedHorizontally(boolean collidedHorizontally){
        return !GeneralConfig.ignoreCollidedHorizontally && collidedHorizontally;
    }

    @Inject(method = "onLivingUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/MovementInput;updatePlayerMoveState()V", shift = At.Shift.AFTER))
    private void updateSneakInput(CallbackInfo ci) {
        if (GeneralConfig.isBESneak && !this.movementInput.sneak && BPKMod$isSneakingPose() && !this.capabilities.isFlying) {
            this.movementInput.moveStrafe *= 0.3F;
            this.movementInput.moveForward *= 0.3F;
        }
    }

    @ModifyVariable(method = "isSneaking", at = @At("STORE"))
    private boolean isSneaking(boolean isSneaking){
        return isSneaking || (GeneralConfig.isBESneak && this.BPKMod$underBlock && !this.BPKMod$resizingAllowed);
    }

//    @Redirect(method = "onUpdateWalkingPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;isSneaking()Z"))
//    private boolean onUpdateWalkingPlayerIsSneaking(EntityPlayerSP playerIn) {
//        return BPKMod$isSneakPressed();
//    }

    @Override
    public boolean BPKMod$isSneakPressed(){
        boolean sneakPressed = this.movementInput != null && this.movementInput.sneak;
        return sneakPressed && !this.sleeping;
    }

    @Unique
    private boolean BPKMod$isSneakingPose(){
        return this.height == GeneralConfig.sneakHeight;
    }

    @ModifyVariable(method = "pushOutOfBlocks", at = @At("HEAD"), argsOnly = true, ordinal = 1)
    private double considerInaccuracy(double y){
        return y - 1.0E-7D;
    }

    @Override
    public float BPKMod$getLastCameraY() {
        return this.BPKMod$lastCameraY;
    }

    @Override
    public float BPKMod$getCameraY() {
        return this.BPKMod$cameraY;
    }

    @Override
    public void BPKMod$updateCameraHeight(double tickDelta) {
        this.BPKMod$lastCameraY = this.BPKMod$getCameraY();
        this.BPKMod$cameraY = (float) MathHelper.clampedLerp(this.BPKMod$getLastCameraY(), this.getEyeHeight(), tickDelta / 2);
    }
}
