package com.vulp.druidcraftrg.client.renderer;

import com.vulp.druidcraftrg.DruidcraftRegrown;
import com.vulp.druidcraftrg.client.gui.screen.inventory.CrateScreen;
import com.vulp.druidcraftrg.client.renderer.tile.BeamRopeRenderer;
import com.vulp.druidcraftrg.client.renderer.tile.RopeKnotRenderer;
import com.vulp.druidcraftrg.init.BlockEntityInit;
import com.vulp.druidcraftrg.init.BlockInit;
import com.vulp.druidcraftrg.init.ContainerInit;
import com.vulp.druidcraftrg.init.ModelLayerInit;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid=DruidcraftRegrown.MODID, value=Dist.CLIENT, bus=Mod.EventBusSubscriber.Bus.MOD)
public class SetupRenderers {

    public static void setup(FMLClientSetupEvent event) {

        // BLOCK RENDERING
        event.enqueueWork(() -> {
            ItemBlockRenderTypes.setRenderLayer(BlockInit.hemp_crop, RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(BlockInit.rope, RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(BlockInit.platform, RenderType.cutout());
        });

        // SCREENS
        MenuScreens.register(ContainerInit.CRATE_9x3, CrateScreen::new);
        MenuScreens.register(ContainerInit.CRATE_9x6, CrateScreen::new);
        MenuScreens.register(ContainerInit.CRATE_9x12, CrateScreen::new);
        MenuScreens.register(ContainerInit.CRATE_9x24, CrateScreen::new);

    }

    public static void textureStitching(TextureStitchEvent.Pre event) {
        if (event.getAtlas().location() == TextureAtlas.LOCATION_BLOCKS) {
            event.addSprite(RopeKnotRenderer.TEXTURE);
            event.addSprite(BeamRopeRenderer.TEXTURE);
        }
    }

    @SubscribeEvent
    public static void registerEntityRenders(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(BlockEntityInit.beam, BeamRopeRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityInit.rope, RopeKnotRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinition(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ModelLayerInit.BEAM_ROPE_LAYER, BeamRopeRenderer.BeamRopeModel::buildModel);
        event.registerLayerDefinition(ModelLayerInit.ROPE_KNOT_LAYER, RopeKnotRenderer.RopeKnotModel::buildModel);
    }

}