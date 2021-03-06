package com.vulp.druidcraftrg.blocks;

import com.google.common.collect.Maps;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class SixWayConnectBlock extends BaseEntityBlock implements IKnifeable, SimpleWaterloggedBlock {

    public static final EnumProperty<Connections> NORTH = EnumProperty.create("north", Connections.class);
    public static final EnumProperty<Connections> EAST = EnumProperty.create("east", Connections.class);
    public static final EnumProperty<Connections> SOUTH = EnumProperty.create("south", Connections.class);
    public static final EnumProperty<Connections> WEST = EnumProperty.create("west", Connections.class);
    public static final EnumProperty<Connections> UP = EnumProperty.create("up", Connections.class);
    public static final EnumProperty<Connections> DOWN = EnumProperty.create("down", Connections.class);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private final Map<Direction, VoxelShape> SHAPE_ARRAY;
    public static final Map<Direction, EnumProperty<Connections>> DIR_TO_PROPERTY_MAP = Util.make(Maps.newEnumMap(Direction.class), (map) -> {
        map.put(Direction.NORTH, NORTH);
        map.put(Direction.EAST, EAST);
        map.put(Direction.SOUTH, SOUTH);
        map.put(Direction.WEST, WEST);
        map.put(Direction.UP, UP);
        map.put(Direction.DOWN, DOWN);
    });

    public SixWayConnectBlock(double branchRadius, double centerRadius, Properties properties) {
        super(properties);
        this.SHAPE_ARRAY = createShapes(branchRadius, centerRadius);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(NORTH, Connections.NONE)
                .setValue(EAST, Connections.NONE)
                .setValue(SOUTH, Connections.NONE)
                .setValue(WEST, Connections.NONE)
                .setValue(UP, Connections.NONE)
                .setValue(DOWN, Connections.NONE)
                .setValue(WATERLOGGED, false));
    }

    public abstract InteractionResult toggleIntersection(@Nullable Player playerEntity, Level world, BlockPos pos, UseOnContext context);

    public abstract boolean canBeKnifed();

    @Override
    public InteractionResult onKnifed(@Nullable Player player, Level world, BlockPos pos, BlockState state, UseOnContext context) {
        if (this.canBeKnifed()) {
            if (player != null && player.isShiftKeyDown()) {
                return this.toggleIntersection(player, world, pos, context);
            }
            Vec3 vecRelative = context.getClickLocation().subtract(pos.getX(), pos.getY(), pos.getZ());
            for (Map.Entry<Direction, VoxelShape> entry : SHAPE_ARRAY.entrySet()) {
                if (collision(vecRelative, entry.getValue())) {
                    if (entry.getKey() == null) {
                        Direction dir = context.getClickedFace();
                        EnumProperty<Connections> property = DIR_TO_PROPERTY_MAP.get(dir);
                        boolean flag = false;
                        if (state.getValue(property) == Connections.CUT) {
                            Connections connection = this.connectInDirection(dir, state.setValue(property, Connections.NONE), pos, world);
                            if (connection != Connections.NONE) {
                                world.setBlock(pos, state.setValue(property, connection), 2);
                                return InteractionResult.SUCCESS;
                            } else {
                                flag = true;
                            }
                        }
                        if (state.getValue(property) != Connections.CUT || flag) {
                            BlockState oppositeState = world.getBlockState(pos.relative(dir));
                            EnumProperty<Connections> oppositeProperty = DIR_TO_PROPERTY_MAP.get(dir.getOpposite());
                            if (oppositeState.getBlock() == this && oppositeState.getValue(oppositeProperty) == Connections.CUT) {
                                world.setBlock(pos, state.setValue(property, Connections.NORMAL), 2);
                                world.setBlock(pos.relative(dir), oppositeState.setValue(oppositeProperty, Connections.NORMAL), 2);
                                return InteractionResult.SUCCESS;
                            }
                        }
                        return InteractionResult.FAIL;
                    } else {
                        world.setBlock(pos, state.setValue(DIR_TO_PROPERTY_MAP.get(entry.getKey()), Connections.CUT), 2);
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }
        return InteractionResult.FAIL;
    }

    public static boolean collision(Vec3 vec, VoxelShape shape) {
        return shape.bounds().minX <= vec.x && shape.bounds().minY <= vec.y && shape.bounds().minZ <= vec.z && shape.bounds().maxX >= vec.x && shape.bounds().maxY >= vec.y && shape.bounds().maxZ >= vec.z;
    }

    public static boolean hasIntersection(BlockState state) {
        AtomicInteger i = new AtomicInteger();
        DIR_TO_PROPERTY_MAP.forEach((direction, connection) -> {
            if (i.get() < 3 && state.getValue(connection) == Connections.NORMAL) {
                i.getAndIncrement();
            }
        });
        return i.get() > 2;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Level world = context.getLevel();
        BlockState state = this.defaultBlockState();
        for (Map.Entry<Direction, EnumProperty<Connections>> entry : DIR_TO_PROPERTY_MAP.entrySet()) {
            state = state.setValue(entry.getValue(), connectInDirection(entry.getKey(), state, pos, world));
        }
        return state.setValue(WATERLOGGED, world.getFluidState(pos).getType() == Fluids.WATER);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos currentPos, BlockPos facingPos) {
        if (state.getValue(WATERLOGGED)) {
            world.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }
        for (Map.Entry<Direction, EnumProperty<Connections>> entry : DIR_TO_PROPERTY_MAP.entrySet()) {
            state = state.setValue(entry.getValue(), connectInDirection(entry.getKey(), state, currentPos, world));
        }
        return state;
    }

    public Connections connectInDirection(Direction direction, BlockState currentState, BlockPos currentPos, LevelAccessor world) {
        BlockState dirState = world.getBlockState(currentPos.relative(direction));
        Direction opposite = direction.getOpposite();
        if (currentState.getValue(DIR_TO_PROPERTY_MAP.get(direction)) == Connections.CUT) {
            return Connections.CUT;
        } else if ((dirState.getBlock() == this && dirState.getValue(DIR_TO_PROPERTY_MAP.get(opposite)) != Connections.CUT)) {
            return Connections.NORMAL;
        }
        return Connections.NONE;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return false;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN, WATERLOGGED);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    private Map<Direction, VoxelShape> createShapes(double branchRadius, double centerRadius) {
        centerRadius = Mth.clamp(centerRadius, 0.0D, 8.0D);
        branchRadius = Mth.clamp(branchRadius, 0.0D, 8.0D);
        double a = 8.0D - centerRadius;
        double b = 8.0D + centerRadius;
        double c = 8.0D - branchRadius;
        double d = 8.0D + branchRadius;
        Map<Direction, VoxelShape> map = new HashMap<>();
        map.put(null, Block.box(a, a, a, b, b, b));
        map.put(Direction.NORTH, Block.box(c, c, 0.0f, d, d, a));
        map.put(Direction.EAST, Block.box(d, a, c, 16.0f, d, d));
        map.put(Direction.SOUTH, Block.box(c, c, b, d, d, 16.0f));
        map.put(Direction.WEST, Block.box(0.0f, c, c, a, d, d));
        map.put(Direction.UP, Block.box(c, b, c, d, 16.0f, d));
        map.put(Direction.DOWN, Block.box(c, 0.0f, c, d, a, d));
        return map;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        VoxelShape shape = SHAPE_ARRAY.get(null);
        for (Map.Entry<Direction, EnumProperty<Connections>> entry : DIR_TO_PROPERTY_MAP.entrySet()) {
            if (state.getValue(entry.getValue()) == Connections.NORMAL) {
                shape = Shapes.or(shape, SHAPE_ARRAY.get(entry.getKey()));
            }
        }
        return shape;
    }

}
