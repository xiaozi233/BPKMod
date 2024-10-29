package cn.xiaozi0721.bpk.mixin.client.entity;

import cn.xiaozi0721.bpk.interfaces.IRenderViewEntity;
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
import org.spongepowered.asm.mixin.injection.Redirect;

import static cn.xiaozi0721.bpk.config.GeneralConfig.sprintBackward;


@Mixin(EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP extends AbstractClientPlayer implements IRenderViewEntity {
    @Shadow public abstract void setSprinting(boolean sprinting);
    @Unique private float BPKMod$lastCameraY;
    @Unique private float BPKMod$cameraY;

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
    private float setMoveForward(MovementInput movementInput){
        return sprintBackward && movementInput.moveForward !=0 ? 1 : movementInput.moveForward;
    }

    @Redirect(method = "onLivingUpdate", at = @At(value = "FIELD", target = "Lnet/minecraft/client/entity/EntityPlayerSP;collidedHorizontally:Z"))
    private boolean ignoreCollidedHorizontally(EntityPlayerSP instance){
        return false;
    }
}
