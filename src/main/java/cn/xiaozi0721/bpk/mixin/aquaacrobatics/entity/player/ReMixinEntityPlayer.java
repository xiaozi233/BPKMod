package cn.xiaozi0721.bpk.mixin.aquaacrobatics.entity.player;

import com.fuzs.aquaacrobatics.entity.player.IPlayerResizeable;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityFlying;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidBlock;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.entity.MoverType;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import net.minecraft.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static cn.xiaozi0721.bpk.config.GeneralConfig.sprintDelay;

@Mixin(value = EntityPlayer.class, priority = 500)
public abstract class ReMixinEntityPlayer extends EntityLivingBase implements IPlayerResizeable {
    @Shadow protected float speedInAir;

    @Shadow public abstract void addMovementStat(double p_71000_1_, double p_71000_3_, double p_71000_5_);

    @Shadow public PlayerCapabilities capabilities;

    public ReMixinEntityPlayer(World worldIn) {
        super(worldIn);
    }

    @Inject(method = "travel", at = @At("HEAD"), cancellable = true)
    private void travel(float strafe, float vertical, float forward, CallbackInfo ci){
        double d0 = this.posX;
        double d1 = this.posY;
        double d2 = this.posZ;
        if (this.isSwimming() && !this.isRiding()) {

            double d3 = this.getLookVec().y;
            double d4 = d3 < -0.2 ? 0.085 : 0.06;
            IBlockState fluidState = this.world.getBlockState(new BlockPos(this.posX, this.posY + 1.0 - 0.1, this.posZ));
            if (d3 <= 0.0 || this.isJumping || fluidState.getBlock() instanceof BlockLiquid || fluidState.getBlock() instanceof IFluidBlock) {

                double d5 = this.motionY;
                this.motionY += (d3 - d5) * d4;
            }
        }

        double d3 = this.motionY;
        float f = this.jumpMovementFactor;
        if (this.capabilities.isFlying && !this.isRiding()) {

            this.jumpMovementFactor = this.capabilities.getFlySpeed() * (float) (this.isSprinting() ? 2 : 1);
        }

        // replaces a section in super method, therefore super is called otherwise
        if (!this.capabilities.isFlying && this.isInWater()) {

            if (this.isServerWorld() || this.canPassengerSteer()) {

                double d8 = this.posY;
                float f5 = this.isSprinting() ? 0.9F : this.getWaterSlowDown();
                float f6 = 0.02F;
                float f7 = (float) EnchantmentHelper.getDepthStriderModifier(this);
                if (f7 > 3.0F) {

                    f7 = 3.0F;
                }

                if (!this.onGround) {

                    f7 *= 0.5F;
                }

                if (f7 > 0.0F) {

                    f5 += (0.54600006F - f5) * f7 / 3.0F;
                    f6 += (this.getAIMoveSpeed() - f6) * f7 / 3.0F;
                }

                this.moveRelative(strafe, vertical, forward, f6);
                this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
                if (this.collidedHorizontally && this.isOnLadder()) {

                    this.motionY = 0.2;
                }

                this.motionX *= f5;
                this.motionY *= 0.8;
                this.motionZ *= f5;
                this.BPKMod$applyGravity();
                if (this.collidedHorizontally && this.isOffsetPositionInLiquid(this.motionX, this.motionY + 0.6 - this.posY + d8, this.motionZ)) {

                    this.motionY = 0.3;
                }

                this.BPKMod$updateLimbSwing();
            }
        } else {
            if (!sprintDelay){
                this.jumpMovementFactor = this.speedInAir;
                if (this.isSprinting())
                {
                    this.jumpMovementFactor = (float)((double)this.jumpMovementFactor + (double)this.speedInAir * 0.3D);
                }
            }
            super.travel(strafe, vertical, forward);
        }

        if (this.capabilities.isFlying && !this.isRiding()) {

            this.motionY = d3 * 0.6D;
            this.jumpMovementFactor = f;
            this.fallDistance = 0.0F;
            this.setFlag(7, false);
        }

        this.addMovementStat(this.posX - d0, this.posY - d1, this.posZ - d2);
        ci.cancel();
    }

    @Unique private void BPKMod$updateLimbSwing() {
        this.prevLimbSwingAmount = this.limbSwingAmount;
        double d5 = this.posX - this.prevPosX;
        double d7 = this.posZ - this.prevPosZ;
        double d9 = this instanceof EntityFlying ? this.posY - this.prevPosY : 0.0;
        float f10 = MathHelper.sqrt(d5 * d5 + d9 * d9 + d7 * d7) * 4.0F;

        if (f10 > 1.0F) {

            f10 = 1.0F;
        }

        this.limbSwingAmount += (f10 - this.limbSwingAmount) * 0.4F;
        this.limbSwing += this.limbSwingAmount;
    }

    @Unique private void BPKMod$applyGravity() {
        if (!this.hasNoGravity() && !this.isSprinting()) {

            if (this.motionY <= 0.0 && Math.abs(this.motionY - 0.005) >= 0.003 && Math.abs(this.motionY - 0.08 / 16.0) < 0.003) {

                this.motionY = -0.003;
            } else {

                this.motionY -= 0.08 / 16.0;
            }
        }
    }
}
