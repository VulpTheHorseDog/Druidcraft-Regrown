package com.vulp.druidcraftrg.capabilities;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public class SpawnDataHolder {

    private final BlockPos pos;
    private final ResourceKey<Level> dimension;
    private final float angle;
    private final boolean forced;

    public SpawnDataHolder() {
        this(null, Level.OVERWORLD, 0.0F, false);
    }

    public SpawnDataHolder(BlockPos pos, ResourceKey<Level> dimension, float angle, boolean forced) {
        this.pos = pos;
        this.dimension = dimension;
        this.angle = angle;
        this.forced = forced;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public ResourceKey<Level> getDimension() {
        return this.dimension;
    }

    public float getAngle() {
        return this.angle;
    }

    public boolean isForced() {
        return this.forced;
    }

}
