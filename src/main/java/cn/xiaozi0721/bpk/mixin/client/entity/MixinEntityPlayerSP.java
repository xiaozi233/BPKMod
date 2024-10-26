package cn.xiaozi0721.bpk.mixin.client.entity;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.MovementInput;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static cn.xiaozi0721.bpk.config.GeneralConfig.sprintBackward;


@Mixin(EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP extends AbstractClientPlayer {
    @Shadow public abstract void setSprinting(boolean sprinting);

    public MixinEntityPlayerSP(World worldIn, GameProfile playerProfile) {
        super(worldIn, playerProfile);
    }


    @Redirect(method = "onLivingUpdate",at = @At(value = "FIELD", target = "Lnet/minecraft/util/MovementInput;moveForward:F", ordinal = 5))
    private float setmoveForward(MovementInput movementInput){

        return sprintBackward && movementInput.moveForward !=0 ? 1 : movementInput.moveForward;
    }

    @Redirect(method = "onLivingUpdate", at = @At(value = "FIELD", target = "Lnet/minecraft/client/entity/EntityPlayerSP;collidedHorizontally:Z"))
    private boolean setCollidedHorizontally(EntityPlayerSP instance){
        return false;
    }
}
