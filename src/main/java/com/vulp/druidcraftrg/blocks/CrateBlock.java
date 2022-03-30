package com.vulp.druidcraftrg.blocks;

import com.vulp.druidcraftrg.DruidcraftRegrown;
import com.vulp.druidcraftrg.blocks.tile.CrateTileEntity;
import com.vulp.druidcraftrg.init.BlockEntityInit;
import com.vulp.druidcraftrg.inventory.MultiSidedInventory;
import com.vulp.druidcraftrg.inventory.container.CrateContainer;
import com.vulp.druidcraftrg.state.properties.CrateType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.*;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;
import java.util.*;

public class CrateBlock extends BaseEntityBlock {

    public static final EnumProperty<CrateType> TYPE = EnumProperty.create("type", CrateType.class);
    public static final int[] CRATE_TYPE_START_POINTS = new int[]{0, 8, 12, 16, 20, 22, 24, 26};

    /*private static final TileEntityMerger.ICallback<CrateTileEntity, Optional<IInventory>> CRATE_COMBINER = new TileEntityMerger.ICallback<CrateTileEntity, Optional<IInventory>>() {
        @Nonnull
        public Optional<IInventory> acceptQuad(@Nonnull CrateTileEntity tile1, @Nonnull CrateTileEntity tile2) {
            return Optional.of(new MultiSidedInventory(tile1, tile2));
        }

        @Nonnull
        public Optional<IInventory> acceptDouble(@Nonnull CrateTileEntity tile1, @Nonnull CrateTileEntity tile2) {
            return Optional.of(new MultiSidedInventory(tile1, tile2));
        }

        @Nonnull
        public Optional<IInventory> acceptSingle(@Nonnull CrateTileEntity tile) {
            return Optional.of(tile);
        }

        @Nonnull
        public Optional<IInventory> acceptNone() {
            return Optional.empty();
        }
    };

    private static final TileEntityMerger.ICallback<CrateTileEntity, Optional<INamedContainerProvider>> MENU_PROVIDER_COMBINER = new TileEntityMerger.ICallback<CrateTileEntity, Optional<INamedContainerProvider>>() {

        @Nonnull
        public Optional<INamedContainerProvider> acceptDouble(@Nonnull final CrateTileEntity tile1, @Nonnull final CrateTileEntity tile2) {

            final IInventory iinventory = new MultiSidedInventory(tile1, tile2);
            return Optional.of(new INamedContainerProvider() {

                @Nullable
                public Container createMenu(int id, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity player) {
                    if (tile1.canOpen(player) && tile2.canOpen(player)) {
                        tile1.unpackLootTable(playerInventory.player);
                        tile2.unpackLootTable(playerInventory.player);
                        return new CrateContainer(id, playerInventory, iinventory, tile2);
                    } else {
                        return null;
                    }
                }

                @Nonnull
                public ITextComponent getDisplayName() {
                    if (tile1.hasCustomName()) {
                        return tile1.getDisplayName();
                    } else {
                        return tile2.hasCustomName() ? tile2.getDisplayName() : new TranslationTextComponent("container.chestDouble");
                    }
                }

            });
        }

        @Nonnull
        public Optional<INamedContainerProvider> acceptSingle(@Nonnull CrateTileEntity tile) {
            return Optional.of(tile);
        }

        @Nonnull
        public Optional<INamedContainerProvider> acceptNone() {
            return Optional.empty();
        }
    };*/

    public CrateBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(TYPE, CrateType.SMALL));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CrateTileEntity(pos, state);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntity) {
        return createTickerHelper(blockEntity, BlockEntityInit.crate, CrateTileEntity::serverTick);
    }

    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult rayTrace) {
        if (world.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            MenuProvider menuProvider = this.getMenuProvider(state, world, pos);
            if (menuProvider != null) {
                player.openMenu(new MenuProvider() {

                    @Nullable
                    private List<CrateTileEntity> findTileList() {
                        List<CrateTileEntity> tileList = new ArrayList<>(Collections.emptyList());
                        BlockEntity tile = world.getBlockEntity(pos);
                        if (tile instanceof CrateTileEntity) {
                            List<BlockPos> array = ((CrateTileEntity) tile).getCrateArray();
                            if (array == null) {
                                array = CrateBlock.getCratePosList(world, pos);
                            }
                            for (BlockPos tempPos : array) {
                                BlockEntity tempTile = world.getBlockEntity(tempPos);
                                if (tempTile instanceof CrateTileEntity)
                                tileList.add((CrateTileEntity) tempTile);
                            }
                        }
                        if (tileList.isEmpty()) {
                            return null;
                        } else return tileList;
                    }

                    @Override
                    public Component getDisplayName() {
                        List<CrateTileEntity> tileList = findTileList();
                        if (tileList != null) {
                            for (CrateTileEntity crateTileEntity : tileList) {
                                if (crateTileEntity.hasCustomName()) {
                                    return crateTileEntity.getDisplayName();
                                }
                            }
                        }
                        return new TranslatableComponent("container.druidcraftrg.crate");
                    }

                    @Nullable
                    @Override
                    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {

                        List<CrateTileEntity> tileList = findTileList();
                        if (tileList != null) {
                            for (int i = 0; i < 2; i++) {
                                for (CrateTileEntity crateTileEntity : tileList) {
                                    if (i == 0 && !crateTileEntity.canOpen(player)) {
                                        return null;
                                    } else {
                                        crateTileEntity.unpackLootTable(playerInventory.player);
                                    }
                                }
                            }
                            return getSizedContainer(state, id, playerInventory, new MultiSidedInventory(tileList.toArray(new CrateTileEntity[0])));
                        }
                        return null;
                    }
                });

                // player.awardStat(this.getOpenChestStat());
                PiglinAi.angerNearbyPiglins(player, true);
            }

            return InteractionResult.CONSUME;
        }
    }

    public static CrateContainer getSizedContainer(BlockState state, int id, Inventory playerInventory, Container inventory) {
        switch (state.getValue(CrateBlock.TYPE).getCrateSize()) {
            case 2:
                return CrateContainer.doubleCrate(id, playerInventory, inventory);
            case 4:
                return CrateContainer.quadCrate(id, playerInventory, inventory);
            case 8:
                return CrateContainer.octoCrate(id, playerInventory, inventory);
            default:
                return CrateContainer.singleCrate(id, playerInventory, inventory);
        }
    }

    public static List<BlockPos> getCratePosList(Level world, BlockPos pos) {
        List<Direction> directions = getAttachDirectionsFromType(world.getBlockState(pos).getValue(TYPE));
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
        return posList;
    }

    public static boolean isCrateConfigValid(Level world, List<BlockPos> cratePositions) {
        for (BlockPos currentPos : cratePositions) {
            for (Direction dir : getAttachDirectionsFromType(world.getBlockState(currentPos).getValue(TYPE))) {
                if (!getAttachDirectionsFromType(world.getBlockState(currentPos.relative(dir)).getValue(TYPE)).contains(dir.getOpposite())) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean[] createCrateArray(LevelAccessor world, BlockPos pos) {
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
    private CrateType detectCrateConfig(LevelAccessor world, BlockPos pos, boolean[] boolArray, boolean updateConfig) {
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
                            HashMap<BlockPos, CrateType> map = checkConfigValid(world, pos, placeType, ticker);
                            if (map != null) {
                                if (updateConfig) {
                                    map.forEach((blockPos, crateType) -> world.setBlock(blockPos, this.defaultBlockState().setValue(TYPE, crateType), 2));
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

    private boolean[] crateTypeToCrateArray(LevelAccessor world, BlockPos pos, CrateType type) {
        List<Direction> directions = getAttachDirectionsFromType(type);
        boolean[] array = createCrateArray(world, pos);
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
        return array;
    }

    // Triggers when setBlock() happens! Is a problem.
    // TODO: On broken, crates outside of original crate bounds are still connected to, which shouldn't happen.
    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState replacingState, boolean bool) {
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
            BlockEntity tileentity = world.getBlockEntity(pos);
            if (tileentity instanceof Container) {
                Containers.dropContents(world, pos, (Container) tileentity);
            }
        }
        super.onRemove(state, world, pos, replacingState, bool);
    }

    public static List<Direction> getAttachDirectionsFromType(CrateType type) {
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
    private HashMap<BlockPos, CrateType> checkConfigValid(LevelAccessor world, BlockPos pos, CrateType type, int oldTicker) {
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
                        CrateType crateTypeReference = CrateType.values()[ticker]; // Acts as the template that the block needs to fill.
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
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state != null) {
            Level world = context.getLevel();
            BlockPos pos = context.getClickedPos();
            CrateType type = CrateType.SMALL;
            if (context.canPlace()) {
                boolean[] boolArray = createCrateArray(world, pos);
                type = detectCrateConfig(world, pos, boolArray, true);
            }
            return state.setValue(TYPE, type);
        } else return null;
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState state1, boolean bool) {
        // detectCrateConfig(world, pos, createCrateArray(world, pos), true);
        super.onPlace(state, world, pos, state1, bool);
    }

    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {

        List<CrateTileEntity> tileList = new ArrayList<>(Collections.emptyList());
        BlockEntity tile = world.getBlockEntity(pos);
        if (tile instanceof CrateTileEntity) {
            List<BlockPos> array = ((CrateTileEntity) tile).getCrateArray();
            if (array == null) {
                array = CrateBlock.getCratePosList(world, pos);
            }
            for (BlockPos tempPos : array) {
                 BlockEntity tempTile = world.getBlockEntity(tempPos);
                if (tempTile instanceof CrateTileEntity)
                    tileList.add((CrateTileEntity) tempTile);
            }
        }

        return !tileList.isEmpty() ? AbstractContainerMenu.getRedstoneSignalFromContainer(new MultiSidedInventory(tileList.toArray(new CrateTileEntity[0]))) : 0;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TYPE);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}
