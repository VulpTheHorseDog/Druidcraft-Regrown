package com.vulp.druidcraftrg;

import com.vulp.druidcraftrg.blocks.BeamBlock;
import com.vulp.druidcraftrg.blocks.DoubleCropBlock;
import com.vulp.druidcraftrg.blocks.PlatformBlock;
import com.vulp.druidcraftrg.blocks.RopeBlock;
import com.vulp.druidcraftrg.blocks.tile.BeamTileEntity;
import com.vulp.druidcraftrg.blocks.tile.RopeTileEntity;
import com.vulp.druidcraftrg.client.renderer.SetupRenderers;
import com.vulp.druidcraftrg.init.BlockInit;
import com.vulp.druidcraftrg.init.ItemInit;
import com.vulp.druidcraftrg.init.TileInit;
import com.vulp.druidcraftrg.items.DoubleCropSeedItem;
import com.vulp.druidcraftrg.items.KnifeItem;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class DruidcraftRegrownRegistry {

    public static final ItemGroup DC_TAB = new ItemGroup("druidcraft") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ItemInit.hemp_seeds); // TEMPORARY
        }
    };

    // ITEM REGISTRATION!
    @SubscribeEvent
    public static void itemRegistryEvent(final RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                // ITEMS:
                ItemInit.knife = new KnifeItem(new Item.Properties().tab(DC_TAB)).setRegistryName(location("knife")),

                // BLOCKITEMS:
                ItemInit.hemp_seeds = new DoubleCropSeedItem(BlockInit.hemp_crop, new Item.Properties().tab(DC_TAB)).setRegistryName(location("hemp_seeds")),
                ItemInit.rope = new BlockItem(BlockInit.rope, new Item.Properties().tab(DC_TAB)).setRegistryName(location("rope")), // redo texture.
                ItemInit.platform = new BlockItem(BlockInit.platform, new Item.Properties().tab(DC_TAB)).setRegistryName(location("platform")),
                ItemInit.beam = new BlockItem(BlockInit.beam, new Item.Properties().tab(DC_TAB)).setRegistryName(location("beam"))
        );

        DruidcraftRegrown.LOGGER.info("Items Registered!");
    }


    /*
    *   Things to do before demo:
    *       - Omnidirectional chains that function just like the rope.
    *       - Beams and rope/chain connections.
    *       - Lantern connections.
    *       -
    *       -
    *       -
    * */




    // BLOCK REGISTRATION!
    @SubscribeEvent
    public static void blockRegistryEvent(final RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(
                BlockInit.hemp_crop = new DoubleCropBlock(7.0D, AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.CROP).noCollission().randomTicks().instabreak()).setRegistryName(location("hemp_crop")),
                BlockInit.rope = new RopeBlock(2.5D, 2.5D, AbstractBlock.Properties.of(Material.WOOL).sound(SoundType.WOOL).strength(0.4F)).setRegistryName(location("rope")),
                BlockInit.platform = new PlatformBlock(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(1.0F).noOcclusion()).setRegistryName(location("platform")),
                BlockInit.beam = new BeamBlock(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(1.0F)).setRegistryName(location("beam"))
        );

        DruidcraftRegrown.LOGGER.info("Blocks Registered!");
    }

    // TILE REGISTRATION!
    @SubscribeEvent
    public static void tileRegistryEvent(final RegistryEvent.Register<TileEntityType<?>> event) {
        event.getRegistry().registerAll(
                TileInit.rope = TileInit.register("rope", TileEntityType.Builder.of(RopeTileEntity::new, BlockInit.rope)),
                TileInit.beam = TileInit.register("beam", TileEntityType.Builder.of(BeamTileEntity::new, BlockInit.beam))
        );

        DruidcraftRegrown.LOGGER.info("Tile Entities Registered!");
    }

    // TEXTURE STITCHING!
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onTextureStitchEvent(TextureStitchEvent.Pre event) {
        SetupRenderers.textureStitching(event);
        DruidcraftRegrown.LOGGER.info("Textures Stitched!");
    }

    public static ResourceLocation location(String name) {
        return new ResourceLocation("druidcraftrg", name);
    }

}
