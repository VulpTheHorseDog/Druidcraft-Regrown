package com.vulp.druidcraftrg.mixins;

import com.vulp.druidcraftrg.blocks.RopeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow public boolean jumping;

    @Shadow public abstract boolean isEffectiveAi();

    @Shadow public abstract Vec3 handleRelativeFrictionAndCalculateMovement(Vec3 p_21075_, float p_21076_);

    @Shadow public abstract void calculateEntityAnimation(LivingEntity p_21044_, boolean p_21045_);

    // TODO: Animation of player arms while on rope? Alternate one when player is hanging underneath and no animation when player is on top.

    @Inject(at = @At("HEAD"), method = "travel", cancellable = true)
    private void travel(Vec3 vec, CallbackInfo ci) {
        if (getThis() instanceof Player) {
            if ((this.isEffectiveAi() || getThis().isControlledByLocalInstance()) && (RopeBlock.isEntityInBlock(getThis()) && !this.jumping && !getThis().isCrouching() && !getThis().isOnGround())) {
                float f3 = getThis().level.getBlockState(new BlockPos(getThis().getX(), getThis().getBoundingBox().minY - 0.5000001D, getThis().getZ())).getFriction(getThis().level, new BlockPos(getThis().getX(), getThis().getBoundingBox().minY - 0.5000001D, getThis().getZ()), getThis());
                float f4 = getThis().onGround ? f3 * 0.91F : 0.91F;
                Vec3 vector3d5 = this.handleRelativeFrictionAndCalculateMovement(vec, f3);
                getThis().setDeltaMovement(vector3d5.x * (double) f4, 0.0D, vector3d5.z * (double) f4);
                this.calculateEntityAnimation(getThis(), getThis() instanceof FlyingAnimal);
                ci.cancel();
            }
        }
    }

    // Allows us to reference the current instance of the class the mixin is injecting.
    private LivingEntity getThis() {
        return ((LivingEntity)(Object)this);
    }

}
