package cn.xiaozi0721.bpk.mixin.aquaacrobatics.client.entity;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.MovementInput;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static cn.xiaozi0721.bpk.config.GeneralConfig.sprintBackward;

@Mixin(value = EntityPlayerSP.class, priority = 2000)
public abstract class ReMixinEntityPlayerSP extends AbstractClientPlayer {
    @Shadow public abstract boolean isSneaking();

    public ReMixinEntityPlayerSP(World worldIn, GameProfile playerProfile) {
        super(worldIn, playerProfile);
    }

    @Redirect(method = "stopSprinting",at = @At(value = "FIELD", target = "Lnet/minecraft/util/MovementInput;moveForward:F"), remap = false)
    @Dynamic("For aquaacrobatics")
    private float setMoveForward(MovementInput movementInput){
        return sprintBackward && movementInput.moveForward !=0 ? 1 : movementInput.moveForward;
    }

    @Redirect(method = "stopSprinting", at = @At(value = "FIELD", target = "Lnet/minecraft/client/entity/EntityPlayerSP;collidedHorizontally:Z"), remap = false)
    @Dynamic("For aquaacrobatics")
    private boolean ignoreCollidedHorizontally(EntityPlayerSP instance){
        return false;
    }
}
