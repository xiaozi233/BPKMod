package cn.xiaozi0721.bpk.mixin.minecraft.accessor;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.UUID;

@Mixin(EntityLivingBase.class)
public interface EntityLivingBaseAccessor {
    @Accessor("SPRINTING_SPEED_BOOST_ID")
    static UUID getSpringSpeedBoostID() {
        throw new AssertionError();
    }

    @Accessor("SPRINTING_SPEED_BOOST")
    static AttributeModifier getSpringSpeedBoost() {
        throw new AssertionError();
    }
}
