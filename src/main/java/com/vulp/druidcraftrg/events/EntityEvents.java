package com.vulp.druidcraftrg.events;

import com.vulp.druidcraftrg.DruidcraftRegrown;
import com.vulp.druidcraftrg.blocks.RopeBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid=DruidcraftRegrown.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE)
public class EntityEvents {

    @SubscribeEvent
    public static void onPlayerBreakBlock(PlayerEvent.BreakSpeed event) {
        PlayerEntity player = event.getPlayer();
        if (RopeBlock.isEntityInBlock(player) && !player.onGround) {
            event.setNewSpeed(event.getOriginalSpeed() * 5.0F);
        }
    }

}
