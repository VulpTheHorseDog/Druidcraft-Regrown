package com.vulp.druidcraftrg.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class PlatformBlock extends Block implements SimpleWaterloggedBlock, IKnifeable {

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
    public InteractionResult onKnifed(@Nullable Player player, Level world, BlockPos pos, BlockState state, UseOnContext context) {
        world.setBlock(pos, state.setValue(OPEN, !state.getValue(OPEN)), 2);
        return InteractionResult.SUCCESS;
    }

    public VoxelShape getCollisionShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
        if ((context.isAbove(Shapes.block(), pos, true) && !context.isDescending()) || !state.getValue(OPEN)) {
            return getShape(state, reader, pos, context);
        } else {
            return Shapes.empty();
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
        VoxelShape shape = SHAPES.get(null);
        if (state.getValue(WALL)) {
            shape = Shapes.or(shape, SHAPES.get(state.getValue(FACING)));
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
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Level world = context.getLevel();
        Direction face = context.getClickedFace();
        BlockState clickedBlock = world.getBlockState(pos.relative(face.getOpposite()));
        Player player = context.getPlayer();
        boolean cardinal = face != Direction.UP && face != Direction.DOWN;
        return this.defaultBlockState().setValue(WALL, cardinal && clickedBlock.isFaceSturdy(world, pos, face)).setValue(OPEN, player == null || !player.isShiftKeyDown()).setValue(FACING, cardinal ? face : context.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos currentPos, BlockPos facingPos) {
/*        if (state.getValue(WATERLOGGED)) {
            world.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }*/
        Direction face = state.getValue(FACING);
        boolean wall = world.getBlockState(currentPos.relative(face.getOpposite())).isFaceSturdy(world, currentPos, face);
        return state.setValue(WALL, wall);
    }

    @Override
    public boolean propagatesSkylightDown(BlockState p_200123_1_, BlockGetter p_200123_2_, BlockPos p_200123_3_) {
        return false;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, OPEN, WALL, WATERLOGGED);
    }

}
