package com.vulp.druidcraftrg.world.worldgen.feature;

import com.mojang.serialization.Codec;
import com.vulp.druidcraftrg.blocks.DoubleCropBlock;
import com.vulp.druidcraftrg.world.worldgen.feature.configuration.DoubleCropRandomPatchConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

import java.util.Random;

public class DoubleCropRandomPatchFeature extends Feature<DoubleCropRandomPatchConfiguration> {

    public DoubleCropRandomPatchFeature(Codec<DoubleCropRandomPatchConfiguration> codec) {
        super(codec);
    }

    public boolean place(FeaturePlaceContext<DoubleCropRandomPatchConfiguration> context) {
        DoubleCropRandomPatchConfiguration config = context.config();
        Random random = context.random();
        BlockPos blockpos = context.origin();
        WorldGenLevel worldgenlevel = context.level();
        int i = 0;
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        int j = config.xzSpread() + 1;
        int k = config.ySpread() + 1;

        for(int l = 0; l < config.tries(); ++l) {
            boolean doub = random.nextBoolean();
            blockpos$mutableblockpos.setWithOffset(blockpos, random.nextInt(j) - random.nextInt(j), random.nextInt(k) - random.nextInt(k), random.nextInt(j) - random.nextInt(j));
            if (this.isValidPosition(false, worldgenlevel, blockpos$mutableblockpos, random, config)) {
                this.setBlock(worldgenlevel, blockpos$mutableblockpos, config.cropState().getState(random, blockpos$mutableblockpos).setValue(DoubleCropBlock.AGE, 7));
                ++i;
                if (doub && this.isValidPosition(true, worldgenlevel, blockpos$mutableblockpos.above(), random, config)) {
                    this.setBlock(worldgenlevel, blockpos$mutableblockpos.above(), config.cropState().getState(random, blockpos$mutableblockpos).setValue(DoubleCropBlock.BOTTOM, false).setValue(DoubleCropBlock.AGE, 7));
                }
            }
        }

        return i > 0;
    }

    private boolean isValidPosition(boolean topBlock, WorldGenLevel worldgenlevel, BlockPos blockpos, Random random, DoubleCropRandomPatchConfiguration config) {
        BlockState belowState = worldgenlevel.getBlockState(blockpos.below());
        BlockState replacedState = worldgenlevel.getBlockState(blockpos);
        return (belowState.is(BlockTags.DIRT) || (topBlock && (belowState.getBlock() == config.cropState().getState(random, blockpos.below()).getBlock() && belowState.getValue(DoubleCropBlock.BOTTOM)))) && (replacedState.isAir() || replacedState.is(BlockTags.LEAVES));
    }

}