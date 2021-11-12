package com.vulp.druidcraftrg.capabilities;

import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public interface ITempSpawn {

    @Nullable
    SpawnDataHolder getSpawnData();

    void setSpawnData(SpawnDataHolder holder);

    void removeSpawnData();

    boolean hasSpawnData();

}
