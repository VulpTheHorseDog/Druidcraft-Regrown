package com.vulp.druidcraftrg.blocks;

import com.google.common.collect.Maps;
import com.vulp.druidcraftrg.DruidcraftRegrown;
import com.vulp.druidcraftrg.blocks.tile.BeamTileEntity;
import com.vulp.druidcraftrg.init.BlockInit;
import com.vulp.druidcraftrg.util.DirAxisEnum;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.entity.MobEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class BeamBlock extends Block implements IBucketPickupHandler, ILiquidContainer {

    public static final BooleanProperty X_AXIS = BooleanProperty.create("x_axis");
    public static final BooleanProperty Y_AXIS = BooleanProperty.create("y_axis");
    public static final BooleanProperty Z_AXIS = BooleanProperty.create("z_axis");
    public static final EnumProperty<Direction.Axis> DEFAULT_AXIS = EnumProperty.create("def_axis", Direction.Axis.class);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final Map<Direction.Axis, VoxelShape> SHAPES = createShapes();
    public static final Map<Direction.Axis, BooleanProperty> AXIS_TO_BOOLEAN_PROPERTY_MAP = Util.make(Maps.newEnumMap(Direction.Axis.class), (map) -> {
        map.put(Direction.Axis.X, X_AXIS);
        map.put(Direction.Axis.Y, Y_AXIS);
        map.put(Direction.Axis.Z, Z_AXIS);
    });

    public BeamBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(X_AXIS, true)
                .setValue(Y_AXIS, true)
                .setValue(Z_AXIS, true)
                .setValue(DEFAULT_AXIS, Direction.Axis.X)
                .setValue(WATERLOGGED, false));
    }

    @Nullable
    @Override
    public PathNodeType getAiPathNodeType(BlockState state, IBlockReader world, BlockPos pos, @Nullable MobEntity entity) {
        return PathNodeType.BLOCKED;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new BeamTileEntity();
    }

    // Tile entity safety check.
    @Override
    public boolean triggerEvent(BlockState state, World world, BlockPos pos, int a, int b) {
        super.triggerEvent(state, world, pos, a, b);
        TileEntity tile = world.getBlockEntity(pos);
        return tile != null && tile.triggerEvent(a, b);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext context) {
        VoxelShape shape = VoxelShapes.empty();
        for (Map.Entry<Direction.Axis, BooleanProperty> entry : AXIS_TO_BOOLEAN_PROPERTY_MAP.entrySet()) {
            if (state.getValue(entry.getValue())) {
                shape = VoxelShapes.or(shape, SHAPES.get(entry.getKey()));
            }
        }
        return shape;
    }

    private static Map<Direction.Axis, VoxelShape> createShapes() {
        Map<Direction.Axis, VoxelShape> shapes = new HashMap<>();
        shapes.put(Direction.Axis.X, Block.box(0.0D, 5.0D, 5.0D, 16.0D, 11.0D, 11.0D));
        shapes.put(Direction.Axis.Y, Block.box(5.0D, 0.0D, 5.0D, 11.0D, 16.0D, 11.0D));
        shapes.put(Direction.Axis.Z, Block.box(5.0D, 5.0D, 0.0D, 11.0D, 11.0D, 16.0D));
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

    private static List<Direction> getOpenSides(BlockState state) {
        List<Direction> list = new LinkedList<>(Arrays.asList(Direction.values()));
        DirAxisEnum.getFromAxis(state.getValue(DEFAULT_AXIS));
        for (Map.Entry<Direction.Axis, BooleanProperty> entry : AXIS_TO_BOOLEAN_PROPERTY_MAP.entrySet()) {
            if (state.getValue(entry.getValue())) {
                for (Direction dir : DirAxisEnum.getFromAxis(entry.getKey()).getDirections()) {
                    list.remove(dir);
                }
            }
        }
        return list;
    }

    @Override
    public void tick(BlockState state, ServerWorld world, BlockPos pos, Random rand) {
        super.tick(state, world, pos, rand);
        DruidcraftRegrown.LOGGER.debug("TICK!");
    }

    public boolean hasLashing(World world, BlockPos pos, BlockState state) {
        List<Direction> list = getOpenSides(state);
        if (list.size() < 4) return true;
        list.removeIf((dir) ->  {
            BlockState checkState = world.getBlockState(pos.relative(dir));
            return checkState.getBlock() == this && checkState.getValue(AXIS_TO_BOOLEAN_PROPERTY_MAP.get(dir.getAxis()));
        });
        return list.size() < 4;
    }

    public static List<Direction> getRopeDirections(World world, BlockPos pos, BlockState state) {
        List<Direction> list = getOpenSides(state);
        list.removeIf((dir) ->  {
            BlockState checkState = world.getBlockState(pos.relative(dir));
            return checkState.getBlock() != BlockInit.rope || checkState.getValue(RopeBlock.DIR_TO_PROPERTY_MAP.get(dir.getOpposite())) != Connections.NORMAL;
        });
        return list;
    }


    /*public static List<Direction> getRopeDirections(World world, BlockPos pos, BlockState state) {
        List<Direction> list = new ArrayList<>(Collections.emptyList());
        for (Map.Entry<Direction.Axis, BooleanProperty> entry : AXIS_TO_BOOLEAN_PROPERTY_MAP.entrySet()) {
            if (!state.getValue(entry.getValue())) {
                Direction[] dirBlacklist = DirAxisEnum.getFromAxis(entry.getKey()).getDirections();
                for (Direction dir : Direction.values()) {
                    if (Arrays.stream(dirBlacklist).anyMatch(direction -> direction == dir)) {
                        continue;
                    }
                    BlockState checkState = world.getBlockState(pos.relative(dir));
                    if ((checkState.getBlock() instanceof RopeBlock && checkState.getValue(RopeBlock.DIR_TO_PROPERTY_MAP.get(dir.getOpposite())) == Connections.NORMAL) || RopeBlock.connectToLantern(checkState, dir)) {
                        list.add(dir);
                    }
                }
            }
        }
        return list;
    }*/

    private Direction.Axis getDefaultAxis(BlockState state) {
        return state.getValue(DEFAULT_AXIS);
    }

    private boolean shouldConnectOnAxis(IWorld world, BlockPos pos, Direction.Axis axis) {
        DirAxisEnum axisEnum = DirAxisEnum.getFromAxis(axis);
        if (axisEnum != null) {
            for (Direction direction : axisEnum.getDirections()) {
                BlockState state = world.getBlockState(pos.relative(direction));
                if (state.getBlock() == this && state.getValue(AXIS_TO_BOOLEAN_PROPERTY_MAP.get(axis))) {
                    return true;
                }
            }
        }
        return false;
    }


    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockPos pos = context.getClickedPos();
        World world = context.getLevel();
        Direction.Axis facing = context.getClickedFace().getAxis();
        return this.defaultBlockState().setValue(X_AXIS, facing == Direction.Axis.X || this.shouldConnectOnAxis(world, pos, Direction.Axis.X)).setValue(Y_AXIS, facing == Direction.Axis.Y || this.shouldConnectOnAxis(world, pos, Direction.Axis.Y)).setValue(Z_AXIS, facing == Direction.Axis.Z || this.shouldConnectOnAxis(world, pos, Direction.Axis.Z)).setValue(DEFAULT_AXIS, facing).setValue(WATERLOGGED, world.getFluidState(pos).getType() == Fluids.WATER);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {
        if (state.getValue(WATERLOGGED)) {
            world.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }
        state = state.setValue(X_AXIS, this.getDefaultAxis(state) == Direction.Axis.X || this.shouldConnectOnAxis(world, currentPos, Direction.Axis.X)).setValue(Y_AXIS, this.getDefaultAxis(state) == Direction.Axis.Y || this.shouldConnectOnAxis(world, currentPos, Direction.Axis.Y)).setValue(Z_AXIS, this.getDefaultAxis(state) == Direction.Axis.Z || this.shouldConnectOnAxis(world, currentPos, Direction.Axis.Z));
        TileEntity tile = world.getBlockEntity(currentPos);
        if (tile instanceof BeamTileEntity) {
            ((BeamTileEntity) tile).update(state);
        }
        return state;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState p_200123_1_, IBlockReader p_200123_2_, BlockPos p_200123_3_) {
        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(X_AXIS, Y_AXIS, Z_AXIS, DEFAULT_AXIS, WATERLOGGED);
    }

}
