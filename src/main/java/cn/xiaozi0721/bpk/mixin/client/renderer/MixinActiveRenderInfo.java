package cn.xiaozi0721.bpk.mixin.client.renderer;

import cn.xiaozi0721.bpk.interfaces.ILerpSneakCameraEntity;
import cn.xiaozi0721.bpk.interfaces.ILerpSneakGameRenderer;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ActiveRenderInfo.class)
public abstract class MixinActiveRenderInfo{
    @ModifyArg(method = "updateRenderInfo(Lnet/minecraft/entity/Entity;Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;<init>(DDD)V"), index = 1, remap = false)
    private static double modifySetPosY(double y, @Share("prevEntity") LocalRef<Entity> entityRef) {
        Entity entity = entityRef.get();
        if(entity instanceof EntityPlayer){
            return MathHelper.clampedLerp(
                    entity.prevPosY,
                    entity.posY + MathHelper.clampedLerp(
                            ((ILerpSneakCameraEntity) entity).BPKMod$getLastCameraY(),
                            ((ILerpSneakCameraEntity) entity).BPKMod$getCameraY(),
                            ((ILerpSneakGameRenderer) Minecraft.getMinecraft().entityRenderer).BPKMod$getTickDelta()
                    ),
                    ((ILerpSneakGameRenderer) Minecraft.getMinecraft().entityRenderer).BPKMod$getTickDelta()
            );
        } else {
            return y;
        }
    }

    @Inject(method = "updateRenderInfo(Lnet/minecraft/entity/Entity;Z)V", at = @At(value = "HEAD"), remap = false)
    private static void getPlayer(Entity entityIn, boolean p_74583_1_, CallbackInfo ci, @Share("prevEntity") LocalRef<Entity> entityRef) {
        entityRef.set(entityIn);
    }
}
