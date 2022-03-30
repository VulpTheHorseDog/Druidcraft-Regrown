package com.vulp.druidcraftrg.items;

import com.vulp.druidcraftrg.DruidcraftRegrown;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;

public class DebugWandItem extends Item {

    public DebugWandItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        DruidcraftRegrown.LOGGER.debug(context.getLevel().getBlockEntity(context.getClickedPos()));
        return super.onItemUseFirst(stack, context);
    }
}
