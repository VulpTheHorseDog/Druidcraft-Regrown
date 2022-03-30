package com.vulp.druidcraftrg.blocks;

import com.google.common.collect.Maps;
import com.vulp.druidcraftrg.blocks.tile.BeamTileEntity;
import com.vulp.druidcraftrg.init.BlockEntityInit;
import com.vulp.druidcraftrg.init.BlockInit;
import com.vulp.druidcraftrg.util.DirAxisEnum;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.*;

public class BeamBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {

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
    public BlockPathTypes getAiPathNodeType(BlockState state, BlockGetter world, BlockPos pos, @Nullable Mob entity) {
        return BlockPathTypes.BLOCKED;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BeamTileEntity(pos, state);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntity) {
        return createTickerHelper(blockEntity, BlockEntityInit.beam, BeamTileEntity::serverTick);
    }

    // Tile entity safety check.
    @Override
    public boolean triggerEvent(BlockState state, Level world, BlockPos pos, int a, int b) {
        super.triggerEvent(state, world, pos, a, b);
        BlockEntity tile = world.getBlockEntity(pos);
        return tile != null && tile.triggerEvent(a, b);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
        VoxelShape shape = Shapes.empty();
        for (Map.Entry<Direction.Axis, BooleanProperty> entry : AXIS_TO_BOOLEAN_PROPERTY_MAP.entrySet()) {
            if (state.getValue(entry.getValue())) {
                shape = Shapes.or(shape, SHAPES.get(entry.getKey()));
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

/*    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }*/


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

    public boolean hasLashing(Level world, BlockPos pos, BlockState state) {
        List<Direction> list = getOpenSides(state);
        if (list.size() < 4) return true;
        list.removeIf((dir) ->  {
            BlockState checkState = world.getBlockState(pos.relative(dir));
            return checkState.getBlock() == this && checkState.getValue(AXIS_TO_BOOLEAN_PROPERTY_MAP.get(dir.getAxis()));
        });
        return list.size() < 4;
    }

    public static List<Direction> getRopeDirections(Level world, BlockPos pos, BlockState state) {
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

    private boolean shouldConnectOnAxis(LevelAccessor world, BlockPos pos, Direction.Axis axis) {
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
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Level world = context.getLevel();
        Direction.Axis facing = context.getClickedFace().getAxis();
        return this.defaultBlockState().setValue(X_AXIS, facing == Direction.Axis.X || this.shouldConnectOnAxis(world, pos, Direction.Axis.X)).setValue(Y_AXIS, facing == Direction.Axis.Y || this.shouldConnectOnAxis(world, pos, Direction.Axis.Y)).setValue(Z_AXIS, facing == Direction.Axis.Z || this.shouldConnectOnAxis(world, pos, Direction.Axis.Z)).setValue(DEFAULT_AXIS, facing).setValue(WATERLOGGED, world.getFluidState(pos).getType() == Fluids.WATER);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos currentPos, BlockPos facingPos) {
        /*if (state.getValue(WATERLOGGED)) {
            world.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }*/
        state = state.setValue(X_AXIS, this.getDefaultAxis(state) == Direction.Axis.X || this.shouldConnectOnAxis(world, currentPos, Direction.Axis.X)).setValue(Y_AXIS, this.getDefaultAxis(state) == Direction.Axis.Y || this.shouldConnectOnAxis(world, currentPos, Direction.Axis.Y)).setValue(Z_AXIS, this.getDefaultAxis(state) == Direction.Axis.Z || this.shouldConnectOnAxis(world, currentPos, Direction.Axis.Z));
        BlockEntity tile = world.getBlockEntity(currentPos);
        if (tile instanceof BeamTileEntity) {
            ((BeamTileEntity) tile).update(state);
        }
        return state;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter world, BlockPos pos) {
        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(X_AXIS, Y_AXIS, Z_AXIS, DEFAULT_AXIS, WATERLOGGED);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}
