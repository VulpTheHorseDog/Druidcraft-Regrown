package com.vulp.druidcraftrg.blocks.tile;

import com.vulp.druidcraftrg.blocks.BeamBlock;
import com.vulp.druidcraftrg.init.TileInit;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BeamTileEntity extends TileEntity implements ITickableTileEntity {

    private Direction.Axis lashingAxis;
    private List<Direction> ropeDirections = Collections.emptyList();
    private boolean initTick = false;

    public BeamTileEntity() {
        super(TileInit.beam);
    }

    public BeamTileEntity(TileEntityType<?> tileEntityType) {
        super(tileEntityType);
    }

    // TODO: Work out how to do calculations after block placement.
    @Override
    public CompoundNBT save(CompoundNBT compound) {
        super.save(compound);
        if (this.lashingAxis != null) {
            compound.putInt("Axis", this.lashingAxis.ordinal());
        } else {
            compound.remove("Axis");
        }
        List<Integer> directions = new ArrayList<>(Collections.emptyList());
        this.ropeDirections.forEach(dir -> directions.add(dir.ordinal()));
        compound.putIntArray("Dir", directions);
        return compound;
    }

    @Override
    public void load(BlockState state, CompoundNBT compound) {
        super.load(state, compound);
        this.lashingAxis = compound.contains("Axis") ? Direction.Axis.values()[compound.getInt("Axis")] : null;
        int[] directions = compound.getIntArray("Dir");
        this.ropeDirections = new ArrayList<>(Collections.emptyList());
        for (int dir : directions) {
            this.ropeDirections.add(Direction.values()[dir]);
        }
    }

    @Override
    public void tick() {
        if (!this.initTick) {
            this.initTick = true;
            this.update(this.getBlockState());
        }
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT nbtTagCompound = new CompoundNBT();
        save(nbtTagCompound);
        return nbtTagCompound;
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        this.load(state, tag);
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
