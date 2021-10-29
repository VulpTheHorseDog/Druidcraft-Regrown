package com.vulp.druidcraftrg.client.renderer;

import com.vulp.druidcraftrg.client.renderer.tile.BeamRopeRenderer;
import com.vulp.druidcraftrg.client.renderer.tile.RopeKnotRenderer;
import com.vulp.druidcraftrg.init.BlockInit;
import com.vulp.druidcraftrg.init.TileInit;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@OnlyIn(Dist.CLIENT)
public class SetupRenderers {

    public static void setup(FMLClientSetupEvent event) {

        ClientRegistry.bindTileEntityRenderer(TileInit.rope, RopeKnotRenderer::new);
        ClientRegistry.bindTileEntityRenderer(TileInit.beam, BeamRopeRenderer::new);

        event.enqueueWork(() -> {
            RenderTypeLookup.setRenderLayer(BlockInit.hemp_crop, RenderType.cutout());
            RenderTypeLookup.setRenderLayer(BlockInit.rope, RenderType.cutout());
            RenderTypeLookup.setRenderLayer(BlockInit.platform, RenderType.cutout());
        });

    }

    public static void textureStitching(TextureStitchEvent.Pre event) {
        if (event.getMap().location() == AtlasTexture.LOCATION_BLOCKS) {
            event.addSprite(RopeKnotRenderer.TEXTURE);
            event.addSprite(BeamRopeRenderer.TEXTURE);
        }
    }

}