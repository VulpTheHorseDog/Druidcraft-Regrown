package com.vulp.druidcraftrg.blocks;

import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.state.properties.BedPart;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Explosion;
import net.minecraft.world.ExplosionContext;
import net.minecraft.world.World;

public class BedrollBlock extends BedBlock {

    // NOTE: Game only takes note of the spawn blockpos for bed respawning and checks if there's a bed there. If bed is broken and placed again it works fine regardless of orientation.
    public BedrollBlock(DyeColor color, Properties properties) {
        super(color, properties);
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTrace) {
        if (world.isClientSide) {
            return ActionResultType.CONSUME;
        } else {
            if (state.getValue(PART) != BedPart.HEAD) {
                pos = pos.relative(state.getValue(FACING));
                state = world.getBlockState(pos);
                if (!state.is(this)) {
                    return ActionResultType.CONSUME;
                }
            }
            if (!canSetSpawn(world)) {
                world.removeBlock(pos, false);
                BlockPos blockpos = pos.relative(state.getValue(FACING).getOpposite());
                if (world.getBlockState(blockpos).is(this)) {
                    world.removeBlock(blockpos, false);
                }
                world.explode(null, DamageSource.badRespawnPointExplosion(), null, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, 5.0F, true, Explosion.Mode.DESTROY);
                return ActionResultType.SUCCESS;
            } else if (state.getValue(OCCUPIED)) {
                player.displayClientMessage(new TranslationTextComponent("block.minecraft.bed.occupied"), true);
                return ActionResultType.SUCCESS;
            } else {
                player.startSleepInBed(pos).ifLeft((p_220173_1_) -> {
                    if (p_220173_1_ != null) {
                        player.displayClientMessage(p_220173_1_.getMessage(), true);
                    }

                });
                return ActionResultType.SUCCESS;
            }
        }
    }

}
