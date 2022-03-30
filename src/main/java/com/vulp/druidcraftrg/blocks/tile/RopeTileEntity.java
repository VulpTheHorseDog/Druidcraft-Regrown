package com.vulp.druidcraftrg.blocks.tile;

import com.vulp.druidcraftrg.blocks.RopeBlock;
import com.vulp.druidcraftrg.init.BlockEntityInit;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class RopeTileEntity extends BlockEntity {

    private boolean forceKnot = false;

    public RopeTileEntity(BlockEntityType<?> tileEntityType, BlockPos pos, BlockState state) {
        super(tileEntityType, pos, state);
    }

    public RopeTileEntity(BlockPos pos, BlockState state) {
        super(BlockEntityInit.rope, pos, state);
    }

    public boolean hasKnot() {
        BlockState state = this.getBlockState();
        return this.forceKnot || state.getBlock() instanceof RopeBlock && RopeBlock.hasIntersection(state);
    }

    public void forceKnot(boolean force) {
        this.forceKnot = force;
    }

    public InteractionResult forceToggle() {
        BlockState state = this.getBlockState();
        if (state.getBlock() instanceof RopeBlock) {
            if (!RopeBlock.hasIntersection(state)) {
                this.forceKnot = !this.forceKnot;
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.FAIL;
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        compound.putBoolean("Knot", this.forceKnot);
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        this.forceKnot = compound.getBoolean("Knot");
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
    }

}
