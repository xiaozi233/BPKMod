package cn.xiaozi0721.bpk.mixin.block;

import cn.xiaozi0721.bpk.config.ConfigHandler.GeneralConfig;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.AxisAlignedBB;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockTrapDoor.class)
public abstract class MixinBlockTrapDoor extends Block {
    @Shadow @Final @Mutable protected static AxisAlignedBB EAST_OPEN_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.1825D, 1.0D, 1.0D);
    @Shadow @Final @Mutable protected static AxisAlignedBB WEST_OPEN_AABB = new AxisAlignedBB(0.8175D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
    @Shadow @Final @Mutable protected static AxisAlignedBB SOUTH_OPEN_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.1825D);
    @Shadow @Final @Mutable protected static AxisAlignedBB NORTH_OPEN_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.8175D, 1.0D, 1.0D, 1.0D);

    public MixinBlockTrapDoor(Material materialIn) {
        super(materialIn);
    }

    @ModifyReturnValue(method = "isLadder", at = @At(value = "RETURN", ordinal = 0), remap = false)
    private boolean isClimbable(boolean original){
        return original && GeneralConfig.climbableTrapdoor;
    }
}
