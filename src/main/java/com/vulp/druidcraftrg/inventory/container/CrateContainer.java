package com.vulp.druidcraftrg.inventory.container;

import com.vulp.druidcraftrg.init.ContainerInit;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

public class CrateContainer extends AbstractContainerMenu {

    private final Container container;
    private final int containerRows;

    private CrateContainer(MenuType<?> type, int id, Inventory playerInventory, int rows) {
        this(type, id, playerInventory, new SimpleContainer(9 * rows), rows);
    }

    @Nonnull
    public static CrateContainer singleCrate(int id, Inventory playerInventory) {
        return new CrateContainer(ContainerInit.CRATE_9x3, id, playerInventory, 3);
    }

    @Nonnull
    public static CrateContainer doubleCrate(int id, Inventory playerInventory) {
        return new CrateContainer(ContainerInit.CRATE_9x6, id, playerInventory, 6);
    }

    @Nonnull
    public static CrateContainer quadCrate(int id, Inventory playerInventory) {
        return new CrateContainer(ContainerInit.CRATE_9x12, id, playerInventory, 12);
    }

    @Nonnull
    public static CrateContainer octoCrate(int id, Inventory playerInventory) {
        return new CrateContainer(ContainerInit.CRATE_9x24, id, playerInventory, 24);
    }

    @Nonnull
    public static CrateContainer singleCrate(int id, Inventory playerInventory, Container inventory) {
        return new CrateContainer(ContainerInit.CRATE_9x3, id, playerInventory, inventory, 3);
    }

    @Nonnull
    public static CrateContainer doubleCrate(int id, Inventory playerInventory, Container inventory) {
        return new CrateContainer(ContainerInit.CRATE_9x6, id, playerInventory, inventory, 6);
    }

    @Nonnull
    public static CrateContainer quadCrate(int id, Inventory playerInventory, Container inventory) {
        return new CrateContainer(ContainerInit.CRATE_9x12, id, playerInventory, inventory, 12);
    }

    @Nonnull
    public static CrateContainer octoCrate(int id, Inventory playerInventory, Container inventory) {
        return new CrateContainer(ContainerInit.CRATE_9x24, id, playerInventory, inventory, 24);
    }

    public CrateContainer(MenuType<?> type, int id, Inventory playerInventory, Container inventory, int rows) {
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

    public void removed(Player player) {
        super.removed(player);
        this.container.stopOpen(player);
    }

    public ItemStack quickMoveStack(Player player, int slotID) {
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

    public Container getContainer() {
        return container;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    @OnlyIn(Dist.CLIENT)
    public int getRowCount() {
        return this.containerRows;
    }

}
