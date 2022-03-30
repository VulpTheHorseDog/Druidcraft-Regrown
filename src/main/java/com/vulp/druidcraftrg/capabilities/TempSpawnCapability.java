package com.vulp.druidcraftrg.capabilities;

import com.vulp.druidcraftrg.DruidcraftRegrown;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class TempSpawnCapability implements ITempSpawn {

    private SpawnDataHolder tempSpawn;

    @Override
    @Nullable
    public SpawnDataHolder getSpawnData() {
        return this.tempSpawn;
    }

    @Override
    public void setSpawnData(SpawnDataHolder holder) {
        this.tempSpawn = holder;
    }

    @Override
    public void removeSpawnData() {
        this.tempSpawn = null;
    }

    @Override
    public boolean hasSpawnData() {
        return this.tempSpawn != null;
    }

    @Override
    public CompoundTag serializeNBT() {
            CompoundTag compound = new CompoundTag();
            if (this.tempSpawn != null) {
                BlockPos pos = this.tempSpawn.getPos();
                if (pos != null) {
                    compound.putIntArray("Pos", new int[]{pos.getX(), pos.getY(), pos.getZ()});
                }
                compound.putFloat("Angle", this.tempSpawn.getAngle());
                compound.putBoolean("Force", this.tempSpawn.isForced());
            }
            return compound;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (!nbt.isEmpty()) {
            int[] pos = null;
            if (nbt.contains("Pos")) {
                pos = nbt.getIntArray("Pos");
            }
            this.tempSpawn = new SpawnDataHolder(pos == null ? null : new BlockPos(pos[0], pos[1], pos[2]), nbt.contains("Dim") ? Level.RESOURCE_KEY_CODEC.parse(NbtOps.INSTANCE, nbt.get("Dim")).resultOrPartial(DruidcraftRegrown.LOGGER::error).orElse(Level.OVERWORLD) : Level.OVERWORLD, nbt.getFloat("Angle"), nbt.getBoolean("Force"));
        } else {
            this.tempSpawn = new SpawnDataHolder();
        }
    }
}
