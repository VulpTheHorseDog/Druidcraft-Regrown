package com.vulp.druidcraftrg.blocks;

import com.vulp.druidcraftrg.blocks.tile.RopeTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemUseContext;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;

// TODO: Potential climb animation. Completely optional but a nice touch.
public class RopeBlock extends SixWayConnectBlock {

    public RopeBlock(double branchRadius, double centerRadius, Properties properties) {
        super(branchRadius, centerRadius, properties);
    }

    @Nullable
    @Override
    public PathNodeType getAiPathNodeType(BlockState state, IBlockReader world, BlockPos pos, @Nullable MobEntity entity) {
        return PathNodeType.BLOCKED;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
        return true;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new RopeTileEntity();
    }

    // Tile entity safety check.
    @Override
    public boolean triggerEvent(BlockState state, World world, BlockPos pos, int a, int b) {
        super.triggerEvent(state, world, pos, a, b);
        TileEntity tile = world.getBlockEntity(pos);
        return tile != null && tile.triggerEvent(a, b);
    }

    @Override
    public ActionResultType toggleIntersection(@Nullable PlayerEntity playerEntity, World world, BlockPos pos, ItemUseContext context) {
        TileEntity tile = world.getBlockEntity(pos);
        if (tile instanceof RopeTileEntity) {
            return ((RopeTileEntity) tile).forceToggle();
        }
        else return ActionResultType.FAIL;
    }

    @Override
    public boolean canBeKnifed() {
        return true;
    }

    @Override
    public Connections connectInDirection(Direction direction, BlockState currentState, BlockPos currentPos, IWorld world) {
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
        AxisAlignedBB axisalignedbb = entity.getBoundingBox();
        BlockPos blockpos = new BlockPos(axisalignedbb.minX + 0.001D, axisalignedbb.minY + 0.001D, axisalignedbb.minZ + 0.001D);
        BlockPos blockpos1 = new BlockPos(axisalignedbb.maxX - 0.001D, axisalignedbb.maxY - 0.001D, axisalignedbb.maxZ - 0.001D);
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
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

    public void entityInside(BlockState state, World world, BlockPos pos, Entity entity) {
        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            Vector3d movement = player.getDeltaMovement();
            player.fallDistance = 0.0F;
            double d0 = MathHelper.clamp(movement.x, (double)-0.15F, (double)0.15F);
            double d1 = MathHelper.clamp(movement.z, (double)-0.15F, (double)0.15F);
            double d2 = Math.max(movement.y, (double)-0.15F);
            double d3 = player.isOnGround() ? movement.x : 0.95F;
            double d4 = player.isOnGround() ? movement.z : 0.95F;
            if (player.jumping) {
                d2 = 0.3D;
            } else if (!player.isShiftKeyDown() && d2 < 0.0D) {
                d2 = 0.0D;
            }
            player.setDeltaMovement(new Vector3d(player.isOnGround() ? movement.x : d0 * 0.95, d2, player.isOnGround() ? movement.z : d1 * 0.95));
        }
    }

}
