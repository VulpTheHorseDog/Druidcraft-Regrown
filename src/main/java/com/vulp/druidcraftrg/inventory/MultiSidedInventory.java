package com.vulp.druidcraftrg.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class MultiSidedInventory implements Container {

    private final Container[] containers;

    public MultiSidedInventory(Container... inventories) {
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
        for (Container container : containers) {
            size += container.getContainerSize();
        }
        return size;
    }

    public boolean isEmpty() {
        for (Container container : containers) {
            if (!container.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public boolean contains(Container inv) {
        for (Container container : containers) {
            if (container == inv) {
                return true;
            }
        }
        return false;
    }

    public ItemStack getItem(int slot) {
        int sizeTicker = 0;
        for (Container container : containers) {
            sizeTicker += container.getContainerSize();
            if (slot < sizeTicker) {
                return container.getItem(slot + container.getContainerSize() - sizeTicker);
            }
        }
        return ItemStack.EMPTY;
    }

    public ItemStack removeItem(int slot, int count) {
        int sizeTicker = 0;
        for (Container container : containers) {
            sizeTicker += container.getContainerSize();
            if (slot < sizeTicker) {
                return container.removeItem(slot + container.getContainerSize() - sizeTicker, count);
            }
        }
        return ItemStack.EMPTY;
    }

    public ItemStack removeItemNoUpdate(int slot) {
        int sizeTicker = 0;
        for (Container container : containers) {
            sizeTicker += container.getContainerSize();
            if (slot < sizeTicker) {
                return container.removeItemNoUpdate(slot + container.getContainerSize() - sizeTicker);
            }
        }
        return ItemStack.EMPTY;
    }

    public void setItem(int slot, ItemStack itemStack) {
        int sizeTicker = 0;
        for (Container container : containers) {
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
        for (Container container : containers) {
            container.setChanged();
        }
    }

    public boolean stillValid(Player player) {
        for (Container container : containers) {
            if (!container.stillValid(player)) {
                return false;
            }
        }
        return true;
    }

    public void startOpen(Player player) {
        for (Container container : containers) {
            container.startOpen(player);
        }
    }

    public void stopOpen(Player player) {
        for (Container container : containers) {
            container.stopOpen(player);
        }
    }

    public boolean canPlaceItem(int slot, ItemStack itemStack) {
        int sizeTicker = 0;
        for (Container container : containers) {
            sizeTicker += container.getContainerSize();
            if (slot < sizeTicker) {
                return container.canPlaceItem(slot + container.getContainerSize() - sizeTicker, itemStack);
            }
        }
        return false;
    }

    public void clearContent() {
        for (Container container : containers) {
            container.clearContent();
        }
    }
}
