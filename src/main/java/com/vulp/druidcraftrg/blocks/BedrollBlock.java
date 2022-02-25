package com.vulp.druidcraftrg.blocks;

import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.state.properties.BedPart;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import java.util.concurrent.atomic.AtomicReference;

public class BedrollBlock extends BedBlock {

    protected static final VoxelShape[] HEAD_SHAPES = new VoxelShape[]{VoxelShapes.or(VoxelShapes.or(Block.box(1.0D, 0.0D, 0.0D, 15.0D, 4.0D, 2.0D), Block.box(0.0D, 0.0D, 2.0D, 16.0D, 5.0D, 9.0D)), VoxelShapes.or(Block.box(2.0D, 2.0D, 9.0D, 14.0D, 4.0D, 15.0D), Block.box(1.0D, 0.0D, 9.0D, 15.0D, 2.0D, 16.0D))),
            VoxelShapes.or(VoxelShapes.or(Block.box(14.0D, 0.0D, 1.0D, 16.0D, 4.0D, 15.0D), Block.box(7.0D, 0.0D, 0.0D, 14.0D, 5.0D, 16.0D)), VoxelShapes.or(Block.box(1.0D, 2.0D, 2.0D, 7.0D, 4.0D, 14.0D), Block.box(0.0D, 0.0D, 1.0D, 7.0D, 2.0D, 15.0D))),
            VoxelShapes.or(VoxelShapes.or(Block.box(1.0D, 0.0D, 14.0D, 15.0D, 4.0D, 16.0D), Block.box(0.0D, 0.0D, 7.0D, 16.0D, 5.0D, 14.0D)), VoxelShapes.or(Block.box(2.0D, 2.0D, 1.0D, 14.0D, 4.0D, 7.0D), Block.box(1.0D, 0.0D, 0.0D, 15.0D, 2.0D, 7.0D))),
            VoxelShapes.or(VoxelShapes.or(Block.box(0.0D, 0.0D, 1.0D, 2.0D, 4.0D, 15.0D), Block.box(2.0D, 0.0D, 0.0D, 9.0D, 5.0D, 16.0D)), VoxelShapes.or(Block.box(9.0D, 2.0D, 2.0D, 15.0D, 4.0D, 14.0D), Block.box(9.0D, 0.0D, 1.0D, 16.0D, 2.0D, 15.0D)))};

    protected static final VoxelShape[] FOOT_SHAPES = new VoxelShape[]{Block.box(1.0D, 0.0D, 0.0D, 15.0D, 4.0D, 16.0D),
            Block.box(0.0D, 0.0D, 1.0D, 16.0D, 4.0D, 15.0D),
            Block.box(1.0D, 0.0D, 0.0D, 15.0D, 4.0D, 16.0D),
            Block.box(0.0D, 0.0D, 1.0D, 16.0D, 4.0D, 15.0D)};

    protected static final VoxelShape[] HEAD_SHAPES_OPEN = new VoxelShape[]{VoxelShapes.or(VoxelShapes.or(Block.box(1.0D, 0.0D, 0.0D, 15.0D, 7.0D, 2.0D), Block.box(0.0D, 0.0D, 2.0D, 16.0D, 8.0D, 9.0D)), VoxelShapes.or(Block.box(2.0D, 2.0D, 9.0D, 14.0D, 4.0D, 15.0D), Block.box(1.0D, 0.0D, 9.0D, 15.0D, 2.0D, 16.0D))),
            VoxelShapes.or(VoxelShapes.or(Block.box(14.0D, 0.0D, 1.0D, 16.0D, 7.0D, 15.0D), Block.box(7.0D, 0.0D, 0.0D, 14.0D, 8.0D, 16.0D)), VoxelShapes.or(Block.box(1.0D, 2.0D, 2.0D, 7.0D, 4.0D, 14.0D), Block.box(0.0D, 0.0D, 1.0D, 7.0D, 2.0D, 15.0D))),
            VoxelShapes.or(VoxelShapes.or(Block.box(1.0D, 0.0D, 14.0D, 15.0D, 7.0D, 16.0D), Block.box(0.0D, 0.0D, 7.0D, 16.0D, 8.0D, 14.0D)), VoxelShapes.or(Block.box(2.0D, 2.0D, 1.0D, 14.0D, 4.0D, 7.0D), Block.box(1.0D, 0.0D, 0.0D, 15.0D, 2.0D, 7.0D))),
            VoxelShapes.or(VoxelShapes.or(Block.box(0.0D, 0.0D, 1.0D, 2.0D, 7.0D, 15.0D), Block.box(2.0D, 0.0D, 0.0D, 9.0D, 8.0D, 16.0D)), VoxelShapes.or(Block.box(9.0D, 2.0D, 2.0D, 15.0D, 4.0D, 14.0D), Block.box(9.0D, 0.0D, 1.0D, 16.0D, 2.0D, 15.0D)))};

    protected static final VoxelShape[] FOOT_SHAPES_OPEN = new VoxelShape[]{Block.box(1.0D, 0.0D, 0.0D, 15.0D, 7.0D, 16.0D),
            Block.box(0.0D, 0.0D, 1.0D, 16.0D, 7.0D, 15.0D),
            Block.box(1.0D, 0.0D, 0.0D, 15.0D, 7.0D, 16.0D),
            Block.box(0.0D, 0.0D, 1.0D, 16.0D, 7.0D, 15.0D)};

    public BedrollBlock(DyeColor color, Properties properties) {
        super(color, properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext context) {
        int i = state.getValue(FACING).get2DDataValue();
        return state.getValue(PART) == BedPart.HEAD ? (state.getValue(OCCUPIED) ? HEAD_SHAPES_OPEN[i] : HEAD_SHAPES[i]) : (state.getValue(OCCUPIED) ? FOOT_SHAPES_OPEN[i] : FOOT_SHAPES[i]);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext context) {
        int i = state.getValue(FACING).get2DDataValue();
        return context.getEntity() instanceof PlayerEntity && ((PlayerEntity) context.getEntity()).isSleeping() ? FOOT_SHAPES_OPEN[i] : FOOT_SHAPES[i];
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
                player.startSleepInBed(pos).ifLeft((sleepResult) -> {
                    if (sleepResult != null) {
                        player.displayClientMessage(sleepResult.getMessage(), true);
                    }

                });
                return ActionResultType.SUCCESS;
            }
        }
    }

    @Override
    public void updateEntityAfterFallOn(IBlockReader reader, Entity entity) {
        entity.setDeltaMovement(entity.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D));
    }

    @Override
    public void fallOn(World p_180658_1_, BlockPos p_180658_2_, Entity p_180658_3_, float p_180658_4_) {
        p_180658_3_.causeFallDamage(p_180658_4_, 0.5F);
    }

    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.MODEL;
    }

}
