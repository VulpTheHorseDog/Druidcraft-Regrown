package com.vulp.druidcraftrg.capabilities;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SpawnDataHolder {

    private final BlockPos pos;
    private final RegistryKey<World> dimension;
    private final float angle;
    private final boolean forced;

    public SpawnDataHolder() {
        this(null, World.OVERWORLD, 0.0F, false);
    }

    public SpawnDataHolder(BlockPos pos, RegistryKey<World> dimension, float angle, boolean forced) {
        this.pos = pos;
        this.dimension = dimension;
        this.angle = angle;
        this.forced = forced;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public RegistryKey<World> getDimension() {
        return this.dimension;
    }

    public float getAngle() {
        return this.angle;
    }

    public boolean isForced() {
        return this.forced;
    }

}
