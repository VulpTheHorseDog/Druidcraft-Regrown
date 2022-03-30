package com.vulp.druidcraftrg.capabilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;

public interface ITempSpawn extends INBTSerializable<CompoundTag> {

    @Nullable
    SpawnDataHolder getSpawnData();

    void setSpawnData(SpawnDataHolder holder);

    void removeSpawnData();

    boolean hasSpawnData();

}
