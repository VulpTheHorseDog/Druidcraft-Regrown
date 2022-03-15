package com.vulp.druidcraftrg.blocks;

import com.vulp.druidcraftrg.DruidcraftRegrown;
import com.vulp.druidcraftrg.blocks.tile.CrateTileEntity;
import com.vulp.druidcraftrg.init.BlockInit;
import com.vulp.druidcraftrg.state.properties.CrateType;
import net.minecraft.block.*;
import net.minecraft.entity.monster.piglin.PiglinTasks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.*;

public class CrateBlock extends ContainerBlock {

    public static final EnumProperty<CrateType> TYPE = EnumProperty.create("type", CrateType.class);
    public static final int[] CRATE_TYPE_START_POINTS = new int[]{0, 8, 12, 16, 20, 22, 24, 26};

    public CrateBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(TYPE, CrateType.SMALL));
    }

    @Nullable
    @Override
    public TileEntity newBlockEntity(IBlockReader reader) {
        return new CrateTileEntity();
    }

    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTrace) {
        if (world.isClientSide) {
            return ActionResultType.SUCCESS;
        } else {
            INamedContainerProvider inamedcontainerprovider = this.getMenuProvider(state, world, pos);
            if (inamedcontainerprovider != null) {
                player.openMenu(inamedcontainerprovider);
                // player.awardStat(this.getOpenChestStat());
                PiglinTasks.angerNearbyPiglins(player, true);
            }

            return ActionResultType.CONSUME;
        }
    }

    private boolean[] createCrateArray(IWorld world, BlockPos pos) {
        boolean[] boolArray = new boolean[27];
        int ticker = 0;
        for (int y = -1; y < 2; y++) {
            for (int x = -1; x < 2; x++) {
                for (int z = -1; z < 2; z++) {
                    boolArray[ticker] = world.getBlockState(pos.offset(x, y, z)).getBlock() == this;
                    ticker++;
                }
            }
        }
        boolArray[13] = true;
        return boolArray;
    }

    // up/down = increments of 9, east/west = increments of 3.
    private CrateType detectCrateConfig(IWorld world, BlockPos pos, boolean[] boolArray, boolean updateConfig) {
        int x_increment = 3;
        int y_increment = 9;
        int centre = 13;
        int ticker = 0;

        for (int m = 0; m < 7; m++) {
            for (int j = 0; j < (m == 3 || (m > 3 && m != 6) ? 1 : 2); j++) {
                for (int i = 0; i < (m == 2 || (m > 3 && m != 5)  ? 1 : 2); i++) {
                    for (int k = 0; k < (m == 1 || (m > 3 && m != 4)  ? 1 : 2); k++) {
                        boolean latch = true;
                        // Large crate checker:
                        if (m == 0) {
                            for (int y = -j; y < 2 - j; y++) {
                                for (int x = -i; x < 2 - i; x++) {
                                    for (int z = -k; z < 2 - k; z++) {
                                        if (!boolArray[13 + (x * 3) + (y * 9) + z]) {
                                            latch = false;
                                            break;
                                        }
                                    }
                                }
                            }
                        } else if (m < 4) {
                            // Wide crate checker:
                            for (int y = -j; y < (m == 3 ? 1 : 2 - j); y++) {
                                for (int x = -i; x < (m == 2 ? 1 : 2 - i); x++) {
                                    for (int z = -k; z < (m == 1 ? 1 : 2 - k); z++) {
                                        if (!boolArray[13 + (x * 3) + (y * 9) + z]) {
                                            latch = false;
                                            break;
                                        }
                                    }
                                }
                            }
                        } else {
                            // Thin crate checker:
                            for (int y = -j; y < (m != 6 ? 1 : 2 - j); y++) {
                                for (int x = -i; x < (m != 5 ? 1 : 2 - i); x++) {
                                    for (int z = -k; z < (m != 4 ? 1 : 2 - k); z++) {
                                        if (!boolArray[13 + (x * 3) + (y * 9) + z]) {
                                            latch = false;
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        if (latch) {
                            CrateType placeType = CrateType.values()[ticker];
                            DruidcraftRegrown.LOGGER.debug("Crate attempted. [" + ticker + "]");
                            HashMap<BlockPos, CrateType> map = checkConfigValid(world, pos, placeType, ticker);
                            if (map != null) {
                                DruidcraftRegrown.LOGGER.debug("CRATE DETECTED! [" + ticker + "]");
                                if (updateConfig) {
                                    map.forEach((blockPos, crateType) -> world.setBlock(blockPos, BlockInit.crate.defaultBlockState().setValue(TYPE, crateType), 2));
                                }
                                return placeType;
                            }
                        }
                        ticker++;
                    }
                }
            }
        }
        return CrateType.SMALL;
    }

    private boolean[] crateTypeToCrateArray(IWorld world, BlockPos pos, CrateType type) {
        List<Direction> directions = getAttachDirectionsFromType(type);
        boolean[] array = createCrateArray(world, pos);
        debugArrayCreation(array);
        boolean[] arrayMask = new boolean[27];
        int[] integers = new int[]{0, 0, 0};
        for (Direction dir : directions) {
            integers[dir.getAxis().ordinal()] = dir.getStepX() + dir.getStepY() + dir.getStepZ();
        }
        for (int y = integers[1] == -1 ? -1 : 0; y < (integers[1] == 1 ? 2 : 1); y++) {
            for (int x = integers[0] == -1 ? -1 : 0; x < (integers[0] == 1 ? 2 : 1); x++) {
                for (int z = integers[2] == -1 ? -1 : 0; z < (integers[2] == 1 ? 2 : 1); z++) {
                    arrayMask[13 + (x * 3) + (y * 9) + z] = true;
                }
            }
        }
        for (int i = 0; i < arrayMask.length; i++) {
            if (!arrayMask[i]) {
                array[i] = false;
            }
        }
        if (!world.isClientSide())
            debugArrayCreation(array);
        return array;
    }

    // Triggers when setBlock() happens! Is a problem.
    // TODO: On broken, crates outside of original crate bounds are still connected to, which shouldn't happen.
    @Override
    public void onRemove(BlockState state, World world, BlockPos pos, BlockState replacingState, boolean bool) {
        if (replacingState.getBlock() != this) {
            CrateType type = state.getValue(TYPE);
            List<Direction> directions = getAttachDirectionsFromType(type);
            int[] integers = new int[]{0, 0, 0};
            for (Direction dir : directions) {
                integers[dir.getAxis().ordinal()] = dir.getStepX() + dir.getStepY() + dir.getStepZ();
            }
            List<BlockPos> posList = new ArrayList<>(Collections.emptyList());
            for (int y = integers[1] == -1 ? -1 : 0; y < (integers[1] == 1 ? 2 : 1); y++) {
                for (int x = integers[0] == -1 ? -1 : 0; x < (integers[0] == 1 ? 2 : 1); x++) {
                    for (int z = integers[2] == -1 ? -1 : 0; z < (integers[2] == 1 ? 2 : 1); z++) {
                        posList.add(pos.offset(x, y, z));
                    }
                }
            }
            posList.remove(pos);
            posList.forEach(blockPos -> {
                BlockState currentState = world.getBlockState(blockPos);
                if (currentState.getBlock() instanceof CrateBlock) {
                    world.setBlock(blockPos, currentState.setValue(TYPE, detectCrateConfig(world, blockPos, crateTypeToCrateArray(world, blockPos, currentState.getValue(TYPE)), true)), 2); // Try setting to false sometime?
                }
            });
        }
        super.onRemove(state, world, pos, replacingState, bool);
    }

    private List<Direction> getAttachDirectionsFromType(CrateType type) {
        List<Direction> directions = new LinkedList<>(Arrays.asList(type.getOpenDirections()));
        List<Direction> oppositeCache = new ArrayList<>(Collections.emptyList());
        directions.removeIf(direction -> {
            Direction opposite = direction.getOpposite();
            oppositeCache.add(opposite);
            return directions.contains(opposite) || oppositeCache.contains(direction);
        });
        return directions;
    }

    @Nullable
    private HashMap<BlockPos, CrateType> checkConfigValid(IWorld world, BlockPos pos, CrateType type, int oldTicker) {
        HashMap<BlockPos, CrateType> map = new HashMap<>();
        List<Direction> directions = getAttachDirectionsFromType(type);
        int[] integers = new int[]{0, 0, 0};
        for (Direction dir : directions) {
            integers[dir.getAxis().ordinal()] = dir.getStepX() + dir.getStepY() + dir.getStepZ();
        }
        // Below is an attempted fix to get the ticker to start at the start of its own crate type.
        for (int i = 0; i < 8; i++) {
            int finalOldTicker = oldTicker;
            if (Arrays.stream(CRATE_TYPE_START_POINTS).noneMatch(j -> j == finalOldTicker)) {
                oldTicker--;
            } else {
                break;
            }
        }
        int ticker = oldTicker;
        for (int y = 0; y < (integers[1] == 0 ? 1 : 2); y++) {
            for (int x = 0; x < (integers[0] == 0 ? 1 : 2); x++) {
                for (int z = 0; z < (integers[2] == 0 ? 1 : 2); z++) {
                    int xPos = pos.getX() + x - (integers[0] == -1 ? 1 : 0);
                    int yPos = pos.getY() + y - (integers[1] == -1 ? 1 : 0);
                    int zPos = pos.getZ() + z - (integers[2] == -1 ? 1 : 0);
                    // Dynamic is the various blocks it checks against.
                    BlockPos dynamicPos = new BlockPos(xPos, yPos, zPos);
                    if (dynamicPos.getX() != pos.getX() || dynamicPos.getY() != pos.getY() || dynamicPos.getZ() != pos.getZ()) {  // The last block placed will not turn here, as it is placed after the fact.
                        /*if (!world.isClientSide)
                            DruidcraftRegrown.LOGGER.debug(dynamicPos);*/
                        CrateType crateTypeReference = CrateType.values()[ticker]; // Acts as the template that the block needs to fill.
                        /*if (!world.isClientSide)
                            DruidcraftRegrown.LOGGER.debug(dynamicCrateType.getSerializedName());*/
                        for (Direction dir : getAttachDirectionsFromType(crateTypeReference)) {
                            BlockState dynamicState = world.getBlockState(dynamicPos);
                            CrateType dynamicType = dynamicState.getValue(TYPE);
                            List<Direction> dynamicOpenDirections = Arrays.asList(dynamicType.getOpenDirections());


                            boolean flag = false;
                            if (dynamicType.getCrateSize() == 2 && crateTypeReference.getCrateSize() == 4) {
                                List<Direction.Axis> axisList = new ArrayList<>(Collections.emptyList());
                                for (Direction direction : getAttachDirectionsFromType(crateTypeReference)) {
                                    axisList.add(direction.getAxis());
                                }
                                if (!axisList.contains(getAttachDirectionsFromType(dynamicType).get(0).getAxis())) {
                                    flag = true;
                                }
                            }



                            if (dynamicState.getBlock() != this || dynamicType.getCrateSize() >= crateTypeReference.getCrateSize() || flag || !dynamicOpenDirections.contains(dir)) {
                                return null;
                            }
                        }
                        map.put(dynamicPos, CrateType.values()[ticker]);
                    }
                    ticker++;
                    // world.setBlock(new BlockPos(xPos, yPos, zPos), Blocks.GLOWSTONE.defaultBlockState(), 2);
                }
            }
        }
        return map;
    }

    private void debugArrayCreation(boolean[] boolArray) {
        for (int i = 0; i < 3; i++) {
            DruidcraftRegrown.LOGGER.debug("----");
            for (int j = 0; j < 3; j++) {
                int k = (i*9)+(j*3);
                DruidcraftRegrown.LOGGER.debug((boolArray[k] ? "O" : "/") + (boolArray[k+1] ? "O" : "/") + (boolArray[k+2] ? "O" : "/"));
            }
        }
        DruidcraftRegrown.LOGGER.debug("----");
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state != null) {
            World world = context.getLevel();
            BlockPos pos = context.getClickedPos();
            CrateType type = CrateType.SMALL;
            if (context.canPlace()) {
                boolean[] boolArray = createCrateArray(world, pos);
            /*if (!world.isClientSide) {
                debugArrayCreation(boolArray);
            }*/
                type = detectCrateConfig(world, pos, boolArray, true);
            }
            return state.setValue(TYPE, type);
        } else return null;
    }

    // Triggers when setBlock() happens! Is a problem.
    @Override
    public void onPlace(BlockState state, World world, BlockPos pos, BlockState state1, boolean bool) {
        // detectCrateConfig(world, pos, createCrateArray(world, pos), true);
        super.onPlace(state, world, pos, state1, bool);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(TYPE);
    }

    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.MODEL;
    }
}
