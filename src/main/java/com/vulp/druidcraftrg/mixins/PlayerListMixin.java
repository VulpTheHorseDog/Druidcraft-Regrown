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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
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
        // For some reason the second time you try and respawn, the below if statement is not passed. Because the capability returns null. Likely because of the janky capability handling in the first place.
        if (spawnData.isPresent() && spawnData.get().hasSpawnData()) {
            SpawnDataHolder holder = spawnData.get().getSpawnData();
            ServerLevel ServerLevel = this.server.getLevel(holder.getDimension());
            if (holder.getPos() != null && ServerLevel.getBlockState(holder.getPos()).getBlock() instanceof BedrollBlock) {
                this.players.remove(player);
                player.getLevel().removePlayerImmediately(player, Entity.RemovalReason.DISCARDED);
                float angle = holder.getAngle();
                BlockPos pos = holder.getPos();
                boolean flag = player.isRespawnForced();
                ServerLevel serverlevel = this.server.getLevel(player.getRespawnDimension());
                Optional<Vec3> optional;
                if (serverlevel != null && pos != null) {
                    optional = Player.findRespawnPositionAndUseSpawnBlock(serverlevel, pos, angle, flag, bool);
                } else {
                    optional = Optional.empty();
                }

                ServerLevel serverlevel1 = serverlevel != null && optional.isPresent() ? serverlevel : this.server.overworld();
                ServerPlayer serverplayer = new ServerPlayer(this.server, serverlevel1, player.getGameProfile());
                serverplayer.connection = player.connection;
                serverplayer.restoreFrom(player, bool);
                serverplayer.setId(player.getId());
                serverplayer.setMainArm(player.getMainArm());

                for (String s : player.getTags()) {
                    serverplayer.addTag(s);
                }

                boolean flag2 = false;
                if (optional.isPresent()) {
                    BlockState blockstate = serverlevel1.getBlockState(pos);
                    boolean flag1 = blockstate.is(Blocks.RESPAWN_ANCHOR);
                    Vec3 vec3 = optional.get();
                    float f1;
                    if (!blockstate.is(BlockTags.BEDS) && !flag1) {
                        f1 = angle;
                    } else {
                        Vec3 vec31 = Vec3.atBottomCenterOf(pos).subtract(vec3).normalize();
                        f1 = (float) Mth.wrapDegrees(Mth.atan2(vec31.z, vec31.x) * (double) (180F / (float) Math.PI) - 90.0D);
                    }

                    serverplayer.moveTo(vec3.x, vec3.y, vec3.z, f1, 0.0F);
                    serverplayer.setRespawnPosition(serverlevel1.dimension(), pos, angle, flag, false);
                    flag2 = !bool && flag1;
                    Optional<TempSpawnCapability> tempSpawnNew = serverplayer.getCapability(TempSpawnProvider.TEMP_SPAWN_CAPABILITY).resolve();
                    tempSpawnNew.ifPresent(iTempSpawn -> iTempSpawn.setSpawnData(holder));
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
                if (flag2) {
                    serverplayer.connection.send(new ClientboundSoundPacket(SoundEvents.RESPAWN_ANCHOR_DEPLETE, SoundSource.BLOCKS, (double) pos.getX(), (double) pos.getY(), (double) pos.getZ(), 1.0F, 1.0F));
                }

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
