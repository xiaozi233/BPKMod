package cn.xiaozi0721.bpk.client.mixin;

import cn.xiaozi0721.bpk.config.ConfigHandler;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LocalPlayer.class)
public abstract class MixinLocalPlayer {
    @Shadow
    public ClientInput input;

    @ModifyExpressionValue(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/ClientInput;hasForwardImpulse()Z"))
    private boolean bpk$sprintBackwardImpulse(boolean original) {
        return original || this.bpk$isSprintBackward();
    }

    @ModifyExpressionValue(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Input;backward()Z"))
    private boolean bpk$sprintBackwardTrigger(boolean original) {
        return original && !ConfigHandler.generalConfig.sprintBackward;
    }

    @ModifyExpressionValue(method = "canStartSprinting", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/ClientInput;hasForwardImpulse()Z"))
    private boolean bpk$sprintBackwardStart(boolean original) {
        return original || this.bpk$isSprintBackward();
    }

    @ModifyExpressionValue(method = "shouldStopRunSprinting", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/ClientInput;hasForwardImpulse()Z"))
    private boolean bpk$sprintBackwardStop(boolean original) {
        return original || this.bpk$isSprintBackward();
    }

    @ModifyExpressionValue(method = "shouldStopRunSprinting", at = @At(value = "FIELD", target = "Lnet/minecraft/client/player/LocalPlayer;horizontalCollision:Z"))
    private boolean bpk$ignoreCollidedHorizontally(boolean original) {
        return !ConfigHandler.generalConfig.ignoreCollidedHorizontally && original;
    }

    private boolean bpk$isSprintBackward() {
        return ConfigHandler.generalConfig.sprintBackward && this.input.getMoveVector().y < -1.0E-5F;
    }
}
