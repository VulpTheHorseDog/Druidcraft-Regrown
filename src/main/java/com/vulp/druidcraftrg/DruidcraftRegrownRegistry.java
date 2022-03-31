package com.vulp.druidcraftrg;

import com.vulp.druidcraftrg.blocks.*;
import com.vulp.druidcraftrg.blocks.tile.BeamTileEntity;
import com.vulp.druidcraftrg.blocks.tile.CrateTileEntity;
import com.vulp.druidcraftrg.blocks.tile.RopeTileEntity;
import com.vulp.druidcraftrg.capabilities.TempSpawnCapability;
import com.vulp.druidcraftrg.client.renderer.SetupRenderers;
import com.vulp.druidcraftrg.init.BlockEntityInit;
import com.vulp.druidcraftrg.init.BlockInit;
import com.vulp.druidcraftrg.init.ContainerInit;
import com.vulp.druidcraftrg.init.ItemInit;
import com.vulp.druidcraftrg.items.DebugWandItem;
import com.vulp.druidcraftrg.items.DoubleCropSeedItem;
import com.vulp.druidcraftrg.items.KnifeItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
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

    public static final CreativeModeTab DC_TAB = new CreativeModeTab("druidcraft") {
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
                ItemInit.debug_wand = new DebugWandItem(new Item.Properties().tab(DC_TAB)).setRegistryName(location("debug_wand")),

                ItemInit.knife = new KnifeItem(new Item.Properties().tab(DC_TAB)).setRegistryName(location("knife")),

                // BLOCKITEMS:
                ItemInit.hemp_seeds = new DoubleCropSeedItem(BlockInit.hemp_crop, new Item.Properties().tab(DC_TAB)).setRegistryName(location("hemp_seeds")),
                ItemInit.rope = new BlockItem(BlockInit.rope, new Item.Properties().tab(DC_TAB)).setRegistryName(location("rope")), // redo texture.
                ItemInit.platform = new BlockItem(BlockInit.platform, new Item.Properties().tab(DC_TAB)).setRegistryName(location("platform")),
                ItemInit.beam = new BlockItem(BlockInit.beam, new Item.Properties().tab(DC_TAB)).setRegistryName(location("beam")),
                ItemInit.bedroll = new BedItem(BlockInit.bedroll, new Item.Properties().tab(DC_TAB)).setRegistryName(location("bedroll")),
                ItemInit.crate = new BlockItem(BlockInit.crate, new Item.Properties().tab(DC_TAB)).setRegistryName(location("crate"))
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
                BlockInit.hemp_crop = new DoubleCropBlock(7.0D, BlockBehaviour.Properties.of(Material.PLANT).sound(SoundType.CROP).noCollission().randomTicks().instabreak()).setRegistryName(location("hemp_crop")),
                BlockInit.rope = new RopeBlock(2.5D, 2.5D, BlockBehaviour.Properties.of(Material.WOOL).sound(SoundType.WOOL).strength(0.4F)).setRegistryName(location("rope")),
                BlockInit.platform = new PlatformBlock(BlockBehaviour.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(1.0F).noOcclusion()).setRegistryName(location("platform")),
                BlockInit.beam = new BeamBlock(BlockBehaviour.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(1.0F)).setRegistryName(location("beam")),
                BlockInit.bedroll = new BedrollBlock(DyeColor.RED, BlockBehaviour.Properties.of(Material.WOOL).sound(SoundType.WOOL).strength(1.0F)).setRegistryName(location("bedroll")),
                BlockInit.crate = new CrateBlock(BlockBehaviour.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(1.0F)).setRegistryName(location("crate"))
        );

        DruidcraftRegrown.LOGGER.info("Blocks Registered!");
    }

    // TILE REGISTRATION!
    @SubscribeEvent
    public static void tileRegistryEvent(final RegistryEvent.Register<BlockEntityType<?>> event) {
        event.getRegistry().registerAll(
                BlockEntityInit.rope = BlockEntityInit.register("rope", BlockEntityType.Builder.of(RopeTileEntity::new, BlockInit.rope)),
                BlockEntityInit.beam = BlockEntityInit.register("beam", BlockEntityType.Builder.of(BeamTileEntity::new, BlockInit.beam)),
                BlockEntityInit.crate = BlockEntityInit.register("crate", BlockEntityType.Builder.of(CrateTileEntity::new, BlockInit.crate))
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
