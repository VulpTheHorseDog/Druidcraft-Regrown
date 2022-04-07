package com.vulp.druidcraftrg.events;

import com.vulp.druidcraftrg.DruidcraftRegrown;
import com.vulp.druidcraftrg.blocks.BedrollBlock;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid= DruidcraftRegrown.MODID, value=Dist.CLIENT, bus=Mod.EventBusSubscriber.Bus.FORGE)
public class RenderEvents {

    @SubscribeEvent
    public static void renderPlayerEventPre(RenderPlayerEvent.Pre event) {
        Player player = event.getPlayer();
        if (player.isSleeping() && player.level.getBlockState(player.getSleepingPos().get()).getBlock() instanceof BedrollBlock) {
            HumanoidModel<AbstractClientPlayer> model = event.getRenderer().getModel();
            model.setAllVisible(false);
            model.head.visible = true;
            model.hat.visible = true;
        }
    }

}