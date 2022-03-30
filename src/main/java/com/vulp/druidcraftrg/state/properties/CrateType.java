package com.vulp.druidcraftrg.state.properties;

import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum CrateType implements StringRepresentable {

    // 2x2x2
    LARGE_NORTH_WEST_DOWN("large_north_west_down", 8, Direction.SOUTH, Direction.EAST, Direction.UP),
    LARGE_SOUTH_WEST_DOWN("large_south_west_down", 8, Direction.NORTH, Direction.EAST, Direction.UP),
    LARGE_NORTH_EAST_DOWN("large_north_east_down", 8, Direction.SOUTH, Direction.WEST, Direction.UP),
    LARGE_SOUTH_EAST_DOWN("large_south_east_down", 8, Direction.NORTH, Direction.WEST, Direction.UP),
    LARGE_NORTH_WEST_UP("large_north_west_up", 8, Direction.SOUTH, Direction.EAST, Direction.DOWN),
    LARGE_SOUTH_WEST_UP("large_south_west_up", 8, Direction.NORTH, Direction.EAST, Direction.DOWN),
    LARGE_NORTH_EAST_UP("large_north_east_up", 8, Direction.SOUTH, Direction.WEST, Direction.DOWN),
    LARGE_SOUTH_EAST_UP("large_south_east_up", 8, Direction.NORTH, Direction.WEST, Direction.DOWN),

    // 2x2x1
    MEDIUM_WEST_DOWN("medium_west_down", 4, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.UP),
    MEDIUM_EAST_DOWN("medium_east_down", 4, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.UP),
    MEDIUM_WEST_UP("medium_west_up", 4, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.DOWN),
    MEDIUM_EAST_UP("medium_east_up", 4, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.DOWN),

    MEDIUM_NORTH_DOWN("medium_north_down", 4, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.UP),
    MEDIUM_SOUTH_DOWN("medium_south_down", 4, Direction.NORTH, Direction.EAST, Direction.WEST, Direction.UP),
    MEDIUM_NORTH_UP("medium_north_up", 4, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.DOWN),
    MEDIUM_SOUTH_UP("medium_south_up", 4, Direction.NORTH, Direction.EAST, Direction.WEST, Direction.DOWN),

    MEDIUM_NORTH_WEST("medium_north_west", 4, Direction.SOUTH, Direction.EAST, Direction.UP, Direction.DOWN),
    MEDIUM_SOUTH_WEST("medium_south_west", 4, Direction.NORTH, Direction.EAST, Direction.UP, Direction.DOWN),
    MEDIUM_NORTH_EAST("medium_north_east", 4, Direction.SOUTH, Direction.WEST, Direction.UP, Direction.DOWN),
    MEDIUM_SOUTH_EAST("medium_south_east", 4, Direction.NORTH, Direction.WEST, Direction.UP, Direction.DOWN),

    // 2x1x1
    MEDIUM_NORTH("medium_north", 2, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.UP, Direction.DOWN),
    MEDIUM_SOUTH("medium_south", 2, Direction.NORTH, Direction.EAST, Direction.WEST, Direction.UP, Direction.DOWN),

    MEDIUM_WEST("medium_west", 2, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.UP, Direction.DOWN),
    MEDIUM_EAST("medium_east", 2, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.UP, Direction.DOWN),

    MEDIUM_DOWN("medium_down", 2, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.UP),
    MEDIUM_UP("medium_up", 2, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.DOWN),

    // 1x1x1
    SMALL("small", 1, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.UP, Direction.DOWN);
    
    private final String name;
    private final int crate_size;
    private final Direction[] open_directions;

    CrateType(String name, int crateSize, Direction... directions) {
        this.name = name;
        this.crate_size = crateSize;
        this.open_directions = directions;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public Direction[] getOpenDirections() {
        return this.open_directions;
    }

    public int getCrateSize() {
        return crate_size;
    }

    public static List<CrateType> typeListFromDirection(Direction dir) {
        List<CrateType> types = new java.util.ArrayList<>(Collections.emptyList());
        for (CrateType type : CrateType.values()) {
            if (Arrays.stream(type.getOpenDirections()).anyMatch(direction -> direction == dir)) {
                types.add(type);
            }
        }
        return types;
    }

}
