package cn.xiaozi0721.bpk.client.mixin;

import cn.xiaozi0721.bpk.config.ConfigHandler;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class MixinLocalPlayer {
    @Shadow
    public ClientInput input;

    @Unique
    private Vec3 prevDelta = Vec3.ZERO;

    @Unique
    private float deltaX = 0;

    @Unique
    private float deltaZ = 0;

    @Shadow
    protected abstract boolean isSprintingPossible(boolean allowedInShallowWater);

    @ModifyExpressionValue(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/ClientInput;hasForwardImpulse()Z"))
    private boolean sprintBackwardImpulse(boolean original) {
        return original || this.isSprintBackward();
    }

    @ModifyExpressionValue(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Input;backward()Z"))
    private boolean sprintBackwardTrigger(boolean original) {
        return original && !ConfigHandler.generalConfig.sprintBackward;
    }

    @ModifyExpressionValue(method = "canStartSprinting", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/ClientInput;hasForwardImpulse()Z"))
    private boolean sprintBackwardStart(boolean original) {
        return original || this.isSprintBackward();
    }

    @ModifyExpressionValue(method = "shouldStopRunSprinting", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/ClientInput;hasForwardImpulse()Z"))
    private boolean sprintBackwardStop(boolean original) {
        return original || this.isSprintBackward();
    }

    @Inject(method = "move", at = @At("HEAD"))
    private void capturePrevDelta(MoverType moverType, Vec3 delta, CallbackInfo ci) {
        this.prevDelta = delta;
    }

    @Inject(method = "move", at = @At("TAIL"))
    private void captureDeltaPos(MoverType moverType, Vec3 delta, CallbackInfo ci, @Local(name = "deltaX") float deltaX, @Local(name = "deltaZ") float deltaZ) {
        this.deltaX = deltaX;
        this.deltaZ = deltaZ;
    }
    
    @WrapMethod(method = "shouldStopRunSprinting")
    private boolean beCollisionStopsSprint(Operation<Boolean> original) {
        if (!ConfigHandler.generalConfig.beCollisionStopsSprint) {
            return original.call();
        }
        LocalPlayer self = (LocalPlayer) (Object) this;
        return !this.isSprintingPossible(self.getAbilities().flying)
                || !this.input.hasForwardImpulse()
                || (Math.abs(prevDelta.z) > Math.abs(prevDelta.x) && Math.abs(deltaZ) < 5e-5)
                || (Math.abs(prevDelta.x) > Math.abs(prevDelta.z) && Math.abs(deltaX) < 5e-5);
    }

    @ModifyReturnValue(method = "modifyInputSpeedForSquareMovement", at = @At("RETURN"))
    private static Vec2 allowRawDiagonalInput(Vec2 original, Vec2 input) {
        return ConfigHandler.generalConfig.strafeAccelerateAllowed ? original : input;
    }

    @Unique
    private boolean isSprintBackward() {
        return ConfigHandler.generalConfig.sprintBackward && this.input.getMoveVector().y < -1.0E-5F;
    }
}
