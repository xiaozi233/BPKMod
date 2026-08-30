package cn.xiaozi0721.bpk.client.mixin;

import cn.xiaozi0721.bpk.config.ConfigHandler;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec2;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
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

    @ModifyExpressionValue(method = "shouldStopRunSprinting", at = @At(value = "FIELD", target = "Lnet/minecraft/client/player/LocalPlayer;horizontalCollision:Z", opcode = Opcodes.GETFIELD))
    private boolean bpk$ignoreCollidedHorizontally(boolean original) {
        return !ConfigHandler.generalConfig.ignoreCollidedHorizontally && original;
    }

    @ModifyReturnValue(method = "modifyInputSpeedForSquareMovement", at = @At("RETURN"))
    private static Vec2 bpk$allowRawDiagonalInput(Vec2 original, Vec2 input) {
        return ConfigHandler.generalConfig.strafeAccelerateAllowed ? original : input;
    }

    @Unique
    private boolean bpk$isSprintBackward() {
        return ConfigHandler.generalConfig.sprintBackward && this.input.getMoveVector().y < -1.0E-5F;
    }
}
