package cn.xiaozi0721.bpk.mixin.minecraft.client.entity;

import cn.xiaozi0721.bpk.config.ConfigHandler.GeneralConfig;
import cn.xiaozi0721.bpk.interfaces.IEntityPlayer;
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
public abstract class MixinEntityPlayerSP extends AbstractClientPlayer implements IRenderViewEntity, IEntityPlayer {
    @Shadow public MovementInput movementInput;

    @Unique private float BPKMod$lastCameraY;
    @Unique private float BPKMod$cameraY;
    @Unique private boolean BPKMod$underBlock;

    public MixinEntityPlayerSP(World worldIn, GameProfile playerProfile) {
        super(worldIn, playerProfile);
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

    @Redirect(method = "onLivingUpdate",at = @At(value = "FIELD", target = "Lnet/minecraft/util/MovementInput;moveForward:F", ordinal = 5))
    private float sprintBackward(MovementInput movementInput){
        return GeneralConfig.sprintBackward && !isSneaking() && movementInput.moveForward != 0 ? 1 : movementInput.moveForward;
    }

    @Redirect(method = "onLivingUpdate", at = @At(value = "FIELD", target = "Lnet/minecraft/client/entity/EntityPlayerSP;collidedHorizontally:Z"))
    private boolean ignoreCollidedHorizontally(EntityPlayerSP instance){
        return false;
    }

    @Inject(method = "onLivingUpdate", at = @At("HEAD"))
    private void updateUnderBlock(CallbackInfo ci) {
        AxisAlignedBB normalAABB = this.getEntityBoundingBox();
        AxisAlignedBB sneakAABB = new AxisAlignedBB(normalAABB.minX, normalAABB.minY, normalAABB.minZ, normalAABB.minX + 0.6D, normalAABB.minY + BPKMod$getSneakHeight() - 1.0E-7, normalAABB.minZ + 0.6D);
        normalAABB = new AxisAlignedBB(normalAABB.minX, normalAABB.minY, normalAABB.minZ, normalAABB.minX + 0.6D, normalAABB.minY + 1.8D - 1.0E-7, normalAABB.minZ + 0.6D);
        BPKMod$underBlock = this.world.collidesWithAnyBlock(normalAABB) && !this.world.collidesWithAnyBlock(sneakAABB);
    }

    @Inject(method = "onLivingUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/MovementInput;updatePlayerMoveState()V", shift = At.Shift.AFTER))
    private void updateSneakInput(CallbackInfo ci) {
        if (!movementInput.sneak && BPKMod$underBlock) {
            movementInput.sneak = true;
            movementInput.moveStrafe *= 0.3F;
            movementInput.moveForward *= 0.3F;
        }
    }

    @ModifyVariable(method = "pushOutOfBlocks", at = @At("HEAD"), ordinal = 1, argsOnly = true)
    private double considerInaccuracy(double y){
        return y - 1.0e-7;
    }

//    @Unique
//    private boolean isSneakingPose(){
//        return height - BPKMod$getSneakHeight() < 1.0e-4;
//    }
}
