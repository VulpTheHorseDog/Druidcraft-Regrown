package com.vulp.druidcraftrg;

import com.vulp.druidcraftrg.blocks.DoubleCropBlock;
import com.vulp.druidcraftrg.init.BlockInit;
import com.vulp.druidcraftrg.init.ItemInit;
import com.vulp.druidcraftrg.items.DoubleCropSeedItem;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class DruidcraftRegrownRegistry {

    public static final ItemGroup DC_TAB = new ItemGroup("tomes") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ItemInit.hemp_seeds); // TEMPORARY
        }
    };

    @SubscribeEvent
    public static void itemRegistryEvent(final RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                ItemInit.hemp_seeds = new DoubleCropSeedItem(BlockInit.hemp_crop, new Item.Properties().tab(DC_TAB)).setRegistryName(location("hemp_seeds"))
        );

        DruidcraftRegrown.LOGGER.info("Items Registered!");
    }

    @SubscribeEvent
    public static void blockRegistryEvent(final RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(
            BlockInit.hemp_crop = new DoubleCropBlock(AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.CROP).noCollission().randomTicks().instabreak()).setRegistryName(location("hemp_crop"))
        );

        DruidcraftRegrown.LOGGER.info("Blocks Registered!");
    }

    public static ResourceLocation location(String name) {
        return new ResourceLocation(DruidcraftRegrown.MODID, name);
    }

}
