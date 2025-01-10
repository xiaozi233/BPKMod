package cn.xiaozi0721.bpk.mixin.util;

import cn.xiaozi0721.bpk.config.ConfigHandler.GeneralConfig;
import net.minecraft.util.MovementInput;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MovementInputFromOptions.class)
public class MixinMovementInputFromOptions extends MovementInput {
    @Inject(method = "updatePlayerMoveState", at = @At(value = "FIELD", target = "Lnet/minecraft/util/MovementInputFromOptions;jump:Z"))
    private void strafe(CallbackInfo ci){
        if(!GeneralConfig.strafeAccelerateAllowed){
            float distance = MathHelper.sqrt(this.moveForward * this.moveForward + this.moveStrafe * this.moveStrafe);
            if (distance >= 1.0E-4){
                this.moveForward /= distance;
                this.moveStrafe /= distance;
            }
        }
    }
}
