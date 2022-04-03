package com.vulp.druidcraftrg.init;

import com.vulp.druidcraftrg.world.worldgen.feature.DoubleCropRandomPatchFeature;
import com.vulp.druidcraftrg.world.worldgen.feature.configuration.DoubleCropRandomPatchConfiguration;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.RarityFilter;

public class FeatureInit {

    public static final Feature<DoubleCropRandomPatchConfiguration> hemp = new DoubleCropRandomPatchFeature(DoubleCropRandomPatchConfiguration.CODEC);

    public static final Holder<ConfiguredFeature<DoubleCropRandomPatchConfiguration, ?>> hemp_config  = FeatureUtils.register("hemp_config", FeatureInit.hemp, new DoubleCropRandomPatchConfiguration(12, 3, 2, BlockStateProvider.simple(BlockInit.hemp_crop.defaultBlockState())));

    public static final Holder<PlacedFeature> hemp_holder = PlacementUtils.register("hemp_holder", hemp_config, RarityFilter.onAverageOnceEvery(24), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());

}
