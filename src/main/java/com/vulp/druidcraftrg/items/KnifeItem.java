package com.vulp.druidcraftrg.items;

import com.vulp.druidcraftrg.blocks.IKnifeable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class KnifeItem extends Item {

    public KnifeItem(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
        World world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        if (block instanceof IKnifeable) {
            return ((IKnifeable) block).onKnifed(context.getPlayer(), world, pos, state, context);
        }

        return super.onItemUseFirst(stack, context);
    }

}
