package cn.xiaozi0721.bpk.mixin.block;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.HoneyBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HoneyBlock.class)
public abstract class MixinHoneyBlock extends HalfTransparentBlock {
    @Shadow @Final @Mutable
    private static final VoxelShape SHAPE = Block.column(14.0, 0.0, 16.0);

    public MixinHoneyBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void stepOn(final Level level, final BlockPos pos, final BlockState onState, final Entity entity) {
        double absDeltaY = Math.abs(entity.getDeltaMovement().y);
        if (absDeltaY < 0.1 && !entity.isSteppingCarefully()) {
            double scale = 0.4 + absDeltaY * 0.2;
            entity.setDeltaMovement(entity.getDeltaMovement().multiply(scale, 1.0, scale));
        }

        super.stepOn(level, pos, onState, entity);
    }

    // Bedrock slides against honey where JE throttles: every tick the entity box is inside a
    // honey block, Bedrock does vx,vz *= 0.4 and vy = max(vy, -0.12) unconditionally per
    // intersected block (terminal fall 0.12/tick = 2.4 b/s). JE instead scales the horizontal
    // by -0.05/vy and only throttles the fall. entityInside runs after travel (gravity+drag)
    // in 26.2, so the clamped value is what the next tick moves by, same as Bedrock.
    @Inject(method = "entityInside", at = @At("TAIL"))
    private void bedrockSlide(final BlockState state, final Level level, final BlockPos pos, final Entity entity,
                                  final InsideBlockEffectApplier effectApplier, final boolean isPrecise, final CallbackInfo ci) {
        Vec3 delta = entity.getDeltaMovement();
        entity.setDeltaMovement(delta.x * 0.4, Math.max(delta.y, -0.12), delta.z * 0.4);
    }

    @WrapOperation(
            method = "entityInside",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/HoneyBlock;doSlideMovement(Lnet/minecraft/world/entity/Entity;)V")
    )
    private void replaceSlideThrottle(final HoneyBlock instance, final Entity entity, final Operation<Void> original) {
        // The clamp above replaces JE's throttle, so doSlideMovement's velocity write is dropped.
        // Its one effect Bedrock has no equivalent for (and that sliding down honey relies on)
        // is kept: the fall distance is reset while sliding, so a slide never adds fall damage.
        entity.resetFallDistance();
    }
}
