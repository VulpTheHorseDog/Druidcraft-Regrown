package com.vulp.druidcraftrg.mixins;

import com.vulp.druidcraftrg.blocks.BedrollBlock;
import com.vulp.druidcraftrg.capabilities.SpawnDataHolder;
import com.vulp.druidcraftrg.capabilities.TempSpawnCapability;
import com.vulp.druidcraftrg.capabilities.TempSpawnProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Mixin(PlayerList.class)
public abstract class PlayerListMixin {

    @Shadow @Final private MinecraftServer server;

    @Shadow public abstract void sendLevelInfo(ServerPlayer p_72354_1_, ServerLevel p_72354_2_);

    @Shadow public abstract void sendPlayerPermissionLevel(ServerPlayer p_187243_1_);

    @Shadow @Final private Map<UUID, ServerPlayer> playersByUUID;

    @Shadow @Final private List<ServerPlayer> players;

    @Shadow public abstract boolean addPlayer(ServerPlayer player);

    @Inject(at = @At("HEAD"), method = "respawn", cancellable = true)
    private void respawn(ServerPlayer player, boolean bool, CallbackInfoReturnable<ServerPlayer> cir) {
        Optional<TempSpawnCapability> spawnData = player.getCapability(TempSpawnProvider.TEMP_SPAWN_CAPABILITY).resolve();
        if (spawnData.isPresent() && spawnData.get().hasSpawnData()) {
            SpawnDataHolder holder = spawnData.get().getSpawnData();
            ServerLevel ServerLevel = this.server.getLevel(holder.getDimension());
            if (holder.getPos() != null && ServerLevel.getBlockState(holder.getPos()).getBlock() instanceof BedrollBlock) {
                this.players.remove(player);
                player.getLevel().removePlayerImmediately(player, Entity.RemovalReason.DISCARDED);
                float angle = holder.getAngle();
                BlockPos pos = holder.getPos();
                boolean flag = holder.isForced();
                ServerLevel playerRespawnLevel = this.server.getLevel(player.getRespawnDimension());
                Optional<Vec3> optional;
                if (playerRespawnLevel != null && pos != null) {
                    optional = Player.findRespawnPositionAndUseSpawnBlock(playerRespawnLevel, pos, angle, flag, bool);
                } else {
                    optional = Optional.empty();
                }

                ServerLevel serverlevel1 = playerRespawnLevel != null && optional.isPresent() ? playerRespawnLevel : this.server.overworld();
                ServerPlayer serverplayer = new ServerPlayer(this.server, serverlevel1, player.getGameProfile());
                serverplayer.connection = player.connection;
                serverplayer.restoreFrom(player, bool);
                serverplayer.setId(player.getId());
                serverplayer.setMainArm(player.getMainArm());

                for (String s : player.getTags()) {
                    serverplayer.addTag(s);
                }

                if (optional.isPresent()) {
                    Vec3 vec3 = optional.get();
                    serverplayer.moveTo(vec3.x, vec3.y, vec3.z, angle, 0.0F);
                    serverplayer.setRespawnPosition(player.getRespawnDimension(), player.getRespawnPosition(), player.getRespawnAngle(), player.isRespawnForced(), false);
                    // Carry the temp spawn data from old player to repawned player.
                    Optional<TempSpawnCapability> tempSpawnNew = serverplayer.getCapability(TempSpawnProvider.TEMP_SPAWN_CAPABILITY).resolve();
                    tempSpawnNew.ifPresent(tempSpawn -> tempSpawn.setSpawnData(holder));
                } else if (pos != null) {
                    serverplayer.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.NO_RESPAWN_BLOCK_AVAILABLE, 0.0F));
                }

                while (!serverlevel1.noCollision(serverplayer) && serverplayer.getY() < (double) serverlevel1.getMaxBuildHeight()) {
                    serverplayer.setPos(serverplayer.getX(), serverplayer.getY() + 1.0D, serverplayer.getZ());
                }

                LevelData leveldata = serverplayer.level.getLevelData();
                serverplayer.connection.send(new ClientboundRespawnPacket(serverplayer.level.dimensionTypeRegistration(), serverplayer.level.dimension(), BiomeManager.obfuscateSeed(serverplayer.getLevel().getSeed()), serverplayer.gameMode.getGameModeForPlayer(), serverplayer.gameMode.getPreviousGameModeForPlayer(), serverplayer.getLevel().isDebug(), serverplayer.getLevel().isFlat(), bool));
                serverplayer.connection.teleport(serverplayer.getX(), serverplayer.getY(), serverplayer.getZ(), serverplayer.getYRot(), serverplayer.getXRot());
                serverplayer.connection.send(new ClientboundSetDefaultSpawnPositionPacket(serverlevel1.getSharedSpawnPos(), serverlevel1.getSharedSpawnAngle()));
                serverplayer.connection.send(new ClientboundChangeDifficultyPacket(leveldata.getDifficulty(), leveldata.isDifficultyLocked()));
                serverplayer.connection.send(new ClientboundSetExperiencePacket(serverplayer.experienceProgress, serverplayer.totalExperience, serverplayer.experienceLevel));
                this.sendLevelInfo(serverplayer, serverlevel1);
                this.sendPlayerPermissionLevel(serverplayer);
                serverlevel1.addRespawnedPlayer(serverplayer);
                this.addPlayer(serverplayer);
                this.playersByUUID.put(serverplayer.getUUID(), serverplayer);
                serverplayer.initInventoryMenu();
                serverplayer.setHealth(serverplayer.getHealth());
                net.minecraftforge.event.ForgeEventFactory.firePlayerRespawnEvent(serverplayer, bool);

                cir.setReturnValue(serverplayer);
            } else {
                spawnData.get().removeSpawnData();
            }
        }
    }

    // Allows us to reference the current instance of the class the mixin is injecting.
    private PlayerList getThis() {
        return ((PlayerList)(Object)this);
    }

}
