package com.vulp.druidcraftrg;

import com.vulp.druidcraftrg.init.ItemInit;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.Arrays;

@Mod.EventBusSubscriber(modid = DruidcraftRegrown.MODID)
public class CommonSetup {

    public static void init(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            registerCompostables();
            registerAnimalFoods();
        });
    }

    private static void registerAnimalFoods() {
        // Chicken Foods:
        Chicken.FOOD_ITEMS = Ingredient.merge(Arrays.asList(Chicken.FOOD_ITEMS, Ingredient.of(ItemInit.hemp_seeds)));

    }

    private static void registerCompostables() {
        ComposterBlock.COMPOSTABLES.put(() -> ItemInit.hemp, 0.65F);
        ComposterBlock.COMPOSTABLES.put(() -> ItemInit.hemp_seeds, 0.3F);
    }

}
