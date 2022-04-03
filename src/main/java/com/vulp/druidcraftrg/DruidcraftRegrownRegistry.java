package com.vulp.druidcraftrg;

import com.vulp.druidcraftrg.blocks.*;
import com.vulp.druidcraftrg.blocks.tile.BeamTileEntity;
import com.vulp.druidcraftrg.blocks.tile.CrateTileEntity;
import com.vulp.druidcraftrg.blocks.tile.RopeTileEntity;
import com.vulp.druidcraftrg.capabilities.TempSpawnCapability;
import com.vulp.druidcraftrg.client.renderer.SetupRenderers;
import com.vulp.druidcraftrg.init.*;
import com.vulp.druidcraftrg.items.DoubleCropSeedItem;
import com.vulp.druidcraftrg.items.KnifeItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class DruidcraftRegrownRegistry {

    public static final CreativeModeTab DC_TAB = new CreativeModeTab("druidcraftrg") {
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
                // ItemInit.debug_wand = new DebugWandItem(new Item.Properties().tab(DC_TAB)).setRegistryName(location("debug_wand")),

                ItemInit.knife = new KnifeItem(new Item.Properties().tab(DC_TAB).stacksTo(1)).setRegistryName(location("knife")),
                ItemInit.hemp = new Item(new Item.Properties().tab(DC_TAB)).setRegistryName(location("hemp")),

                // BLOCKITEMS:
                ItemInit.hemp_seeds = new DoubleCropSeedItem(BlockInit.hemp_crop, new Item.Properties().tab(DC_TAB)).setRegistryName(location("hemp_seeds")),
                ItemInit.rope = new BlockItem(BlockInit.rope, new Item.Properties().tab(DC_TAB)).setRegistryName(location("rope")),
                ItemInit.platform = new BlockItem(BlockInit.spruce_platform, new Item.Properties().tab(DC_TAB)).setRegistryName(location("platform")),
                ItemInit.beam = new BlockItem(BlockInit.oak_beam, new Item.Properties().tab(DC_TAB)).setRegistryName(location("beam")),
                ItemInit.white_bedroll = new BedItem(BlockInit.white_bedroll, new Item.Properties().tab(DC_TAB).stacksTo(1)).setRegistryName(location("white_bedroll")),
                ItemInit.orange_bedroll = new BedItem(BlockInit.orange_bedroll, new Item.Properties().tab(DC_TAB).stacksTo(1)).setRegistryName(location("orange_bedroll")),
                ItemInit.magenta_bedroll = new BedItem(BlockInit.magenta_bedroll, new Item.Properties().tab(DC_TAB).stacksTo(1)).setRegistryName(location("magenta_bedroll")),
                ItemInit.light_blue_bedroll = new BedItem(BlockInit.light_blue_bedroll, new Item.Properties().tab(DC_TAB).stacksTo(1)).setRegistryName(location("light_blue_bedroll")),
                ItemInit.yellow_bedroll = new BedItem(BlockInit.yellow_bedroll, new Item.Properties().tab(DC_TAB).stacksTo(1)).setRegistryName(location("yellow_bedroll")),
                ItemInit.lime_bedroll = new BedItem(BlockInit.lime_bedroll, new Item.Properties().tab(DC_TAB).stacksTo(1)).setRegistryName(location("lime_bedroll")),
                ItemInit.pink_bedroll = new BedItem(BlockInit.pink_bedroll, new Item.Properties().tab(DC_TAB).stacksTo(1)).setRegistryName(location("pink_bedroll")),
                ItemInit.gray_bedroll = new BedItem(BlockInit.gray_bedroll, new Item.Properties().tab(DC_TAB).stacksTo(1)).setRegistryName(location("gray_bedroll")),
                ItemInit.light_gray_bedroll = new BedItem(BlockInit.light_gray_bedroll, new Item.Properties().tab(DC_TAB).stacksTo(1)).setRegistryName(location("light_gray_bedroll")),
                ItemInit.cyan_bedroll = new BedItem(BlockInit.cyan_bedroll, new Item.Properties().tab(DC_TAB).stacksTo(1)).setRegistryName(location("cyan_bedroll")),
                ItemInit.purple_bedroll = new BedItem(BlockInit.purple_bedroll, new Item.Properties().tab(DC_TAB).stacksTo(1)).setRegistryName(location("purple_bedroll")),
                ItemInit.blue_bedroll = new BedItem(BlockInit.blue_bedroll, new Item.Properties().tab(DC_TAB).stacksTo(1)).setRegistryName(location("blue_bedroll")),
                ItemInit.brown_bedroll = new BedItem(BlockInit.brown_bedroll, new Item.Properties().tab(DC_TAB).stacksTo(1)).setRegistryName(location("brown_bedroll")),
                ItemInit.green_bedroll = new BedItem(BlockInit.green_bedroll, new Item.Properties().tab(DC_TAB).stacksTo(1)).setRegistryName(location("green_bedroll")),
                ItemInit.red_bedroll = new BedItem(BlockInit.red_bedroll, new Item.Properties().tab(DC_TAB).stacksTo(1)).setRegistryName(location("red_bedroll")),
                ItemInit.black_bedroll = new BedItem(BlockInit.black_bedroll, new Item.Properties().tab(DC_TAB).stacksTo(1)).setRegistryName(location("black_bedroll")),

                ItemInit.crate = new BlockItem(BlockInit.oak_crate, new Item.Properties().tab(DC_TAB)).setRegistryName(location("crate"))

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
                BlockInit.hemp_crop = new DoubleCropBlock(7.0D, () -> ItemInit.hemp_seeds, BlockBehaviour.Properties.of(Material.PLANT).sound(SoundType.CROP).noCollission().randomTicks().instabreak()).setRegistryName(location("hemp_crop")),
                BlockInit.rope = new RopeBlock(2.5D, 2.5D, BlockBehaviour.Properties.of(Material.WOOL).sound(SoundType.WOOL).strength(0.4F)).setRegistryName(location("rope")),
                BlockInit.spruce_platform = new PlatformBlock(BlockBehaviour.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(1.0F).noOcclusion()).setRegistryName(location("platform")),
                BlockInit.oak_beam = new BeamBlock(BlockBehaviour.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(1.0F)).setRegistryName(location("beam")),
                BlockInit.white_bedroll = new BedrollBlock(DyeColor.WHITE, BlockBehaviour.Properties.of(Material.WOOL).sound(SoundType.WOOL).strength(1.0F)).setRegistryName(location("white_bedroll")),
                BlockInit.orange_bedroll = new BedrollBlock(DyeColor.ORANGE, BlockBehaviour.Properties.of(Material.WOOL).sound(SoundType.WOOL).strength(1.0F)).setRegistryName(location("orange_bedroll")),
                BlockInit.magenta_bedroll = new BedrollBlock(DyeColor.MAGENTA, BlockBehaviour.Properties.of(Material.WOOL).sound(SoundType.WOOL).strength(1.0F)).setRegistryName(location("magenta_bedroll")),
                BlockInit.light_blue_bedroll = new BedrollBlock(DyeColor.LIGHT_BLUE, BlockBehaviour.Properties.of(Material.WOOL).sound(SoundType.WOOL).strength(1.0F)).setRegistryName(location("light_blue_bedroll")),
                BlockInit.yellow_bedroll = new BedrollBlock(DyeColor.YELLOW, BlockBehaviour.Properties.of(Material.WOOL).sound(SoundType.WOOL).strength(1.0F)).setRegistryName(location("yellow_bedroll")),
                BlockInit.lime_bedroll = new BedrollBlock(DyeColor.LIME, BlockBehaviour.Properties.of(Material.WOOL).sound(SoundType.WOOL).strength(1.0F)).setRegistryName(location("lime_bedroll")),
                BlockInit.pink_bedroll = new BedrollBlock(DyeColor.PINK, BlockBehaviour.Properties.of(Material.WOOL).sound(SoundType.WOOL).strength(1.0F)).setRegistryName(location("pink_bedroll")),
                BlockInit.gray_bedroll = new BedrollBlock(DyeColor.GRAY, BlockBehaviour.Properties.of(Material.WOOL).sound(SoundType.WOOL).strength(1.0F)).setRegistryName(location("gray_bedroll")),
                BlockInit.light_gray_bedroll = new BedrollBlock(DyeColor.LIGHT_GRAY, BlockBehaviour.Properties.of(Material.WOOL).sound(SoundType.WOOL).strength(1.0F)).setRegistryName(location("light_gray_bedroll")),
                BlockInit.cyan_bedroll = new BedrollBlock(DyeColor.CYAN, BlockBehaviour.Properties.of(Material.WOOL).sound(SoundType.WOOL).strength(1.0F)).setRegistryName(location("cyan_bedroll")),
                BlockInit.purple_bedroll = new BedrollBlock(DyeColor.PURPLE, BlockBehaviour.Properties.of(Material.WOOL).sound(SoundType.WOOL).strength(1.0F)).setRegistryName(location("purple_bedroll")),
                BlockInit.blue_bedroll = new BedrollBlock(DyeColor.BLUE, BlockBehaviour.Properties.of(Material.WOOL).sound(SoundType.WOOL).strength(1.0F)).setRegistryName(location("blue_bedroll")),
                BlockInit.brown_bedroll = new BedrollBlock(DyeColor.BROWN, BlockBehaviour.Properties.of(Material.WOOL).sound(SoundType.WOOL).strength(1.0F)).setRegistryName(location("brown_bedroll")),
                BlockInit.green_bedroll = new BedrollBlock(DyeColor.GREEN, BlockBehaviour.Properties.of(Material.WOOL).sound(SoundType.WOOL).strength(1.0F)).setRegistryName(location("green_bedroll")),
                BlockInit.red_bedroll = new BedrollBlock(DyeColor.RED, BlockBehaviour.Properties.of(Material.WOOL).sound(SoundType.WOOL).strength(1.0F)).setRegistryName(location("red_bedroll")),
                BlockInit.black_bedroll = new BedrollBlock(DyeColor.BLACK, BlockBehaviour.Properties.of(Material.WOOL).sound(SoundType.WOOL).strength(1.0F)).setRegistryName(location("black_bedroll")),

                BlockInit.oak_crate = new CrateBlock(BlockBehaviour.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(1.0F)).setRegistryName(location("crate"))
        );

        DruidcraftRegrown.LOGGER.info("Blocks Registered!");
    }

    // TILE REGISTRATION!
    @SubscribeEvent
    public static void tileRegistryEvent(final RegistryEvent.Register<BlockEntityType<?>> event) {
        event.getRegistry().registerAll(
                BlockEntityInit.rope = BlockEntityInit.register("rope", BlockEntityType.Builder.of(RopeTileEntity::new, BlockInit.rope)),
                BlockEntityInit.beam = BlockEntityInit.register("beam", BlockEntityType.Builder.of(BeamTileEntity::new, BlockInit.oak_beam)),
                BlockEntityInit.crate = BlockEntityInit.register("crate", BlockEntityType.Builder.of(CrateTileEntity::new, BlockInit.oak_crate))
        );

        DruidcraftRegrown.LOGGER.info("Tile Entities Registered!");
    }

    // CONTAINER REGISTRATION!
    @SubscribeEvent
    public static void containerRegistryEvent(final RegistryEvent.Register<MenuType<?>> event) {
        event.getRegistry().registerAll(
                ContainerInit.CRATE_9x3.setRegistryName(DruidcraftRegrown.MODID, "crate_9x3"),
                ContainerInit.CRATE_9x6.setRegistryName(DruidcraftRegrown.MODID, "crate_9x6"),
                ContainerInit.CRATE_9x12.setRegistryName(DruidcraftRegrown.MODID, "crate_9x12"),
                ContainerInit.CRATE_9x24.setRegistryName(DruidcraftRegrown.MODID, "crate_9x24")
        );

        DruidcraftRegrown.LOGGER.info("Containers Registered!");
    }

    // FEATURE REGISTRATION!
    @SubscribeEvent
    public static void featureRegistryEvent(final RegistryEvent.Register<Feature<?>> event) {
        event.getRegistry().registerAll(

                FeatureInit.hemp.setRegistryName("hemp")


        );
        DruidcraftRegrown.LOGGER.info("Features Registered!");
    }

    // CAPABILITY REGISTRATION!
    @SubscribeEvent
    public static void capabilityRegistryEvent(RegisterCapabilitiesEvent event) {
        event.register(TempSpawnCapability.class);
        DruidcraftRegrown.LOGGER.info("Capabilities Registered!");
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
