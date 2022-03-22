package com.vulp.druidcraftrg.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class MultiSidedInventory implements IInventory {

    private final IInventory[] containers;

    public MultiSidedInventory(IInventory... inventories) {
        if (inventories.length > 1) {
            for (int i = 0; i < inventories.length; i++) {
                if (inventories[i] == null) {
                    if (i != 0) {
                        inventories[i] = inventories[i - 1];
                    } else {
                        inventories[i] = inventories[i + 1];
                    }
                }
            }
        }
        this.containers = inventories;
    }

    public int getContainerSize() {
        int size = 0;
        for (IInventory container : containers) {
            size += container.getContainerSize();
        }
        return size;
    }

    public boolean isEmpty() {
        for (IInventory container : containers) {
            if (!container.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public boolean contains(IInventory inv) {
        for (IInventory container : containers) {
            if (container == inv) {
                return true;
            }
        }
        return false;
    }

    public ItemStack getItem(int slot) {
        int sizeTicker = 0;
        for (IInventory container : containers) {
            sizeTicker += container.getContainerSize();
            if (slot < sizeTicker) {
                return container.getItem(slot + container.getContainerSize() - sizeTicker);
            }
        }
        return ItemStack.EMPTY;
    }

    public ItemStack removeItem(int slot, int count) {
        int sizeTicker = 0;
        for (IInventory container : containers) {
            sizeTicker += container.getContainerSize();
            if (slot < sizeTicker) {
                return container.removeItem(slot + container.getContainerSize() - sizeTicker, count);
            }
        }
        return ItemStack.EMPTY;
    }

    public ItemStack removeItemNoUpdate(int slot) {
        int sizeTicker = 0;
        for (IInventory container : containers) {
            sizeTicker += container.getContainerSize();
            if (slot < sizeTicker) {
                return container.removeItemNoUpdate(slot + container.getContainerSize() - sizeTicker);
            }
        }
        return ItemStack.EMPTY;
    }

    public void setItem(int slot, ItemStack itemStack) {
        int sizeTicker = 0;
        for (IInventory container : containers) {
            sizeTicker += container.getContainerSize();
            if (slot < sizeTicker) {
                container.setItem(slot + container.getContainerSize() - sizeTicker, itemStack);
                return;
            }
        }
    }

    public int getMaxStackSize() {
        return this.containers[0].getMaxStackSize();
    }

    public void setChanged() {
        for (IInventory container : containers) {
            container.setChanged();
        }
    }

    public boolean stillValid(PlayerEntity player) {
        for (IInventory container : containers) {
            if (!container.stillValid(player)) {
                return false;
            }
        }
        return true;
    }

    public void startOpen(PlayerEntity player) {
        for (IInventory container : containers) {
            container.startOpen(player);
        }
    }

    public void stopOpen(PlayerEntity player) {
        for (IInventory container : containers) {
            container.stopOpen(player);
        }
    }

    public boolean canPlaceItem(int slot, ItemStack itemStack) {
        int sizeTicker = 0;
        for (IInventory container : containers) {
            sizeTicker += container.getContainerSize();
            if (slot < sizeTicker) {
                return container.canPlaceItem(slot + container.getContainerSize() - sizeTicker, itemStack);
            }
        }
        return false;
    }

    public void clearContent() {
        for (IInventory container : containers) {
            container.clearContent();
        }
    }
}
