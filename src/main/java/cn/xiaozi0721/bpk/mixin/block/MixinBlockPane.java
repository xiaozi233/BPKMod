package cn.xiaozi0721.bpk.mixin.block;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPane;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.List;

@SuppressWarnings({"UnnecessaryUnboxing", "RedundantCast"})
@Mixin(BlockPane.class)
public abstract class MixinBlockPane extends Block{
    @Shadow @Final public static PropertyBool NORTH;
    @Shadow @Final public static PropertyBool EAST;
    @Shadow @Final public static PropertyBool SOUTH;
    @Shadow @Final public static PropertyBool WEST;
    @Shadow @Final @Mutable protected static AxisAlignedBB[] AABB_BY_INDEX;

    public MixinBlockPane(Material materialIn) {
        super(materialIn);
    }

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void setAABB(CallbackInfo ci){
        AABB_BY_INDEX = new AxisAlignedBB[]{
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
    }

    @WrapWithCondition(method = "addCollisionBoxToList", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockPane;addCollisionBoxToList(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/AxisAlignedBB;Ljava/util/List;Lnet/minecraft/util/math/AxisAlignedBB;)V", ordinal = 0))
    private boolean onlyDisconnected(BlockPos blockPos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable AxisAlignedBB blockBox, @Local(argsOnly = true) IBlockState state){
        return !((Boolean)state.getValue(NORTH)).booleanValue()
                && !((Boolean)state.getValue(SOUTH)).booleanValue()
                && !((Boolean)state.getValue(EAST)).booleanValue()
                && !((Boolean)state.getValue(WEST)).booleanValue();
    }
}
