package com.vulp.druidcraftrg.capabilities;

import com.vulp.druidcraftrg.DruidcraftRegrown;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class TempSpawnStorage implements Capability.IStorage<ITempSpawn> {

    @Nullable
    @Override
    public INBT writeNBT(Capability<ITempSpawn> capability, ITempSpawn instance, Direction side) {
        SpawnDataHolder holder = instance.getSpawnData();
        CompoundNBT compound = new CompoundNBT();
        if (holder != null) {
            BlockPos pos = holder.getPos();
            if (pos != null) {
                compound.putIntArray("Pos", new int[]{pos.getX(), pos.getY(), pos.getZ()});
            }
            ResourceLocation.CODEC.encodeStart(NBTDynamicOps.INSTANCE, holder.getDimension().location()).resultOrPartial(DruidcraftRegrown.LOGGER::error).ifPresent((dimData) -> compound.put("Dim", dimData));
            compound.putFloat("Angle", holder.getAngle());
            compound.putBoolean("Force", holder.isForced());
        }
        return compound;
    }

    @Override
    public void readNBT(Capability<ITempSpawn> capability, ITempSpawn instance, Direction side, INBT nbt) {
        if (!(instance instanceof TempSpawn))
            throw new IllegalArgumentException("Can not deserialize to an instance that isn't the default implementation");
        CompoundNBT compound = (CompoundNBT) nbt;
        if (!compound.isEmpty()) {
            int[] pos = null;
            if (compound.contains("Pos")) {
                pos = compound.getIntArray("Pos");
            }
            instance.setSpawnData(new SpawnDataHolder(pos == null ? null : new BlockPos(pos[0], pos[1], pos[2]), compound.contains("Dim") ? World.RESOURCE_KEY_CODEC.parse(NBTDynamicOps.INSTANCE, compound.get("Dim")).resultOrPartial(DruidcraftRegrown.LOGGER::error).orElse(World.OVERWORLD) : World.OVERWORLD, compound.getFloat("Angle"), compound.getBoolean("Force")));
        } else {
            instance.setSpawnData(new SpawnDataHolder());
        }
    }

}
