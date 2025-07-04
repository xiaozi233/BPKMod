package cn.xiaozi0721.bpk.mixin.client.renderer;

import cn.xiaozi0721.bpk.config.ConfigHandler.GeneralConfig;
import cn.xiaozi0721.bpk.interfaces.ILerpSneakCameraEntity;
import cn.xiaozi0721.bpk.interfaces.ILerpSneakGameRenderer;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRender implements ILerpSneakGameRenderer {
    @Shadow @Final private Minecraft mc;

    @Unique private float BPKMod$tickDelta;

    @Inject(method = "orientCamera", at = @At(value = "HEAD"))
    private void getTickDelta(float tickDelta, CallbackInfo ci) {
        this.BPKMod$tickDelta = tickDelta;
    }

    @WrapOperation(method = "orientCamera", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getEyeHeight()F"))
    private float lerpSneak(Entity entity, Operation<Float> original) {
        if(GeneralConfig.isBESneak && entity instanceof EntityPlayer){
            return (float) MathHelper.clampedLerp(
                    ((ILerpSneakCameraEntity) entity).BPKMod$getLastCameraY(),
                    ((ILerpSneakCameraEntity) entity).BPKMod$getCameraY(),
                    BPKMod$tickDelta
            );
        } else {
            return original.call(entity);
        }
    }

    @Inject(method = "updateRenderer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/WorldClient;getLightBrightness(Lnet/minecraft/util/math/BlockPos;)F"))
    private void updateCameraHeight(CallbackInfo ci) {
        Entity renderViewEntity = this.mc.getRenderViewEntity();
        if (GeneralConfig.isBESneak && renderViewEntity instanceof EntityPlayer) {
            ((ILerpSneakCameraEntity) renderViewEntity).BPKMod$updateCameraHeight(BPKMod$tickDelta);
        }
    }

    @Override
    public float BPKMod$getTickDelta() {
        return BPKMod$tickDelta;
    }
}
