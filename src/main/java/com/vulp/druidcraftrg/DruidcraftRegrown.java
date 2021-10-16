package com.vulp.druidcraftrg;

import com.vulp.druidcraftrg.client.renderer.RenderSetup;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("druidcraftrg")
public class DruidcraftRegrown {

    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "druidcraft_regrown";

    public DruidcraftRegrown() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        bus.addListener(this::commonSetup);
        bus.addListener(this::clientSetup);
        bus.addListener(this::particleSetup);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Packet Handler Initialization
        LOGGER.info("Common setup event complete!");
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        RenderSetup.setupRenderers(event);
        LOGGER.info("Client setup event complete!");
    }

    private void particleSetup(final ParticleFactoryRegisterEvent event) {
        // Particle Factory Registration Event
        LOGGER.info("Particle setup event complete!");
    }

}