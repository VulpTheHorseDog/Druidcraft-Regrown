package com.vulp.druidcraftrg.mixins;

import com.vulp.druidcraftrg.blocks.RopeBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.IFlyingAnimal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow public boolean jumping;

    @Shadow public abstract boolean isEffectiveAi();

    @Shadow public abstract Vector3d handleRelativeFrictionAndCalculateMovement(Vector3d p_233633_1_, float p_233633_2_);

    @Shadow public abstract void calculateEntityAnimation(LivingEntity p_233629_1_, boolean p_233629_2_);

    // TODO: Animation of player arms while on rope? Alternate one when player is hanging underneath and no animation when player is on top.

    @Inject(at = @At("HEAD"), method = "travel", cancellable = true)
    private void travel(Vector3d vec, CallbackInfo ci) {
        if (getThis() instanceof PlayerEntity) {
            if ((this.isEffectiveAi() || getThis().isControlledByLocalInstance()) && (RopeBlock.isEntityInBlock(getThis()) && !this.jumping && !getThis().isCrouching() && !getThis().isOnGround())) {
                float f3 = getThis().level.getBlockState(getThis().getBlockPosBelowThatAffectsMyMovement()).getSlipperiness(getThis().level, getThis().getBlockPosBelowThatAffectsMyMovement(), getThis());
                float f4 = getThis().onGround ? f3 * 0.91F : 0.91F;
                Vector3d vector3d5 = this.handleRelativeFrictionAndCalculateMovement(vec, f3);
                getThis().setDeltaMovement(vector3d5.x * (double) f4, 0.0D, vector3d5.z * (double) f4);
                this.calculateEntityAnimation(getThis(), getThis() instanceof IFlyingAnimal);
                ci.cancel();
            }
        }
    }

    // Allows us to reference the current instance of the class the mixin is injecting.
    private LivingEntity getThis() {
        return ((LivingEntity)(Object)this);
    }

}
