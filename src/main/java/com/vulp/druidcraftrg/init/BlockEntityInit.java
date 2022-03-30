package com.vulp.druidcraftrg.init;

import com.vulp.druidcraftrg.DruidcraftRegrownRegistry;
import com.vulp.druidcraftrg.blocks.tile.BeamTileEntity;
import com.vulp.druidcraftrg.blocks.tile.CrateTileEntity;
import com.vulp.druidcraftrg.blocks.tile.RopeTileEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class BlockEntityInit {

    public static BlockEntityType<RopeTileEntity> rope;
    public static BlockEntityType<BeamTileEntity> beam;
    public static BlockEntityType<CrateTileEntity> crate;

    public static <T extends BlockEntity> BlockEntityType<T> register(String id, BlockEntityType.Builder<T> builder) {
        BlockEntityType<T> type = builder.build(null);
        type.setRegistryName(DruidcraftRegrownRegistry.location(id));
        return type;
    }

}
