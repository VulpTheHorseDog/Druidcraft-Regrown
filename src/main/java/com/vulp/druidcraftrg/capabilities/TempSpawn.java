package com.vulp.druidcraftrg.capabilities;

import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public class TempSpawn implements ITempSpawn {

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
}
