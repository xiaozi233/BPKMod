package cn.xiaozi0721.bpk.mixin.minecraft.client.entity;

import cn.xiaozi0721.bpk.config.ConfigHandler.GeneralConfig;
import cn.xiaozi0721.bpk.interfaces.IPlayerPressingSneak;
import cn.xiaozi0721.bpk.interfaces.IPlayerResizable;
import cn.xiaozi0721.bpk.interfaces.ILerpSneakCameraEntity;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
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

    @ModifyExpressionValue(method = "onLivingUpdate", at = @At(value = "FIELD", target = "Lnet/minecraft/client/entity/EntityPlayerSP;collidedHorizontally:Z"))
    private boolean ignoreCollidedHorizontally(boolean collidedHorizontally){
        return GeneralConfig.ignoreCollidedHorizontally ? false : collidedHorizontally;
    }

//    @Inject(method = "onLivingUpdate", at = @At("HEAD"))
//    private void updateSneakPose(CallbackInfo ci) {
//        AxisAlignedBB normalAABB = this.getEntityBoundingBox();
//        AxisAlignedBB sneakAABB = new AxisAlignedBB(normalAABB.minX, normalAABB.minY, normalAABB.minZ, normalAABB.minX + 0.6D, normalAABB.minY + BPKMod$getSneakHeight() - 1.0E-7, normalAABB.minZ + 0.6D);
//        normalAABB = new AxisAlignedBB(normalAABB.minX, normalAABB.minY, normalAABB.minZ, normalAABB.minX + 0.6D, normalAABB.minY + 1.8D - 1.0E-7, normalAABB.minZ + 0.6D);
//        BPKMod$resizingAllowed = !this.world.collidesWithAnyBlock(normalAABB);
//        BPKMod$underBlock = !BPKMod$resizingAllowed && !this.world.collidesWithAnyBlock(sneakAABB);
//    }

    @Inject(method = "onLivingUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/MovementInput;updatePlayerMoveState()V", shift = At.Shift.AFTER))
    private void updateSneakInput(CallbackInfo ci) {
        if (GeneralConfig.beSneak && !movementInput.sneak && (((IPlayerResizable)this).BPKMod$getUnderBlock() || BPKMod$isSneakingPose()) && !this.capabilities.isFlying) {
            movementInput.moveStrafe *= 0.3F;
            movementInput.moveForward *= 0.3F;
        }
    }

    @ModifyVariable(method = "isSneaking", at = @At("STORE"))
    private boolean isSneakingOrUnderBlock(boolean isSneaking){
        return isSneaking || (GeneralConfig.beSneak && (((IPlayerResizable)this).BPKMod$getUnderBlock() || BPKMod$isSneakingPose() && !((IPlayerResizable)this).BPKMod$getResizingAllowed()));
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
        return height - ((IPlayerResizable)this).BPKMod$getSneakHeight() < 1.0e-4;
    }

    @ModifyVariable(method = "pushOutOfBlocks", at = @At("HEAD"), ordinal = 1, argsOnly = true)
    private double considerInaccuracy(double y){
        return y - 1.0E-7;
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
