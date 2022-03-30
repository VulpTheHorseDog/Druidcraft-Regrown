package com.vulp.druidcraftrg.blocks.tile;

import com.vulp.druidcraftrg.blocks.BeamBlock;
import com.vulp.druidcraftrg.init.BlockEntityInit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BeamTileEntity extends BlockEntity {

    private Direction.Axis lashingAxis;
    private List<Direction> ropeDirections = Collections.emptyList();
    private boolean initTick = false;

    public BeamTileEntity(BlockPos pos, BlockState state) {
        super(BlockEntityInit.beam, pos, state);
    }

    public BeamTileEntity(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(blockEntityType, pos, state);
    }

    // TODO: Work out how to do calculations after block placement.
    @Override
    public void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        if (this.lashingAxis != null) {
            compound.putInt("Axis", this.lashingAxis.ordinal());
        } else {
            compound.remove("Axis");
        }
        List<Integer> directions = new ArrayList<>(Collections.emptyList());
        this.ropeDirections.forEach(dir -> directions.add(dir.ordinal()));
        compound.putIntArray("Dir", directions);
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        this.lashingAxis = compound.contains("Axis") ? Direction.Axis.values()[compound.getInt("Axis")] : null;
        int[] directions = compound.getIntArray("Dir");
        this.ropeDirections = new ArrayList<>(Collections.emptyList());
        for (int dir : directions) {
            this.ropeDirections.add(Direction.values()[dir]);
        }
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, BeamTileEntity blockEntity) {
        if (!blockEntity.initTick) {
            blockEntity.initTick = true;
            blockEntity.update(state);
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag nbtTagCompound = new CompoundTag();
        saveAdditional(nbtTagCompound);
        return nbtTagCompound;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        this.load(tag);
        super.handleUpdateTag(tag);
    }

    @Nullable
    public Direction.Axis findAxis(BlockState state) {
        if (state.getBlock() instanceof BeamBlock && (((BeamBlock)state.getBlock()).hasLashing(this.level, this.getBlockPos(), state) || !this.ropeDirections.isEmpty())) {
            return state.getValue(BeamBlock.DEFAULT_AXIS);
        }
        return null;
    }

    public void update(BlockState state) {
        this.ropeDirections = BeamBlock.getRopeDirections(this.level, this.getBlockPos(), state);
        this.lashingAxis = findAxis(state);
    }


    public Direction.Axis getLashingAxis() {
        return this.lashingAxis;
    }

    public List<Direction> getRopeDirections() {
        return this.ropeDirections;
    }

}
