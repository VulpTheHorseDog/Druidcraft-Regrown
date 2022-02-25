package com.vulp.druidcraftrg.state.properties;

import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public enum CrateType implements IStringSerializable {

    LARGE_NORTH_WEST_DOWN("large_north_west_down", Direction.SOUTH, Direction.EAST, Direction.UP),
    LARGE_SOUTH_WEST_DOWN("large_south_west_down", Direction.NORTH, Direction.EAST, Direction.UP),
    LARGE_NORTH_EAST_DOWN("large_north_east_down", Direction.SOUTH, Direction.WEST, Direction.UP),
    LARGE_SOUTH_EAST_DOWN("large_south_east_down", Direction.NORTH, Direction.WEST, Direction.UP),
    LARGE_NORTH_WEST_UP("large_north_west_up", Direction.SOUTH, Direction.EAST, Direction.DOWN),
    LARGE_SOUTH_WEST_UP("large_south_west_up", Direction.NORTH, Direction.EAST, Direction.DOWN),
    LARGE_NORTH_EAST_UP("large_north_east_up", Direction.SOUTH, Direction.WEST, Direction.DOWN),
    LARGE_SOUTH_EAST_UP("large_south_east_up", Direction.NORTH, Direction.WEST, Direction.DOWN),
    MEDIUM_WEST("medium_west", Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.UP, Direction.DOWN),
    MEDIUM_EAST("medium_east", Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.UP, Direction.DOWN),
    MEDIUM_SOUTH("medium_south", Direction.NORTH, Direction.EAST, Direction.WEST, Direction.UP, Direction.DOWN),
    MEDIUM_NORTH("medium_north", Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.UP, Direction.DOWN),
    MEDIUM_UP("medium_up", Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.DOWN),
    MEDIUM_DOWN("medium_down", Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.UP),
    MEDIUM_WEST_DOWN("medium_west_down", Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.UP),
    MEDIUM_EAST_DOWN("medium_east_down", Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.UP),
    MEDIUM_WEST_UP("medium_west_up", Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.DOWN),
    MEDIUM_EAST_UP("medium_east_up", Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.DOWN),
    MEDIUM_SOUTH_WEST("medium_south_west", Direction.NORTH, Direction.EAST, Direction.UP, Direction.DOWN),
    MEDIUM_SOUTH_EAST("medium_south_east", Direction.NORTH, Direction.WEST, Direction.UP, Direction.DOWN),
    MEDIUM_NORTH_WEST("medium_north_west", Direction.SOUTH, Direction.EAST, Direction.UP, Direction.DOWN),
    MEDIUM_NORTH_EAST("medium_north_east", Direction.SOUTH, Direction.WEST, Direction.UP, Direction.DOWN),
    MEDIUM_SOUTH_DOWN("medium_south_down", Direction.NORTH, Direction.EAST, Direction.WEST, Direction.UP),
    MEDIUM_NORTH_DOWN("medium_north_down", Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.UP),
    MEDIUM_SOUTH_UP("medium_south_up", Direction.NORTH, Direction.EAST, Direction.WEST, Direction.DOWN),
    MEDIUM_NORTH_UP("medium_north_up", Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.DOWN),
    SMALL("small", Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.UP, Direction.DOWN);
    
    private final String name;
    private final Direction[] open_directions;

    CrateType(String name, Direction... directions) {
        this.name = name;
        this.open_directions = directions;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public Direction[] getOpenDirections() {
        return this.open_directions;
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
