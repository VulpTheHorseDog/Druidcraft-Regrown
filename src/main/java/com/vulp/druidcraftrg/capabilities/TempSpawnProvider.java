package com.vulp.druidcraftrg.capabilities;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class TempSpawnProvider implements ICapabilitySerializable<INBT> {

    @CapabilityInject(ITempSpawn.class)
    public static final Capability<ITempSpawn> TEMP_SPAWN = null;

    private LazyOptional<ITempSpawn> INSTANCE = LazyOptional.of(TEMP_SPAWN::getDefaultInstance);

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        return cap == TEMP_SPAWN ? INSTANCE.cast() : LazyOptional.empty();
    }

    @Override
    public INBT serializeNBT() {
        return TEMP_SPAWN.getStorage().writeNBT(TEMP_SPAWN, this.INSTANCE.orElseThrow(() -> new IllegalArgumentException("LazyOptional must not be empty!")), null);
    }

    @Override
    public void deserializeNBT(INBT nbt) {
        TEMP_SPAWN.getStorage().readNBT(TEMP_SPAWN, this.INSTANCE.orElseThrow(() -> new IllegalArgumentException("LazyOptional must not be empty!")), null, nbt);
    }

}
