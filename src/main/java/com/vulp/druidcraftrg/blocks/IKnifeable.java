package com.vulp.druidcraftrg.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

@FunctionalInterface
public interface IKnifeable {

    ActionResultType onKnifed(@Nullable PlayerEntity player, World world, BlockPos pos, BlockState state, ItemUseContext context);

}
