package com.vulp.druidcraftrg.capabilities;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;

public class TempSpawnProvider implements ICapabilityProvider, ICapabilitySerializable<CompoundTag> {

    public static Capability<TempSpawnCapability> TEMP_SPAWN_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
    private TempSpawnCapability cache = null;
    private final LazyOptional<TempSpawnCapability> INSTANCE = LazyOptional.of(this::createInstance);

    @Nonnull
    private TempSpawnCapability createInstance() {
        if (this.cache == null) {
            this.cache = new TempSpawnCapability();
        }
        return this.cache;
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        return cap == TEMP_SPAWN_CAPABILITY ? INSTANCE.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return createInstance().serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        createInstance().deserializeNBT(nbt);
    }

}
