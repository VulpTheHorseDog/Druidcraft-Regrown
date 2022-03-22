package com.vulp.druidcraftrg.inventory.container;

import com.vulp.druidcraftrg.init.ContainerInit;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

public class CrateContainer extends Container {

    private final IInventory container;
    private final int containerRows;

    private CrateContainer(ContainerType<?> type, int id, PlayerInventory playerInventory, int rows) {
        this(type, id, playerInventory, new Inventory(9 * rows), rows);
    }

    @Nonnull
    public static CrateContainer singleCrate(int id, PlayerInventory playerInventory) {
        return new CrateContainer(ContainerInit.CRATE_9x3, id, playerInventory, 3);
    }

    @Nonnull
    public static CrateContainer doubleCrate(int id, PlayerInventory playerInventory) {
        return new CrateContainer(ContainerInit.CRATE_9x6, id, playerInventory, 6);
    }

    @Nonnull
    public static CrateContainer quadCrate(int id, PlayerInventory playerInventory) {
        return new CrateContainer(ContainerInit.CRATE_9x12, id, playerInventory, 12);
    }

    @Nonnull
    public static CrateContainer octoCrate(int id, PlayerInventory playerInventory) {
        return new CrateContainer(ContainerInit.CRATE_9x24, id, playerInventory, 24);
    }

    @Nonnull
    public static CrateContainer singleCrate(int id, PlayerInventory playerInventory, IInventory inventory) {
        return new CrateContainer(ContainerInit.CRATE_9x3, id, playerInventory, inventory, 3);
    }

    @Nonnull
    public static CrateContainer doubleCrate(int id, PlayerInventory playerInventory, IInventory inventory) {
        return new CrateContainer(ContainerInit.CRATE_9x6, id, playerInventory, inventory, 6);
    }

    @Nonnull
    public static CrateContainer quadCrate(int id, PlayerInventory playerInventory, IInventory inventory) {
        return new CrateContainer(ContainerInit.CRATE_9x12, id, playerInventory, inventory, 12);
    }

    @Nonnull
    public static CrateContainer octoCrate(int id, PlayerInventory playerInventory, IInventory inventory) {
        return new CrateContainer(ContainerInit.CRATE_9x24, id, playerInventory, inventory, 24);
    }

    public CrateContainer(ContainerType<?> type, int id, PlayerInventory playerInventory, IInventory inventory, int rows) {
        super(type, id);

        checkContainerSize(inventory, rows * 9);

        this.container = inventory;
        this.containerRows = rows;
        inventory.startOpen(playerInventory.player);

        for(int j = 0; j < this.containerRows; ++j) {
            for(int k = 0; k < 9; ++k) {
                this.addSlot(new Slot(inventory, k + j * 9, 8 + k * 18, 18 + j * 18 + 219));
            }
        }

        for(int l = 0; l < 3; ++l) {
            for(int j1 = 0; j1 < 9; ++j1) {
                this.addSlot(new Slot(playerInventory, j1 + l * 9 + 9, 8 + j1 * 18, 139 + l * 18));
            }
        }

        for(int i1 = 0; i1 < 9; ++i1) {
            this.addSlot(new Slot(playerInventory, i1, 8 + i1 * 18, 197));
        }

    }

    public void removed(PlayerEntity player) {
        super.removed(player);
        this.container.stopOpen(player);
    }

    public ItemStack quickMoveStack(PlayerEntity player, int slotID) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotID);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (slotID < this.containerRows * 9) {
                if (!this.moveItemStackTo(itemstack1, this.containerRows * 9, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, this.containerRows * 9, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    public IInventory getContainer() {
        return container;
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return this.container.stillValid(player);
    }

    @OnlyIn(Dist.CLIENT)
    public int getRowCount() {
        return this.containerRows;
    }

}
