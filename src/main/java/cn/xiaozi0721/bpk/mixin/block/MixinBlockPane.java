package cn.xiaozi0721.bpk.mixin.block;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPane;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(BlockPane.class)
public abstract class MixinBlockPane extends Block{
    @Shadow @Final public static PropertyBool NORTH;
    @Shadow @Final public static PropertyBool EAST;
    @Shadow @Final public static PropertyBool SOUTH;
    @Shadow @Final public static PropertyBool WEST;
    @Shadow @Final @Mutable protected static AxisAlignedBB[] AABB_BY_INDEX = new AxisAlignedBB[]{
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
            new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)
    };

    public MixinBlockPane(Material materialIn) {
        super(materialIn);
    }

    @WrapWithCondition(method = "addCollisionBoxToList", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockPane;addCollisionBoxToList(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/AxisAlignedBB;Ljava/util/List;Lnet/minecraft/util/math/AxisAlignedBB;)V", ordinal = 0))
    private boolean onlyNotConnected(BlockPos blockPos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable AxisAlignedBB blockBox, @Local(argsOnly = true) IBlockState state){
        return !state.getValue(NORTH) && !state.getValue(SOUTH) && !state.getValue(EAST) && !state.getValue(WEST);
    }

    @ModifyReturnValue(method = "attachesTo", at = @At("RETURN"))
    private boolean canConnectToWall(boolean original, @Local BlockFaceShape blockFaceShape){
        return original || blockFaceShape == BlockFaceShape.MIDDLE_POLE_THICK || blockFaceShape == BlockFaceShape.CENTER_BIG;
    }
}
