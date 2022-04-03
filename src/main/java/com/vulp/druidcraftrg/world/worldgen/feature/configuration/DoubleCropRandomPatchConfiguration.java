package com.vulp.druidcraftrg.world.worldgen.feature.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public record DoubleCropRandomPatchConfiguration(int tries, int xzSpread, int ySpread, BlockStateProvider cropState) implements FeatureConfiguration {
    public static final Codec<DoubleCropRandomPatchConfiguration> CODEC = RecordCodecBuilder.create((instance) ->
                    instance.group(ExtraCodecs.POSITIVE_INT.fieldOf("tries").orElse(128).forGetter(DoubleCropRandomPatchConfiguration::tries),
                            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("xz_spread").orElse(7).forGetter(DoubleCropRandomPatchConfiguration::xzSpread),
                            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("y_spread").orElse(3).forGetter(DoubleCropRandomPatchConfiguration::ySpread),
                            BlockStateProvider.CODEC.fieldOf("crop_state").forGetter(DoubleCropRandomPatchConfiguration::cropState)).apply(instance, DoubleCropRandomPatchConfiguration::new));
}