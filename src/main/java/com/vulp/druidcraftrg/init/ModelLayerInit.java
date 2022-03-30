package com.vulp.druidcraftrg.init;

import com.vulp.druidcraftrg.DruidcraftRegrown;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DruidcraftRegrown.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModelLayerInit {

    public static ModelLayerLocation BEAM_ROPE_LAYER = new ModelLayerLocation(new ResourceLocation(DruidcraftRegrown.MODID, "beam_rope"), "lashing");
    public static ModelLayerLocation ROPE_KNOT_LAYER = new ModelLayerLocation(new ResourceLocation(DruidcraftRegrown.MODID, "rope_knot"), "knot");

    public static void init() {

    }

}
