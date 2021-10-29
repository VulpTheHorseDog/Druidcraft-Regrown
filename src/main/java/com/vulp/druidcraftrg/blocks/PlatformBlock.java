package com.vulp.druidcraftrg.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class PlatformBlock extends Block implements IBucketPickupHandler, ILiquidContainer, IKnifeable {

    public static final BooleanProperty WALL = BooleanProperty.create("wall");
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final DirectionProperty FACING = DirectionProperty.create("facing", (dir) -> dir != Direction.UP && dir != Direction.DOWN);
    private static final Map<Direction, VoxelShape> SHAPES = createShapes();
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public PlatformBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(WALL, false)
                .setValue(FACING, Direction.NORTH)
                .setValue(OPEN, true)
                .setValue(WATERLOGGED, false));
    }

    @Override
    public ActionResultType onKnifed(@Nullable PlayerEntity player, World world, BlockPos pos, BlockState state, ItemUseContext context) {
        world.setBlock(pos, state.setValue(OPEN, !state.getValue(OPEN)), 2);
        return ActionResultType.SUCCESS;
    }

    public VoxelShape getCollisionShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext context) {
        if ((context.isAbove(VoxelShapes.block(), pos, true) && !context.isDescending()) || !state.getValue(OPEN)) {
            return getShape(state, reader, pos, context);
        } else {
            return VoxelShapes.empty();
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext context) {
        VoxelShape shape = SHAPES.get(null);
        if (state.getValue(WALL)) {
            shape = VoxelShapes.or(shape, SHAPES.get(state.getValue(FACING)));
        }
        return shape;
    }

    private static Map<Direction, VoxelShape> createShapes() {
        Map<Direction, VoxelShape> shapes = new HashMap<>();
        shapes.put(null, Block.box(0.0D, 12.0D, 0.0D, 16.0D, 16.0D, 16.0D));
        shapes.put(Direction.NORTH, Block.box(0.0D, 0.0D, 13.0D, 16.0D, 16.0D, 16.0D));
        shapes.put(Direction.EAST, Block.box(0.0D, 0.0D, 0.0D, 3.0D, 16.0D, 16.0D));
        shapes.put(Direction.SOUTH, Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 3.0D));
        shapes.put(Direction.WEST, Block.box(13.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D));
        return shapes;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public Fluid takeLiquid(IWorld world, BlockPos pos, BlockState state) {
        if (state.getValue(WATERLOGGED)) {
            world.setBlock(pos, state.setValue(WATERLOGGED, false), 3);
            return Fluids.WATER;
        } else {
            return Fluids.EMPTY;
        }
    }

    @Override
    public boolean canPlaceLiquid(IBlockReader reader, BlockPos pos, BlockState state, Fluid fluid) {
        return fluid == Fluids.WATER;
    }

    @Override
    public boolean placeLiquid(IWorld world, BlockPos pos, BlockState blockState, FluidState fluidState) {
        if (fluidState.getType() == Fluids.WATER) {
            if (!world.isClientSide()) {
                world.setBlock(pos, blockState.setValue(WATERLOGGED, true), 3);
                world.getLiquidTicks().scheduleTick(pos, fluidState.getType(), fluidState.getType().getTickDelay(world));
            }
            return true;
        }
        return false;
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockPos pos = context.getClickedPos();
        World world = context.getLevel();
        Direction face = context.getClickedFace();
        BlockState clickedBlock = world.getBlockState(pos.relative(face.getOpposite()));
        PlayerEntity player = context.getPlayer();
        boolean cardinal = face != Direction.UP && face != Direction.DOWN;
        return this.defaultBlockState().setValue(WALL, cardinal && clickedBlock.isFaceSturdy(world, pos, face)).setValue(OPEN, player == null || !player.isShiftKeyDown()).setValue(FACING, cardinal ? face : context.getHorizontalDirection().getOpposite()).setValue(WATERLOGGED, world.getFluidState(pos).getType() == Fluids.WATER);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {
        if (state.getValue(WATERLOGGED)) {
            world.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }
        Direction face = state.getValue(FACING);
        boolean wall = world.getBlockState(currentPos.relative(face.getOpposite())).isFaceSturdy(world, currentPos, face);
        return state.setValue(WALL, wall);
    }

    @Override
    public boolean propagatesSkylightDown(BlockState p_200123_1_, IBlockReader p_200123_2_, BlockPos p_200123_3_) {
        return false;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING, OPEN, WALL, WATERLOGGED);
    }

}
