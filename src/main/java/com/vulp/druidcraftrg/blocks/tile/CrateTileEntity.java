package com.vulp.druidcraftrg.blocks.tile;

import com.vulp.druidcraftrg.blocks.CrateBlock;
import com.vulp.druidcraftrg.init.BlockEntityInit;
import com.vulp.druidcraftrg.inventory.MultiSidedInventory;
import com.vulp.druidcraftrg.inventory.container.CrateContainer;
import com.vulp.druidcraftrg.state.properties.CrateType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class CrateTileEntity extends RandomizableContainerBlockEntity {

    private NonNullList<ItemStack> items = NonNullList.withSize(27, ItemStack.EMPTY);
    protected int lastOpenCount = 0;
    protected int openCount;
    private int tickInterval;
    private LazyOptional<IItemHandlerModifiable> crateHandler;
    private List<BlockPos> crateArray;

    protected CrateTileEntity(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(blockEntityType, pos, state);
    }

    public CrateTileEntity(BlockPos pos, BlockState state) {
        super(BlockEntityInit.crate, pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, CrateTileEntity blockEntity) {
        int x = blockEntity.worldPosition.getX();
        int y = blockEntity.worldPosition.getY();
        int z = blockEntity.worldPosition.getZ();
        ++blockEntity.tickInterval;
        boolean wasOpen = blockEntity.lastOpenCount > 0;
        blockEntity.openCount = getOpenCount(blockEntity.level, blockEntity, blockEntity.tickInterval, x, y, z, blockEntity.openCount);
        if (!wasOpen && blockEntity.openCount > 0) {
            blockEntity.playSound(SoundEvents.CHEST_OPEN);
        } else if (wasOpen && blockEntity.openCount == 0) {
            blockEntity.playSound(SoundEvents.CHEST_CLOSE);
        }
        blockEntity.lastOpenCount = blockEntity.openCount;
    }

    public static int getOpenCount(Level world, RandomizableContainerBlockEntity tile, int tickInterval, int xPos, int yPos, int zPos, int openCount) {
        if (!world.isClientSide && openCount != 0 && (tickInterval + xPos + yPos + zPos) % 200 == 0) {
            openCount = getOpenCount(world, tile, xPos, yPos, zPos);
        }

        return openCount;
    }

    public static int getOpenCount(Level world, RandomizableContainerBlockEntity tile, int xPos, int yPos, int zPos) {
        int ticker = 0;
        float i = 5.0F;
        for(Player playerentity : world.getEntitiesOfClass(Player.class, new AABB((float)xPos - i, (float)yPos - i, (float)zPos - i, (float)(xPos + 1) + i, (float)(yPos + 1) + i, (float)(zPos + 1) + i))) {
            if (playerentity.containerMenu instanceof CrateContainer) {
                Container iinventory = ((CrateContainer)playerentity.containerMenu).getContainer();
                if (iinventory == tile || iinventory instanceof MultiSidedInventory && ((MultiSidedInventory)iinventory).contains(tile)) {
                    ++ticker;
                }
            }
        }
        return ticker;
    }

    private void playSound(SoundEvent soundEvent) {
        CrateType crateType = this.getBlockState().getValue(CrateBlock.TYPE);
        if (Arrays.stream(CrateBlock.CRATE_TYPE_START_POINTS).anyMatch(i -> i == crateType.ordinal())) {
            List<Direction> directions = CrateBlock.getAttachDirectionsFromType(crateType);
            int[] integers = new int[]{0, 0, 0};
            for (Direction dir : directions) {
                integers[dir.getAxis().ordinal()] = dir.getStepX() + dir.getStepY() + dir.getStepZ();
            }
            double d0 = (double)this.worldPosition.getX() + 0.5D + ((float)integers[0] / 2);
            double d1 = (double)this.worldPosition.getY() + 0.5D + ((float)integers[1] / 2);
            double d2 = (double)this.worldPosition.getZ() + 0.5D + ((float)integers[2] / 2);
            this.level.playSound(null, d0, d1, d2, soundEvent, SoundSource.BLOCKS, 0.5F, (this.level.random.nextFloat() * 0.1F + 0.9F) * (1.05F - 0.05F * (float) crateType.getCrateSize()));
        }
    }

    @Override
    public boolean triggerEvent(int p_145842_1_, int p_145842_2_) {
        if (p_145842_1_ == 1) {
            this.openCount = p_145842_2_;
            return true;
        } else {
            return super.triggerEvent(p_145842_1_, p_145842_2_);
        }
    }

    @Override
    public void startOpen(Player player) {
        if (!player.isSpectator()) {
            if (this.openCount < 0) {
                this.openCount = 0;
            }
            ++this.openCount;
            this.signalOpenCount();
        }
    }

    @Override
    public void stopOpen(Player player) {
        if (!player.isSpectator()) {
            --this.openCount;
            this.signalOpenCount();
        }
    }

    protected void signalOpenCount() {
        Block block = this.getBlockState().getBlock();
        if (block instanceof CrateBlock) {
            this.level.blockEvent(this.worldPosition, block, 1, this.openCount);
            this.level.updateNeighborsAt(this.worldPosition, block); // May need replaced with a method that updated all crates in a proper array.
        }

    }

    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (!this.tryLoadLootTable(nbt)) {
            ContainerHelper.loadAllItems(nbt, this.items);
        }

    }

    public void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        if (!this.trySaveLootTable(nbt)) {
            ContainerHelper.saveAllItems(nbt, this.items);
        }
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> p_199721_1_) {
        this.items = p_199721_1_;
    }

    public static int getOpenCount(BlockGetter reader, BlockPos pos) {
        BlockState blockstate = reader.getBlockState(pos);
        if (blockstate.hasBlockEntity()) {
            BlockEntity tileentity = reader.getBlockEntity(pos);
            if (tileentity instanceof CrateTileEntity) {
                return ((CrateTileEntity)tileentity).openCount;
            }
        }
        return 0;
    }

    // ???
    public static void swapContents(CrateTileEntity crateTile1, CrateTileEntity crateTile2) {
        NonNullList<ItemStack> nonnulllist = crateTile1.getItems();
        crateTile1.setItems(crateTile2.getItems());
        crateTile2.setItems(nonnulllist);
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerCounter, Inventory playerInventory) {
        return CrateContainer.singleCrate(containerCounter, playerInventory, this);
    }

    @Override
    public void clearContent() {
        super.clearContent();
        if (this.crateHandler != null) {
            LazyOptional<?> oldHandler = this.crateHandler;
            this.crateHandler = null;
            oldHandler.invalidate();
        }
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (!this.remove && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {


            if (this.level == null) {
                return LazyOptional.empty().cast();
            }

            List<BlockPos> cratePositions = CrateBlock.getCratePosList(this.level, this.worldPosition);
            if (this.crateArray == null) {
                this.crateArray = cratePositions;
            } else if (this.crateArray != cratePositions) {
                this.crateArray = cratePositions;
            }

            if (this.crateHandler == null)
                this.crateHandler = LazyOptional.of(this::createHandler);
            return this.crateHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Nonnull
    private net.minecraftforge.items.IItemHandlerModifiable createHandler() {
        BlockState state = this.getBlockState();
        if (!(state.getBlock() instanceof CrateBlock)) {
            return new InvWrapper(this);
        }
        CrateTileEntity[] tileList = new CrateTileEntity[crateArray.size()];
        if (this.level != null) {
            for (int i = 0; i < crateArray.size(); i++) {
                tileList[i] = (CrateTileEntity) this.level.getBlockEntity(crateArray.get(i));
            }
        }
        Container inv = new MultiSidedInventory(tileList); //ChestBlock.getContainer((ChestBlock) state.getBlock(), state, getLevel(), getBlockPos(), true);
        return new InvWrapper(inv);
    }

/*    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (!this.remove && cap == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (this.crateHandler == null)
                this.crateHandler = net.minecraftforge.common.util.LazyOptional.of(this::createHandler);
            return this.crateHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Nonnull
    private net.minecraftforge.items.IItemHandlerModifiable createHandler() {
        return new net.minecraftforge.items.wrapper.InvWrapper(this);
    }*/

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        if (crateHandler != null)
            crateHandler.invalidate();
    }

    public List<BlockPos> getCrateArray() {
        return this.crateArray;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.level != null && (!(this.level.getBlockState(this.worldPosition).getBlock() instanceof CrateBlock) || CrateBlock.isCrateConfigValid(this.level, CrateBlock.getCratePosList(this.level, this.worldPosition))) && super.stillValid(player);
    }

    @Override
    protected Component getDefaultName() {
        return new TranslatableComponent("container.druidcraftrg.crate");
    }

    @Override
    public int getContainerSize() {
        return 27;
    }

}
