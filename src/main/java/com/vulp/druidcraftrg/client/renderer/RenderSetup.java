package com.vulp.druidcraftrg.client.renderer;

import com.vulp.druidcraftrg.init.BlockInit;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@OnlyIn(Dist.CLIENT)
public class RenderSetup {

    public static void setupRenderers(FMLClientSetupEvent event) {

        event.enqueueWork(() -> {
            RenderTypeLookup.setRenderLayer(BlockInit.hemp_crop, RenderType.cutout());
        });

    }

}
