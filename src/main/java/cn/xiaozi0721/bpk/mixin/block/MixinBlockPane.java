package cn.xiaozi0721.bpk.mixin.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPane;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(BlockPane.class)
public abstract class MixinBlockPane extends Block {
    @Shadow @Final public static PropertyBool NORTH;
    @Shadow @Final public static PropertyBool SOUTH;
    @Shadow @Final public static PropertyBool EAST;
    @Shadow @Final public static PropertyBool WEST;

    @Unique
    private static final AxisAlignedBB[] AABB_BY_INDEX = new AxisAlignedBB[]{
            new AxisAlignedBB(0.4375, 0.0, 0.4375, 0.5625, 1.0, 0.5625),
            new AxisAlignedBB(0.4375, 0.0, 0.5, 0.5625, 1.0, 1.0),
            new AxisAlignedBB(0.0, 0.0, 0.4375, 0.5, 1.0, 0.5625),
            new AxisAlignedBB(0.0, 0.0, 0.4375, 0.5, 1.0, 1.0),
            new AxisAlignedBB(0.4375, 0.0, 0.0, 0.5625, 1.0, 0.5),
            new AxisAlignedBB(0.4375, 0.0, 0.0, 0.5625, 1.0, 1.0),
            new AxisAlignedBB(0.0, 0.0, 0.0, 0.5625, 1.0, 0.5625),
            new AxisAlignedBB(0.0, 0.0, 0.0, 0.5625, 1.0, 1.0),
            new AxisAlignedBB(0.5, 0.0, 0.4375, 1.0, 1.0, 0.5625),
            new AxisAlignedBB(0.4375, 0.0, 0.4375, 1.0, 1.0, 1.0),
            new AxisAlignedBB(0.0, 0.0, 0.4375, 1.0, 1.0, 0.5625),
            new AxisAlignedBB(0.0, 0.0, 0.4375, 1.0, 1.0, 1.0),
            new AxisAlignedBB(0.4375, 0.0, 0.0, 1.0, 1.0, 0.5625),
            new AxisAlignedBB(0.4375, 0.0, 0.0, 1.0, 1.0, 1.0),
            new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 0.5625),
            new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)};

    @Shadow private static int getBoundingBoxIndex(EnumFacing p_185729_0_) {
        return 0;
    }

    public MixinBlockPane(Material materialIn) {
        super(materialIn);
    }


    @Redirect(method = "addCollisionBoxToList", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockPane;addCollisionBoxToList(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/AxisAlignedBB;Ljava/util/List;Lnet/minecraft/util/math/AxisAlignedBB;)V", ordinal = 0))
    private void addCollisionBoxToListClear(BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable AxisAlignedBB blockBox){}

    @Inject(method = "addCollisionBoxToList", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockPane;addCollisionBoxToList(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/AxisAlignedBB;Ljava/util/List;Lnet/minecraft/util/math/AxisAlignedBB;)V", ordinal = 0, shift = At.Shift.AFTER))
    private void addCollisionBoxToListNone(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity entityIn, boolean isActualState, CallbackInfo ci){
        if(!((Boolean)state.getValue(NORTH)).booleanValue()&&!((Boolean)state.getValue(SOUTH)).booleanValue()&&!((Boolean)state.getValue(EAST)).booleanValue()&&!((Boolean)state.getValue(WEST)).booleanValue()) {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_BY_INDEX[0]);
        }
    }

    @Redirect(method = "addCollisionBoxToList", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockPane;addCollisionBoxToList(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/AxisAlignedBB;Ljava/util/List;Lnet/minecraft/util/math/AxisAlignedBB;)V", ordinal = 1))
    private void addCollisionBoxToListNorth(BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable AxisAlignedBB blockBox){
        addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_BY_INDEX[getBoundingBoxIndex(EnumFacing.NORTH)]);
    }

    @Redirect(method = "addCollisionBoxToList", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockPane;addCollisionBoxToList(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/AxisAlignedBB;Ljava/util/List;Lnet/minecraft/util/math/AxisAlignedBB;)V", ordinal = 2))
    private void addCollisionBoxToListSouth(BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable AxisAlignedBB blockBox){
        addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_BY_INDEX[getBoundingBoxIndex(EnumFacing.SOUTH)]);
    }

    @Redirect(method = "addCollisionBoxToList", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockPane;addCollisionBoxToList(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/AxisAlignedBB;Ljava/util/List;Lnet/minecraft/util/math/AxisAlignedBB;)V", ordinal = 3))
    private void addCollisionBoxToListEast(BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable AxisAlignedBB blockBox){
        addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_BY_INDEX[getBoundingBoxIndex(EnumFacing.EAST)]);
    }

    @Redirect(method = "addCollisionBoxToList", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockPane;addCollisionBoxToList(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/AxisAlignedBB;Ljava/util/List;Lnet/minecraft/util/math/AxisAlignedBB;)V", ordinal = 4))
    private void addCollisionBoxToListWest(BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable AxisAlignedBB blockBox){
        addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_BY_INDEX[getBoundingBoxIndex(EnumFacing.WEST)]);
    }
}
