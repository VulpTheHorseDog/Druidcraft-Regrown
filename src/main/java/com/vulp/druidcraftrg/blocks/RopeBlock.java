package com.vulp.druidcraftrg.blocks;

import com.vulp.druidcraftrg.blocks.tile.RopeTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

// TODO: Potential climb animation. Completely optional but a nice touch.
public class RopeBlock extends SixWayConnectBlock {

    public RopeBlock(double branchRadius, double centerRadius, Properties properties) {
        super(branchRadius, centerRadius, properties);
    }

    @Nullable
    @Override
    public BlockPathTypes getAiPathNodeType(BlockState state, BlockGetter world, BlockPos pos, @Nullable Mob entity) {
        return BlockPathTypes.BLOCKED;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return true;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RopeTileEntity(pos, state);
    }

    // Tile entity safety check.
    @Override
    public boolean triggerEvent(BlockState state, Level world, BlockPos pos, int a, int b) {
        super.triggerEvent(state, world, pos, a, b);
        BlockEntity tile = world.getBlockEntity(pos);
        return tile != null && tile.triggerEvent(a, b);
    }

    @Override
    public InteractionResult toggleIntersection(@Nullable Player playerEntity, Level world, BlockPos pos, UseOnContext context) {
        BlockEntity tile = world.getBlockEntity(pos);
        if (tile instanceof RopeTileEntity) {
            return ((RopeTileEntity) tile).forceToggle();
        }
        else return InteractionResult.FAIL;
    }

    @Override
    public boolean canBeKnifed() {
        return true;
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 60;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 30;
    }

    @Override
    public Connections connectInDirection(Direction direction, BlockState currentState, BlockPos currentPos, LevelAccessor world) {
        BlockState dirState = world.getBlockState(currentPos.relative(direction));
        Direction opposite = direction.getOpposite();
        if (currentState.getValue(DIR_TO_PROPERTY_MAP.get(direction)) == Connections.CUT) {
            return Connections.CUT;
        } else if ((dirState.getBlock() == this && dirState.getValue(DIR_TO_PROPERTY_MAP.get(opposite)) != Connections.CUT) || (dirState.isFaceSturdy(world, currentPos, opposite) || connectToLantern(dirState, direction) || isBeamBlock(dirState))) {
            return Connections.NORMAL;
        }
        return Connections.NONE;
    }

    public static boolean connectToLantern(BlockState state, Direction direction) {
        // TODO: Initialize and check list of lanterns that can be connected to.
        return false;
    }

    public static boolean isBeamBlock(BlockState state) {
        return state.getBlock() instanceof BeamBlock;
    }

    // Static check to be called for the entity itself.
    public static boolean isEntityInBlock(Entity entity) {
        AABB axisalignedbb = entity.getBoundingBox();
        BlockPos blockpos = new BlockPos(axisalignedbb.minX + 0.001D, axisalignedbb.minY + 0.001D, axisalignedbb.minZ + 0.001D);
        BlockPos blockpos1 = new BlockPos(axisalignedbb.maxX - 0.001D, axisalignedbb.maxY - 0.001D, axisalignedbb.maxZ - 0.001D);
        BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos();
        if (entity.level.hasChunksAt(blockpos, blockpos1)) {
            for (int i = blockpos.getX(); i <= blockpos1.getX(); ++i) {
                for (int j = blockpos.getY(); j <= blockpos1.getY(); ++j) {
                    for (int k = blockpos.getZ(); k <= blockpos1.getZ(); ++k) {
                        blockpos$mutable.set(i, j, k);
                        BlockState blockstate = entity.level.getBlockState(blockpos$mutable);
                        if (blockstate.getBlock() instanceof RopeBlock) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        if (entity instanceof Player) {
            Player player = (Player) entity;
            Vec3 movement = player.getDeltaMovement();
            player.fallDistance = 0.0F;
            double d0 = Mth.clamp(movement.x, (double)-0.15F, (double)0.15F);
            double d1 = Mth.clamp(movement.z, (double)-0.15F, (double)0.15F);
            double d2 = Math.max(movement.y, (double)-0.15F);
            double d3 = player.isOnGround() ? movement.x : 0.95F;
            double d4 = player.isOnGround() ? movement.z : 0.95F;
            if (player.jumping) {
                d2 = 0.3D;
            } else if (!player.isShiftKeyDown() && d2 < 0.0D) {
                d2 = 0.0D;
            }
            player.setDeltaMovement(new Vec3(player.isOnGround() ? movement.x : d0 * 0.95, d2, player.isOnGround() ? movement.z : d1 * 0.95));
        }
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

}
