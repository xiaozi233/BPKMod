package cn.xiaozi0721.bpk.mixin.minecraft.client.renderer;

import cn.xiaozi0721.bpk.interfaces.IRenderViewEntity;
import cn.xiaozi0721.bpk.interfaces.IEntityRender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class MixinEntityRender implements IEntityRender{
    @Shadow @Final private Minecraft mc;

    @Unique private float BPKMod$tickDelta;

    @Inject(method = "orientCamera", at = @At(value = "HEAD"))
    private void getTickDelta(float tickDelta, CallbackInfo ci) {
        this.BPKMod$tickDelta = tickDelta;
    }

    @Redirect(method = "orientCamera", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getEyeHeight()F"))
    private float lerpSneak(Entity entity) {
        return entity instanceof EntityPlayer ? (float) MathHelper.clampedLerp(((IRenderViewEntity) entity).BPKMod$getLastCameraY(), ((IRenderViewEntity) entity).BPKMod$getCameraY(), BPKMod$tickDelta)
                                                : entity.getEyeHeight();
    }

    @Inject(method = "updateRenderer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/WorldClient;getLightBrightness(Lnet/minecraft/util/math/BlockPos;)F"))
    private void updateCameraHeight(CallbackInfo ci) {
        if (this.mc.getRenderViewEntity() instanceof EntityPlayer) {
            ((IRenderViewEntity) this.mc.getRenderViewEntity()).BPKMod$updateCameraHeight(BPKMod$tickDelta);
        }else{
            this.mc.world.getLightBrightness(new BlockPos(this.mc.getRenderViewEntity().getPositionEyes(1F)));
        }
    }

    @Override
    public float BPKMod$getTickDelta() {
        return BPKMod$tickDelta;
    }
}
