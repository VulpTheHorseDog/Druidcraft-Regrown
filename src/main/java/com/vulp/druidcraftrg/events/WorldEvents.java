package com.vulp.druidcraftrg.events;

import com.vulp.druidcraftrg.DruidcraftRegrown;
import com.vulp.druidcraftrg.init.FeatureInit;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid= DruidcraftRegrown.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE)
public class WorldEvents {

    @SubscribeEvent
    public static void onBiomeLoadingEvent(BiomeLoadingEvent event) {
        Biome.BiomeCategory category = event.getCategory();
        if (category.equals(Biome.BiomeCategory.FOREST) || category.equals(Biome.BiomeCategory.PLAINS)) {
            event.getGeneration().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, FeatureInit.hemp_holder);
        }
    }

}
