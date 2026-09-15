package cn.xiaozi0721.bpk.mixin.world.level.block;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(Blocks.class)
public abstract class MixinBlocks {
    @ModifyArg(
            method = "<clinit>",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Blocks;register(Lnet/minecraft/references/BlockItemId;Ljava/util/function/Function;Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/Block;"),
            slice = @Slice(
                    from = @At(value = "FIELD", target = "Lnet/minecraft/references/BlockItemIds;HONEY_BLOCK:Lnet/minecraft/references/BlockItemId;", opcode = Opcodes.GETSTATIC),
                    to = @At(value = "FIELD", target = "Lnet/minecraft/world/level/block/Blocks;HONEY_BLOCK:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)
            )
    )
    private static BlockBehaviour.Properties honeyBlockProperties(BlockBehaviour.Properties properties) {
        return properties.jumpFactor(0.6F).speedFactor(1F).friction(0.8F);
    }

    @ModifyArg(
            method = "<clinit>",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Blocks;register(Lnet/minecraft/references/BlockItemId;Ljava/util/function/Function;Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/Block;"),
            slice = @Slice(
                    from = @At(value = "FIELD", target = "Lnet/minecraft/references/BlockItemIds;SOUL_SAND:Lnet/minecraft/references/BlockItemId;", opcode = Opcodes.GETSTATIC),
                    to = @At(value = "FIELD", target = "Lnet/minecraft/world/level/block/Blocks;SOUL_SAND:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)
            )
    )
    private static BlockBehaviour.Properties soulSandProperties(BlockBehaviour.Properties properties) {
        return properties.speedFactor(1F);
    }
}
