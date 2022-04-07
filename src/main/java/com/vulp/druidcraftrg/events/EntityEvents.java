package com.vulp.druidcraftrg.events;

import com.vulp.druidcraftrg.DruidcraftRegrown;
import com.vulp.druidcraftrg.blocks.BedrollBlock;
import com.vulp.druidcraftrg.blocks.RopeBlock;
import com.vulp.druidcraftrg.capabilities.SpawnDataHolder;
import com.vulp.druidcraftrg.capabilities.TempSpawnCapability;
import com.vulp.druidcraftrg.capabilities.TempSpawnProvider;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Optional;

@Mod.EventBusSubscriber(modid=DruidcraftRegrown.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE)
public class EntityEvents {

    @SubscribeEvent
    public static void onPlayerBreakBlock(PlayerEvent.BreakSpeed event) {
        Player player = event.getPlayer();
        if (RopeBlock.isEntityInBlock(player) && !player.onGround) {
            event.setNewSpeed(event.getOriginalSpeed() * 5.0F);
        }
    }

    @SubscribeEvent
    public static void onPlayerSpawnSet(PlayerSetSpawnEvent event) {
        Player player = event.getPlayer();
        if (player instanceof ServerPlayer) {
            ServerLevel world = ((ServerPlayer) player).getLevel();
            BlockPos pos = event.getNewSpawn();
            if (pos != null && (world.getBlockState(pos).getBlock() instanceof BedrollBlock)) {
                Optional<TempSpawnCapability> spawnData = player.getCapability(TempSpawnProvider.TEMP_SPAWN_CAPABILITY).resolve();
                if (spawnData.isPresent()) {
                    ResourceKey<Level> dimension = world.dimension();
                    SpawnDataHolder holder = spawnData.get().getSpawnData();
                    boolean flag = holder != null && pos.equals(holder.getPos()) && dimension.equals(holder.getDimension());
                    if (!flag) {
                        player.sendMessage(new TranslatableComponent("block.druidcraftrg.bedroll.set_temp_spawn"), Util.NIL_UUID);
                    }
                    spawnData.get().setSpawnData(new SpawnDataHolder(pos, dimension, player.yRot, false));
                    event.setCanceled(true);
                }
            }
        }
    }

}
