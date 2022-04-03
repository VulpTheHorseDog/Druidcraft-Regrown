package com.vulp.druidcraftrg.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.IPlantable;

import java.util.Random;

public class DoubleCropBlock extends CropBlock {

    public static final BooleanProperty BOTTOM = BlockStateProperties.BOTTOM;
    private final VoxelShape[] SHAPE_ARRAY;
    private final ItemLike SEED_ITEM;

    public DoubleCropBlock(double shapeRadius, ItemLike seedItem, Properties properties) {
        super(properties);
        shapeRadius = Mth.clamp(shapeRadius, 0.0D, 8.0D);
        double a = 8.0D - shapeRadius;
        double b = 8.0D + shapeRadius;
        SEED_ITEM = seedItem;
        this.SHAPE_ARRAY = new VoxelShape[]{
                Block.box(a, 0.0D, a, b, 2.0D, b),
                Block.box(a, 0.0D, a, b, 4.0D, b),
                Block.box(a, 0.0D, a, b, 6.0D, b),
                Block.box(a, 0.0D, a, b, 8.0D, b),
                Block.box(a, 0.0D, a, b, 10.0D, b),
                Block.box(a, 0.0D, a, b, 12.0D, b),
                Block.box(a, 0.0D, a, b, 14.0D, b),
                Block.box(a, 0.0D, a, b, 16.0D, b)};
        this.registerDefaultState(this.defaultBlockState().setValue(BOTTOM, true));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
        return SHAPE_ARRAY[state.getValue(this.getAgeProperty())];
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return SEED_ITEM;
    }

    private boolean isBottom(BlockState state) {
        return state.getValue(BOTTOM);
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, Random rand) {
        if (!this.isMaxAge(state) || this.canGrowUpward(world, state, pos)) {
            if (!world.isAreaLoaded(pos, 1)) return;
            if (world.getRawBrightness(pos, 0) >= 9) {
                float f = getGrowthSpeed(this, world, pos);
                if (net.minecraftforge.common.ForgeHooks.onCropsGrowPre(world, pos, state, rand.nextInt((int) (25.0F / f) + 1) == 0)) {
                    this.naturalGrowth(world, pos, state);
                    net.minecraftforge.common.ForgeHooks.onCropsGrowPost(world, pos, state);
                }
            }
        }

    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 60;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 30;
    }

    private boolean isAirAbove(BlockGetter reader, BlockPos pos) {
        return reader.getBlockState(pos.above()).getBlock() instanceof AirBlock;
    }

    private boolean isTopHalfAbove(BlockGetter reader, BlockPos pos) {
        BlockState state = reader.getBlockState(pos.above());
        return state.getBlock() == this && !state.getValue(BOTTOM);
    }

    @Override
    public void growCrops(Level world, BlockPos pos, BlockState state) {
        int bonemeal = this.getBonemealAgeIncrease(world);
        if (this.isMaxAge(state) && this.isBottom(state)) {
            if (isAirAbove(world, pos)) {
                world.setBlock(pos.above(), this.getStateForAge(0).setValue(BOTTOM, false), 2);
            } else if (isTopHalfAbove(world, pos)) {
                BlockState aboveState = world.getBlockState(pos.above());
                int aboveAge = aboveState.getValue(AGE);
                if (!this.isMaxAge(aboveState)) {
                    world.setBlock(pos.above(), aboveState.setValue(AGE, Mth.clamp(aboveAge + bonemeal, 0, this.getMaxAge())), 2);
                }
            }
        } else if (!this.isMaxAge(state)) {
            world.setBlock(pos, this.getStateForAge(Mth.clamp(this.getAge(state) + bonemeal, 0, this.getMaxAge())).setValue(BOTTOM, state.getValue(BOTTOM)), 2);
        }
    }

    public void naturalGrowth(Level world, BlockPos pos, BlockState state) {
        if (this.isMaxAge(state) && this.isBottom(state)) {
            if (isAirAbove(world, pos)) {
                world.setBlock(pos.above(), this.getStateForAge(0).setValue(BOTTOM, false), 2);
            }
        } else if (!this.isMaxAge(state)) {
            world.setBlock(pos, this.getStateForAge(this.getAge(state) + 1).setValue(BOTTOM, state.getValue(BOTTOM)), 2);
        }
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter reader, BlockPos pos) {
        return super.mayPlaceOn(state, reader, pos) || (state.getBlock() == this && this.isMaxAge(state)) || state.is(BlockTags.DIRT);
    }

    @Override
    public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, IPlantable plantable) {
        return super.canSustainPlant(state, world, pos, facing, plantable);
    }

    private boolean canGrowUpward(BlockGetter reader, BlockState state, BlockPos pos) {
        return state.getValue(BOTTOM) && isAirAbove(reader, pos);
    }

    @Override
    public boolean isValidBonemealTarget(BlockGetter reader, BlockPos pos, BlockState state, boolean isRemote) {
        BlockState aboveState = reader.getBlockState(pos.above());
        return !this.isMaxAge(state) || this.canGrowUpward(reader, state, pos) || aboveState.getBlock() == this && !isMaxAge(aboveState);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateContainer) {
        stateContainer.add(AGE, BOTTOM);
    }
}
