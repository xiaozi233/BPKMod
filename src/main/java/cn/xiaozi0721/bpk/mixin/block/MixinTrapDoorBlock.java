package cn.xiaozi0721.bpk.mixin.block;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TrapDoorBlock.class)
public abstract class MixinTrapDoorBlock {
    @Unique
    private static final double BEDROCK_THICKNESS = 0.1825D * 16.0D;

    @WrapOperation(
        method = "<clinit>",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;boxZ(DDD)Lnet/minecraft/world/phys/shapes/VoxelShape;")
    )
    private static VoxelShape bedrockThickness(double sizeXY, double minZ, double maxZ, Operation<VoxelShape> original) {
        return original.call(sizeXY, 16.0D - BEDROCK_THICKNESS, maxZ);
    }
}
