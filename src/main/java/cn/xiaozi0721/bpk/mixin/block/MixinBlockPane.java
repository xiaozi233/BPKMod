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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
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

    @Inject(method = "addCollisionBoxToList", at = @At("HEAD"), cancellable = true)
    private void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity entityIn, boolean isActualState, CallbackInfo ci){
        if (!isActualState)
        {
            state = this.getActualState(state, worldIn, pos);
        }

        if(!((Boolean)state.getValue(NORTH)).booleanValue()&&!((Boolean)state.getValue(SOUTH)).booleanValue()&&!((Boolean)state.getValue(EAST)).booleanValue()&&!((Boolean)state.getValue(WEST)).booleanValue()) {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_BY_INDEX[0]);
        }

        if (((Boolean)state.getValue(NORTH)).booleanValue())
        {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_BY_INDEX[getBoundingBoxIndex(EnumFacing.NORTH)]);
//            System.out.println(getBoundingBoxIndex(EnumFacing.NORTH));
        }

        if (((Boolean)state.getValue(SOUTH)).booleanValue())
        {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_BY_INDEX[getBoundingBoxIndex(EnumFacing.SOUTH)]);
//            System.out.println(getBoundingBoxIndex(EnumFacing.SOUTH));
        }

        if (((Boolean)state.getValue(EAST)).booleanValue())
        {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_BY_INDEX[getBoundingBoxIndex(EnumFacing.EAST)]);
//            System.out.println(getBoundingBoxIndex(EnumFacing.EAST));
        }

        if (((Boolean)state.getValue(WEST)).booleanValue())
        {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_BY_INDEX[getBoundingBoxIndex(EnumFacing.WEST)]);
//            System.out.println(getBoundingBoxIndex(EnumFacing.WEST));
        }
        ci.cancel();
    }

}
