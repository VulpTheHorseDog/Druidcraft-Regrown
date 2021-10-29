package com.vulp.druidcraftrg.client.renderer;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.VertexFormat;

public class RenderTypes extends RenderType {

    public RenderTypes(String string, VertexFormat vertex, int mode, int buffer, boolean affectCrumbling, boolean sortedUpload, Runnable setupState, Runnable clearState) {
        super(string, vertex, mode, buffer, affectCrumbling, sortedUpload, setupState, clearState);
    }

}
