package com.vulp.druidcraftrg.mixins;

import com.vulp.druidcraftrg.blocks.BedrollBlock;
import com.vulp.druidcraftrg.capabilities.ITempSpawn;
import com.vulp.druidcraftrg.capabilities.SpawnDataHolder;
import com.vulp.druidcraftrg.capabilities.TempSpawnProvider;
import com.vulp.druidcraftrg.capabilities.TempSpawnStorage;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.DemoPlayerInteractionManager;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.server.management.PlayerList;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.IWorldInfo;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Mixin(PlayerList.class)
public abstract class PlayerListMixin {

    @Shadow @Final private MinecraftServer server;

    @Shadow public abstract boolean removePlayer(ServerPlayerEntity player);

    @Shadow protected abstract void updatePlayerGameMode(ServerPlayerEntity p_72381_1_, @Nullable ServerPlayerEntity p_72381_2_, ServerWorld p_72381_3_);

    @Shadow public abstract void sendLevelInfo(ServerPlayerEntity p_72354_1_, ServerWorld p_72354_2_);

    @Shadow public abstract void sendPlayerPermissionLevel(ServerPlayerEntity p_187243_1_);

    @Shadow public abstract boolean addPlayer(ServerPlayerEntity player);

    @Shadow @Final private Map<UUID, ServerPlayerEntity> playersByUUID;

    @Inject(at = @At("HEAD"), method = "respawn", cancellable = true)
    private void respawn(ServerPlayerEntity player, boolean bool, CallbackInfoReturnable<ServerPlayerEntity> cir) {
        Optional<ITempSpawn> spawnData = player.getCapability(TempSpawnProvider.TEMP_SPAWN).resolve();
        // For some reason the second time you try and respawn, the below if statement is not passed. Because the capability returns null. Likely because of the janky capability handling in the first place.
        if (spawnData.isPresent() && spawnData.get().hasSpawnData()) {
            SpawnDataHolder holder = spawnData.get().getSpawnData();
            ServerWorld serverworld = this.server.getLevel(holder.getDimension());
            if (holder.getPos() != null && serverworld.getBlockState(holder.getPos()).getBlock() instanceof BedrollBlock) {
                ServerWorld tempWorld = this.server.getLevel(player.getRespawnDimension());
                BlockPos mainBedPos = player.getRespawnPosition();
                float mainBedAngle = player.getRespawnAngle();
                boolean mainBedForce = player.isRespawnForced();
                Optional<Vector3d> opt;
                // Below probably actially respawns the player. Make sure that doesnt happen and it just detects if block is there.
                if (tempWorld != null && mainBedPos != null) {
                    opt = PlayerEntity.findRespawnPositionAndUseSpawnBlock(serverworld, mainBedPos, mainBedAngle, mainBedForce, bool);
                } else {
                    opt = Optional.empty();
                }
                ServerWorld mainBedWorld = tempWorld != null && opt.isPresent() ? tempWorld : this.server.overworld();
                this.removePlayer(player);
                player.getLevel().removePlayer(player, true); // Forge: keep data until copyFrom called
                BlockPos pos = holder.getPos();
                float angle = holder.getAngle();
                boolean forceRespawn = holder.isForced();
                Optional<Vector3d> optional;
                if (pos != null) {
                    optional = BedBlock.findStandUpPosition(EntityType.PLAYER, serverworld, pos, angle);
                } else {
                    optional = Optional.empty();
                }

                ServerWorld serverworld1 = optional.isPresent() ? serverworld : this.server.overworld();
                PlayerInteractionManager playerinteractionmanager;
                if (this.server.isDemo()) {
                    playerinteractionmanager = new DemoPlayerInteractionManager(serverworld1);
                } else {
                    playerinteractionmanager = new PlayerInteractionManager(serverworld1);
                }

                ServerPlayerEntity serverplayerentity = new ServerPlayerEntity(this.server, serverworld1, player.getGameProfile(), playerinteractionmanager);
                serverplayerentity.connection = player.connection;
                serverplayerentity.restoreFrom(player, bool);
                player.remove(false); // Forge: clone event had a chance to see old data, now discard it
                serverplayerentity.setId(player.getId());
                serverplayerentity.setMainArm(player.getMainArm());

                for (String s : player.getTags()) {
                    serverplayerentity.addTag(s);
                }

                this.updatePlayerGameMode(serverplayerentity, player, serverworld1);

                boolean flag = optional.isPresent();
                if (flag) {
                    Vector3d vector3d = optional.get();
                    Vector3d vector3d1 = Vector3d.atBottomCenterOf(pos).subtract(vector3d).normalize();
                    float f1 = (float) MathHelper.wrapDegrees(MathHelper.atan2(vector3d1.z, vector3d1.x) * (double) (180F / (float) Math.PI) - 90.0D);
                    serverplayerentity.moveTo(vector3d.x, vector3d.y, vector3d.z, f1, 0.0F);
                    serverplayerentity.setRespawnPosition(mainBedWorld.dimension(), mainBedPos, mainBedAngle, mainBedForce, false);
                    Optional<ITempSpawn> tempSpawnNew = serverplayerentity.getCapability(TempSpawnProvider.TEMP_SPAWN).resolve();
                    tempSpawnNew.ifPresent(iTempSpawn -> iTempSpawn.setSpawnData(holder));
                } else if (pos != null) {
                    serverplayerentity.connection.send(new SChangeGameStatePacket(SChangeGameStatePacket.NO_RESPAWN_BLOCK_AVAILABLE, 0.0F));
                }

                while (!serverworld1.noCollision(serverplayerentity) && serverplayerentity.getY() < 256.0D) {
                    serverplayerentity.setPos(serverplayerentity.getX(), serverplayerentity.getY() + 1.0D, serverplayerentity.getZ());
                }

                IWorldInfo iworldinfo = serverplayerentity.level.getLevelData();
                serverplayerentity.connection.send(new SRespawnPacket(serverplayerentity.level.dimensionType(), serverplayerentity.level.dimension(), BiomeManager.obfuscateSeed(serverplayerentity.getLevel().getSeed()), serverplayerentity.gameMode.getGameModeForPlayer(), serverplayerentity.gameMode.getPreviousGameModeForPlayer(), serverplayerentity.getLevel().isDebug(), serverplayerentity.getLevel().isFlat(), bool));
                serverplayerentity.connection.teleport(serverplayerentity.getX(), serverplayerentity.getY(), serverplayerentity.getZ(), serverplayerentity.yRot, serverplayerentity.xRot);
                serverplayerentity.connection.send(new SWorldSpawnChangedPacket(serverworld1.getSharedSpawnPos(), serverworld1.getSharedSpawnAngle()));
                serverplayerentity.connection.send(new SServerDifficultyPacket(iworldinfo.getDifficulty(), iworldinfo.isDifficultyLocked()));
                serverplayerentity.connection.send(new SSetExperiencePacket(serverplayerentity.experienceProgress, serverplayerentity.totalExperience, serverplayerentity.experienceLevel));
                this.sendLevelInfo(serverplayerentity, serverworld1);
                this.sendPlayerPermissionLevel(serverplayerentity);
                serverworld1.addRespawnedPlayer(serverplayerentity);
                this.addPlayer(serverplayerentity);
                this.playersByUUID.put(serverplayerentity.getUUID(), serverplayerentity);
                serverplayerentity.initMenu();
                serverplayerentity.setHealth(serverplayerentity.getHealth());
                net.minecraftforge.fml.hooks.BasicEventHooks.firePlayerRespawnEvent(serverplayerentity, bool);
                cir.setReturnValue(serverplayerentity);
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
