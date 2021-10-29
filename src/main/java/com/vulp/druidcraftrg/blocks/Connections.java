package com.vulp.druidcraftrg.blocks;

import net.minecraft.util.IStringSerializable;

public enum Connections implements IStringSerializable {

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