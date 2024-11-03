package cn.xiaozi0721.bpk.mixin.minecraft.client.renderer;

import cn.xiaozi0721.bpk.interfaces.IRenderViewEntity;
import cn.xiaozi0721.bpk.interfaces.IEntityRender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ActiveRenderInfo.class)
public class MixinActiveRenderInfo {
    @Unique private static Entity BPKMod$EntityPlayer;

    @ModifyArg(method = "updateRenderInfo(Lnet/minecraft/entity/Entity;Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;<init>(DDD)V"), index = 1)
    private static double modifySetPosY(double y) {
        return MathHelper.clampedLerp(BPKMod$EntityPlayer.prevPosY, BPKMod$EntityPlayer.posY + MathHelper.clampedLerp(((IRenderViewEntity) BPKMod$EntityPlayer).BPKMod$getLastCameraY(), ((IRenderViewEntity) BPKMod$EntityPlayer).BPKMod$getCameraY(), ((IEntityRender) Minecraft.getMinecraft().entityRenderer).BPKMod$getTickDelta()), ((IEntityRender) Minecraft.getMinecraft().entityRenderer).BPKMod$getTickDelta());
    }

    @Inject(method = "updateRenderInfo(Lnet/minecraft/entity/Entity;Z)V", at = @At(value = "HEAD"), remap = false)
    private static void getPlayer(Entity entityplayerIn, boolean p_74583_1_, CallbackInfo ci) {
        BPKMod$EntityPlayer =  entityplayerIn;
    }
}