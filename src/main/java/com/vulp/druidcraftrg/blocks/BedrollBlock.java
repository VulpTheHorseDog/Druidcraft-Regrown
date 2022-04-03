package com.vulp.druidcraftrg.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BedrollBlock extends BedBlock {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    protected static final VoxelShape[] HEAD_SHAPES = new VoxelShape[]{Shapes.or(Shapes.or(Block.box(1.0D, 0.0D, 0.0D, 15.0D, 4.0D, 2.0D), Block.box(0.0D, 0.0D, 2.0D, 16.0D, 5.0D, 9.0D)), Shapes.or(Block.box(2.0D, 2.0D, 9.0D, 14.0D, 4.0D, 15.0D), Block.box(1.0D, 0.0D, 9.0D, 15.0D, 2.0D, 16.0D))),
            Shapes.or(Shapes.or(Block.box(14.0D, 0.0D, 1.0D, 16.0D, 4.0D, 15.0D), Block.box(7.0D, 0.0D, 0.0D, 14.0D, 5.0D, 16.0D)), Shapes.or(Block.box(1.0D, 2.0D, 2.0D, 7.0D, 4.0D, 14.0D), Block.box(0.0D, 0.0D, 1.0D, 7.0D, 2.0D, 15.0D))),
            Shapes.or(Shapes.or(Block.box(1.0D, 0.0D, 14.0D, 15.0D, 4.0D, 16.0D), Block.box(0.0D, 0.0D, 7.0D, 16.0D, 5.0D, 14.0D)), Shapes.or(Block.box(2.0D, 2.0D, 1.0D, 14.0D, 4.0D, 7.0D), Block.box(1.0D, 0.0D, 0.0D, 15.0D, 2.0D, 7.0D))),
            Shapes.or(Shapes.or(Block.box(0.0D, 0.0D, 1.0D, 2.0D, 4.0D, 15.0D), Block.box(2.0D, 0.0D, 0.0D, 9.0D, 5.0D, 16.0D)), Shapes.or(Block.box(9.0D, 2.0D, 2.0D, 15.0D, 4.0D, 14.0D), Block.box(9.0D, 0.0D, 1.0D, 16.0D, 2.0D, 15.0D)))};

    protected static final VoxelShape[] FOOT_SHAPES = new VoxelShape[]{Block.box(1.0D, 0.0D, 0.0D, 15.0D, 4.0D, 16.0D),
            Block.box(0.0D, 0.0D, 1.0D, 16.0D, 4.0D, 15.0D),
            Block.box(1.0D, 0.0D, 0.0D, 15.0D, 4.0D, 16.0D),
            Block.box(0.0D, 0.0D, 1.0D, 16.0D, 4.0D, 15.0D)};

    protected static final VoxelShape[] HEAD_SHAPES_OPEN = new VoxelShape[]{Shapes.or(Shapes.or(Block.box(1.0D, 0.0D, 0.0D, 15.0D, 7.0D, 2.0D), Block.box(0.0D, 0.0D, 2.0D, 16.0D, 8.0D, 9.0D)), Shapes.or(Block.box(2.0D, 2.0D, 9.0D, 14.0D, 4.0D, 15.0D), Block.box(1.0D, 0.0D, 9.0D, 15.0D, 2.0D, 16.0D))),
            Shapes.or(Shapes.or(Block.box(14.0D, 0.0D, 1.0D, 16.0D, 7.0D, 15.0D), Block.box(7.0D, 0.0D, 0.0D, 14.0D, 8.0D, 16.0D)), Shapes.or(Block.box(1.0D, 2.0D, 2.0D, 7.0D, 4.0D, 14.0D), Block.box(0.0D, 0.0D, 1.0D, 7.0D, 2.0D, 15.0D))),
            Shapes.or(Shapes.or(Block.box(1.0D, 0.0D, 14.0D, 15.0D, 7.0D, 16.0D), Block.box(0.0D, 0.0D, 7.0D, 16.0D, 8.0D, 14.0D)), Shapes.or(Block.box(2.0D, 2.0D, 1.0D, 14.0D, 4.0D, 7.0D), Block.box(1.0D, 0.0D, 0.0D, 15.0D, 2.0D, 7.0D))),
            Shapes.or(Shapes.or(Block.box(0.0D, 0.0D, 1.0D, 2.0D, 7.0D, 15.0D), Block.box(2.0D, 0.0D, 0.0D, 9.0D, 8.0D, 16.0D)), Shapes.or(Block.box(9.0D, 2.0D, 2.0D, 15.0D, 4.0D, 14.0D), Block.box(9.0D, 0.0D, 1.0D, 16.0D, 2.0D, 15.0D)))};

    protected static final VoxelShape[] FOOT_SHAPES_OPEN = new VoxelShape[]{Block.box(1.0D, 0.0D, 0.0D, 15.0D, 7.0D, 16.0D),
            Block.box(0.0D, 0.0D, 1.0D, 16.0D, 7.0D, 15.0D),
            Block.box(1.0D, 0.0D, 0.0D, 15.0D, 7.0D, 16.0D),
            Block.box(0.0D, 0.0D, 1.0D, 16.0D, 7.0D, 15.0D)};

    public BedrollBlock(DyeColor color, Properties properties) {
        super(color, properties);
        this.registerDefaultState(this.defaultBlockState().setValue(WATERLOGGED, false));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
        int i = state.getValue(FACING).get2DDataValue();
        return state.getValue(PART) == BedPart.HEAD ? (state.getValue(OCCUPIED) ? HEAD_SHAPES_OPEN[i] : HEAD_SHAPES[i]) : (state.getValue(OCCUPIED) ? FOOT_SHAPES_OPEN[i] : FOOT_SHAPES[i]);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
        int i = state.getValue(FACING).get2DDataValue();
        Entity entity = ((EntityCollisionContext) context).getEntity();
        return entity instanceof Player && ((Player) entity).isSleeping() ? FOOT_SHAPES_OPEN[i] : FOOT_SHAPES[i];
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult rayTrace) {
        if (world.isClientSide) {
            return InteractionResult.CONSUME;
        } else {
            if (state.getValue(PART) != BedPart.HEAD) {
                pos = pos.relative(state.getValue(FACING));
                state = world.getBlockState(pos);
                if (!state.is(this)) {
                    return InteractionResult.CONSUME;
                }
            }
            if (!canSetSpawn(world)) {
                world.removeBlock(pos, false);
                BlockPos blockpos = pos.relative(state.getValue(FACING).getOpposite());
                if (world.getBlockState(blockpos).is(this)) {
                    world.removeBlock(blockpos, false);
                }
                world.explode(null, DamageSource.badRespawnPointExplosion(), null, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, 5.0F, true, Explosion.BlockInteraction.DESTROY);
                return InteractionResult.SUCCESS;
            } else if (state.getValue(OCCUPIED)) {
                player.displayClientMessage(new TextComponent("block.minecraft.bed.occupied"), true);
                return InteractionResult.SUCCESS;
            } else {
                player.startSleepInBed(pos).ifLeft((sleepResult) -> {
                    if (sleepResult != null) {
                        player.displayClientMessage(sleepResult.getMessage(), true);
                    }

                });
                return InteractionResult.SUCCESS;
            }
        }
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide && player.isCreative()) {
            BedPart bedpart = state.getValue(PART);
            if (bedpart == BedPart.FOOT) {
                BlockPos blockpos = pos.relative(state.getValue(FACING));
                BlockState blockstate = level.getBlockState(blockpos);
                if (blockstate.is(this) && blockstate.getValue(PART) == BedPart.HEAD) {
                    level.setBlock(blockpos, blockstate.getValue(WATERLOGGED) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState(), 35);
                    level.levelEvent(player, 2001, blockpos, Block.getId(blockstate));
                }
            }
        }

        super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 20;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 5;
    }

    @Override
    public void updateEntityAfterFallOn(BlockGetter reader, Entity entity) {
        entity.setDeltaMovement(entity.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state != null) {
            return state.setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
        } else return null;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState facingState, LevelAccessor accessor, BlockPos currentPos, BlockPos facingPos) {
        if (state.getValue(WATERLOGGED)) {
            accessor.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(accessor));
        }
        return super.updateShape(state, direction, facingState, accessor, currentPos, facingPos);
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float amount) {
        super.fallOn(level, state, pos, entity, amount * 0.5F);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WATERLOGGED);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

}
