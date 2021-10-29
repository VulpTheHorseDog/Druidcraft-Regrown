package com.vulp.druidcraftrg.util;

import net.minecraft.util.Direction;

import javax.annotation.Nullable;
import java.util.Arrays;

public enum DirAxisEnum {

    X(Direction.Axis.X, Direction.EAST, Direction.WEST),
    Y(Direction.Axis.Y, Direction.UP, Direction.DOWN),
    Z(Direction.Axis.Z, Direction.NORTH, Direction.SOUTH);

    private final Direction.Axis axis;
    private final Direction[] directions;

    DirAxisEnum(Direction.Axis axis, Direction... directions) {
        this.axis = axis;
        this.directions = directions;
    }

    public Direction.Axis getAxis() {
        return this.axis;
    }

    public Direction[] getDirections() {
        return this.directions;
    }

    @Nullable
    public static DirAxisEnum getFromAxis(Direction.Axis axis) {
        for (DirAxisEnum value : DirAxisEnum.values()){
            if (value.getAxis() == axis) {
                return value;
            }
        }
        return null;
    }

    @Nullable
    public static DirAxisEnum getFromDirection(Direction dir) {
        for (DirAxisEnum value : DirAxisEnum.values()){
            if (Arrays.stream(value.getDirections()).allMatch(direction -> direction == dir)) {
                return value;
            }
        }
        return null;
    }

}
