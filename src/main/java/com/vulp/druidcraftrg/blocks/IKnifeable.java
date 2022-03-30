package com.vulp.druidcraftrg.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

@FunctionalInterface
public interface IKnifeable {

    InteractionResult onKnifed(@Nullable Player player, Level world, BlockPos pos, BlockState state, UseOnContext context);

}
