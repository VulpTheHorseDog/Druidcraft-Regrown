package com.vulp.druidcraftrg.items;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.BlockNamedItem;

public class DoubleCropSeedItem extends BlockNamedItem {

    public DoubleCropSeedItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    protected boolean canPlace(BlockItemUseContext context, BlockState state) {
        return super.canPlace(context, state) && context.getLevel().getBlockState(context.getClickedPos().below()).getBlock() != this.getBlock();
    }
}
