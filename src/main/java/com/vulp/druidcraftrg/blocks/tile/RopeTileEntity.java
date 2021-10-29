package com.vulp.druidcraftrg.blocks.tile;

import com.vulp.druidcraftrg.blocks.RopeBlock;
import com.vulp.druidcraftrg.init.TileInit;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;

public class RopeTileEntity extends TileEntity {

    private boolean forceKnot = false;

    public RopeTileEntity() {
        super(TileInit.rope);
    }

    public RopeTileEntity(TileEntityType<?> tileEntityType) {
        super(tileEntityType);
    }

    public boolean hasKnot() {
        BlockState state = this.getBlockState();
        return this.forceKnot || state.getBlock() instanceof RopeBlock && RopeBlock.hasIntersection(state);
    }

    public void forceKnot(boolean force) {
        this.forceKnot = force;
    }

    public ActionResultType forceToggle() {
        BlockState state = this.getBlockState();
        if (state.getBlock() instanceof RopeBlock) {
            if (!RopeBlock.hasIntersection(state)) {
                this.forceKnot = !this.forceKnot;
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.FAIL;
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        super.save(compound);
        compound.putBoolean("Knot", this.forceKnot);
        return compound;
    }

    @Override
    public void load(BlockState state, CompoundNBT compound) {
        super.load(state, compound);
        this.forceKnot = compound.getBoolean("Knot");
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

}
