package com.vulp.druidcraftrg.events;

import com.vulp.druidcraftrg.DruidcraftRegrown;
import com.vulp.druidcraftrg.DruidcraftRegrownRegistry;
import com.vulp.druidcraftrg.capabilities.TempSpawnProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid= DruidcraftRegrown.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE)
public class MiscEvents {

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<?> event) {
        if (event.getObject() instanceof Player && !((Player) event.getObject()).getCapability(TempSpawnProvider.TEMP_SPAWN_CAPABILITY).isPresent()) {
            event.addCapability(DruidcraftRegrownRegistry.location("temprespawn"), new TempSpawnProvider());
        }
    }

}
