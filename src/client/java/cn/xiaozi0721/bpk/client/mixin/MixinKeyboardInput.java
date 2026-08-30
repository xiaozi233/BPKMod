package cn.xiaozi0721.bpk.client.mixin;

import cn.xiaozi0721.bpk.config.ConfigHandler;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.world.phys.Vec2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(KeyboardInput.class)
public abstract class MixinKeyboardInput {
    @Redirect(
        method = "tick",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec2;normalized()Lnet/minecraft/world/phys/Vec2;")
    )
    private Vec2 bpk$strafeAccelerate(Vec2 moveVector) {
        if (!ConfigHandler.generalConfig.strafeAccelerateAllowed || moveVector.x == 0.0F || moveVector.y == 0.0F) {
            return moveVector;
        }
        return new Vec2(Math.signum(moveVector.x), Math.signum(moveVector.y));
    }
}
