package com.vulp.druidcraftrg.items;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class DoubleCropSeedItem extends ItemNameBlockItem {

    public DoubleCropSeedItem(Block block, Item.Properties properties) {
        super(block, properties);
    }

    @Override
    protected boolean canPlace(BlockPlaceContext context, BlockState state) {
        BlockState placedOn = context.getLevel().getBlockState(context.getClickedPos().below());
        return super.canPlace(context, state) && placedOn.getBlock() != this.getBlock() && !placedOn.is(BlockTags.DIRT);
    }
}
