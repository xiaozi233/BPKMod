package cn.xiaozi0721.bpk.mixin.world.level.block;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.references.BlockItemId;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

import java.util.function.Function;

@Mixin(Blocks.class)
public abstract class MixinBlocks {
    @WrapOperation(
            method = "<clinit>",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Blocks;register(Lnet/minecraft/references/BlockItemId;Ljava/util/function/Function;Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/Block;"),
            slice = @Slice(
                    from = @At(value = "FIELD", target = "Lnet/minecraft/references/BlockItemIds;HONEY_BLOCK:Lnet/minecraft/references/BlockItemId;", opcode = Opcodes.GETSTATIC),
                    to = @At(value = "FIELD", target = "Lnet/minecraft/world/level/block/Blocks;HONEY_BLOCK:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)
            )
    )
    private static Block honeyBlockProperties(BlockItemId id, Function<BlockBehaviour.Properties, Block> factory, BlockBehaviour.Properties properties, Operation<Block> original) {
        return original.call(id, factory, properties.jumpFactor(0.6F).speedFactor(1F).friction(0.8F));
    }
}
