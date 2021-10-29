package com.vulp.druidcraftrg.client.renderer.tile;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.vulp.druidcraftrg.DruidcraftRegrownRegistry;
import com.vulp.druidcraftrg.blocks.tile.RopeTileEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;

public class RopeKnotRenderer extends TileEntityRenderer<RopeTileEntity> {

    public static final ResourceLocation TEXTURE = DruidcraftRegrownRegistry.location("entity/rope/rope_knot");
    private final RopeKnotModel MODEL = new RopeKnotModel();

    public RopeKnotRenderer(TileEntityRendererDispatcher rendererDispatcher) {
        super(rendererDispatcher);
    }

    @Override
    public void render(RopeTileEntity tileEntity, float v, MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int i, int i1) {
        matrixStack.pushPose();
        matrixStack.translate(0.5D, -0.5D, 0.5D);
        this.MODEL.knot.visible = tileEntity.hasKnot();
        RenderMaterial material = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS, TEXTURE);
        IVertexBuilder iVertexBuilder = material.buffer(iRenderTypeBuffer, this.MODEL::renderType);
        this.MODEL.renderToBuffer(matrixStack, iVertexBuilder, i, i1, 1.0F, 1.0F, 1.0F, 1.0F);
        matrixStack.popPose();
    }

    public static class RopeKnotModel extends Model {

        private final ModelRenderer knot;

        public RopeKnotModel() {
            super(RenderType::entityCutout);
            texWidth = 16;
            texHeight = 16;

            knot = new ModelRenderer(this);
            knot.setPos(0.0F, 16.0F, 0.0F);
            knot.texOffs(0, 8).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, 0.5F, false);
            knot.texOffs(0, 0).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, 0.8F, false);
        }

        @Override
        public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder vertexBuilder, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
            knot.render(matrixStack, vertexBuilder, packedLight, packedOverlay, red, green, blue, alpha);
        }
    }

}
