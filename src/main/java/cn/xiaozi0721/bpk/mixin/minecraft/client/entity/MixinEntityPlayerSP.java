package cn.xiaozi0721.bpk.mixin.minecraft.client.entity;

import cn.xiaozi0721.bpk.config.ConfigHandler.GeneralConfig;
import cn.xiaozi0721.bpk.interfaces.IEntityPlayer;
import cn.xiaozi0721.bpk.interfaces.IEntityPlayerSP;
import cn.xiaozi0721.bpk.interfaces.IRenderViewEntity;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.AxisAlignedBB;
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
public abstract class MixinEntityPlayerSP extends AbstractClientPlayer implements IRenderViewEntity, IEntityPlayerSP, IEntityPlayer {
    @Shadow public MovementInput movementInput;

    @Unique private float BPKMod$lastCameraY;
    @Unique private float BPKMod$cameraY;

    public MixinEntityPlayerSP(World worldIn, GameProfile playerProfile) {
        super(worldIn, playerProfile);
    }

    @Redirect(method = "onLivingUpdate", at = @At(value = "FIELD", target = "Lnet/minecraft/util/MovementInput;moveForward:F", ordinal = 5))
    private float sprintBackward(MovementInput movementInput){
        return GeneralConfig.sprintBackward && !isSneaking() && movementInput.moveForward != 0 ? 1 : movementInput.moveForward;
    }

    @Redirect(method = "onLivingUpdate", at = @At(value = "FIELD", target = "Lnet/minecraft/client/entity/EntityPlayerSP;collidedHorizontally:Z"))
    private boolean ignoreCollidedHorizontally(EntityPlayerSP instance){
        return false;
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
        if (GeneralConfig.beSneak && !movementInput.sneak && (BPKMod$getUnderBlock() || BPKMod$isSneakingPose()) && !this.capabilities.isFlying) {
            movementInput.moveStrafe *= 0.3F;
            movementInput.moveForward *= 0.3F;
        }
    }

    @ModifyVariable(method = "isSneaking", at = @At("STORE"))
    private boolean isSneakingOrUnderBlock(boolean isSneaking){
        return isSneaking || (GeneralConfig.beSneak && (BPKMod$getUnderBlock() || BPKMod$isSneakingPose() && !BPKMod$getResizingAllowed()));
    }

//    @Redirect(method = "onUpdateWalkingPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;isSneaking()Z"))
//    private boolean onUpdateWalkingPlayerIsSneaking(EntityPlayerSP playerIn) {
//        return BPKMod$isSneakPressed();
//    }

    @Override
    public boolean BPKMod$isSneakPressed(){
        boolean flag = movementInput != null && movementInput.sneak;
        return flag && !sleeping;
    }

    @Unique
    private boolean BPKMod$isSneakingPose(){
        return height - BPKMod$getSneakHeight() < 1.0e-4;
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
