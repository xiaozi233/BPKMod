package cn.xiaozi0721.bpk.mixin.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TrapDoorBlock.class)
public abstract class MixinTrapDoorBlock {
    private static final double BPK$BEDROCK_THICKNESS = 0.1825D * 16.0D;

    @Redirect(
        method = "<clinit>",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;boxZ(DDD)Lnet/minecraft/world/phys/shapes/VoxelShape;")
    )
    private static VoxelShape bpk$bedrockThickness(double sizeXY, double minZ, double maxZ) {
        return Block.boxZ(sizeXY, 16.0D - BPK$BEDROCK_THICKNESS, maxZ);
    }
}
