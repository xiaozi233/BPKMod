package cn.xiaozi0721.bpk.mixin.block;

import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockChest.class)
public abstract class MixinBlockChest extends BlockContainer {
    protected MixinBlockChest(Material materialIn) {
        super(materialIn);
    }
    @Unique private static final AxisAlignedBB NORTH_CHEST_AABB = new AxisAlignedBB(0.025D, 0.0D, 0.0D, 0.975D, 0.875D, 0.975D);
    @Unique private static final AxisAlignedBB SOUTH_CHEST_AABB = new AxisAlignedBB(0.025D, 0.0D, 0.025D, 0.975D, 0.875D, 1.0D);
    @Unique private static final AxisAlignedBB WEST_CHEST_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.025D, 0.975D, 0.875D, 0.975D);
    @Unique private static final AxisAlignedBB EAST_CHEST_AABB = new AxisAlignedBB(0.025D, 0.0D, 0.025D, 1.0D, 0.875D, 0.975D);
    @Unique private static final AxisAlignedBB NOT_CONNECTED_AABB = new AxisAlignedBB(0.025D, 0.0D, 0.025D, 0.975D, 0.875D, 0.975D);

    @Inject(method = "getBoundingBox", at = @At(value = "RETURN", ordinal = 0), cancellable = true)
    private void getBoundingBoxNorth(IBlockState state, IBlockAccess source, BlockPos pos, CallbackInfoReturnable<AxisAlignedBB> cir){
        cir.setReturnValue(NORTH_CHEST_AABB);
    }
    @Inject(method = "getBoundingBox", at = @At(value = "RETURN", ordinal = 1), cancellable = true)
    private void getBoundingBoxSouth(IBlockState state, IBlockAccess source, BlockPos pos, CallbackInfoReturnable<AxisAlignedBB> cir){
        cir.setReturnValue(SOUTH_CHEST_AABB);
    }
    @Inject(method = "getBoundingBox", at = @At(value = "RETURN", ordinal = 2), cancellable = true)
    private void getBoundingBoxWest(IBlockState state, IBlockAccess source, BlockPos pos, CallbackInfoReturnable<AxisAlignedBB> cir){
        cir.setReturnValue(WEST_CHEST_AABB);
    }
    @Inject(method = "getBoundingBox", at = @At(value = "RETURN", ordinal = 3), cancellable = true)
    private void getBoundingBoxEastAndNone(IBlockState state, IBlockAccess source, BlockPos pos, CallbackInfoReturnable<AxisAlignedBB> cir){
        cir.setReturnValue(source.getBlockState(pos.east()).getBlock() == this ? EAST_CHEST_AABB : NOT_CONNECTED_AABB);
    }
}
