package com.vulp.druidcraftrg.items;

import com.vulp.druidcraftrg.DruidcraftRegrown;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;

public class DebugWandItem extends Item {

    public DebugWandItem(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
        DruidcraftRegrown.LOGGER.debug(context.getLevel().getBlockEntity(context.getClickedPos()));
        return super.onItemUseFirst(stack, context);
    }
}
