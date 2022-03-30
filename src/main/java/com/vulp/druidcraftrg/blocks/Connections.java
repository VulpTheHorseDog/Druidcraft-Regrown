package com.vulp.druidcraftrg.blocks;

import net.minecraft.util.StringRepresentable;

public enum Connections implements StringRepresentable {

    NONE("none"),
    NORMAL("normal"),
    CUT("cut");

    private final String name;

    Connections(String string) {
        this.name = string;
    }

    public String toString() {
        return this.getSerializedName();
    }

    public String getSerializedName() {
        return this.name;
    }
}