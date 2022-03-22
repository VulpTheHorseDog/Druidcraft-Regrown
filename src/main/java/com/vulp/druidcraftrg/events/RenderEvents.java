package com.vulp.druidcraftrg.events;

import com.vulp.druidcraftrg.DruidcraftRegrown;
import com.vulp.druidcraftrg.blocks.BedrollBlock;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid= DruidcraftRegrown.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE)
public class RenderEvents {

    @SubscribeEvent
    public static void renderPlayerEventPre(RenderPlayerEvent.Pre event) {
        PlayerEntity player = event.getPlayer();
        if (player.isSleeping() && event.getPlayer().level.getBlockState(player.getSleepingPos().get()).getBlock() instanceof BedrollBlock) {
            BipedModel<AbstractClientPlayerEntity> model = event.getRenderer().getModel();
            model.setAllVisible(false);
            model.head.visible = true;
            model.hat.visible = true;
        }
    }

}