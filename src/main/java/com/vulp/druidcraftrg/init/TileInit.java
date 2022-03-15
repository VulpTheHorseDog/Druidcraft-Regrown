package com.vulp.druidcraftrg.init;

import com.vulp.druidcraftrg.DruidcraftRegrownRegistry;
import com.vulp.druidcraftrg.blocks.tile.BeamTileEntity;
import com.vulp.druidcraftrg.blocks.tile.CrateTileEntity;
import com.vulp.druidcraftrg.blocks.tile.RopeTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

public class TileInit {

    public static TileEntityType<RopeTileEntity> rope;
    public static TileEntityType<BeamTileEntity> beam;
    public static TileEntityType<CrateTileEntity> crate;

    public static <T extends TileEntity> TileEntityType<T> register(String id, TileEntityType.Builder<T> builder) {
        TileEntityType<T> type = builder.build(null);
        type.setRegistryName(DruidcraftRegrownRegistry.location(id));
        return type;
    }

}
