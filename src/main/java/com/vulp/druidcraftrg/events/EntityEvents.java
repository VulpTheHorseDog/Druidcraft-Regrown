package com.vulp.druidcraftrg.events;

import com.vulp.druidcraftrg.DruidcraftRegrown;
import com.vulp.druidcraftrg.blocks.BedrollBlock;
import com.vulp.druidcraftrg.blocks.RopeBlock;
import com.vulp.druidcraftrg.capabilities.ITempSpawn;
import com.vulp.druidcraftrg.capabilities.SpawnDataHolder;
import com.vulp.druidcraftrg.capabilities.TempSpawnProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Optional;

@Mod.EventBusSubscriber(modid=DruidcraftRegrown.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE)
public class EntityEvents {

    @SubscribeEvent
    public static void onPlayerBreakBlock(PlayerEvent.BreakSpeed event) {
        PlayerEntity player = event.getPlayer();
        if (RopeBlock.isEntityInBlock(player) && !player.onGround) {
            event.setNewSpeed(event.getOriginalSpeed() * 5.0F);
        }
    }

    @SubscribeEvent
    public static void onPlayerSpawnSet(PlayerSetSpawnEvent event) {
        PlayerEntity player = event.getPlayer();
        if (player instanceof ServerPlayerEntity) {
            ServerWorld world = ((ServerPlayerEntity) player).getLevel();
            BlockPos pos = event.getNewSpawn();
            if (pos != null && (world.getBlockState(pos).getBlock() instanceof BedrollBlock)) {
                Optional<ITempSpawn> spawnData = ((ServerPlayerEntity) player).getCapability(TempSpawnProvider.TEMP_SPAWN).resolve();
                if (spawnData.isPresent()) {
                    RegistryKey<World> dimension = world.dimension();
                    SpawnDataHolder holder = spawnData.get().getSpawnData();
                    boolean flag = holder != null && pos.equals(holder.getPos()) && dimension.equals(holder.getDimension());
                    if (!flag) {
                        player.sendMessage(new TranslationTextComponent("block.druidcraftrg.set_temp_spawn"), Util.NIL_UUID);
                    }
                    spawnData.get().setSpawnData(new SpawnDataHolder(pos, dimension, player.yRot, false));
                    event.setCanceled(true);
                }
            }
        }
    }

}
