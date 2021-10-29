package com.vulp.druidcraftrg.client.renderer.tile;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.vulp.druidcraftrg.DruidcraftRegrownRegistry;
import com.vulp.druidcraftrg.blocks.tile.BeamTileEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public class BeamRopeRenderer extends TileEntityRenderer<BeamTileEntity> {

        public static final ResourceLocation TEXTURE = DruidcraftRegrownRegistry.location("entity/rope/beam_rope");
        private final BeamRopeModel MODEL = new BeamRopeModel();

        public BeamRopeRenderer(TileEntityRendererDispatcher rendererDispatcher) {
            super(rendererDispatcher);
        }

        @Override
        public void render(BeamTileEntity tileEntity, float v, MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int i, int i1) {
            this.MODEL.calculateVisibility(tileEntity.getLashingAxis(), tileEntity.getRopeDirections());
            matrixStack.pushPose();
            matrixStack.translate(0.5D, -0.5D, 0.5D);
            RenderMaterial material = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS, TEXTURE);
            IVertexBuilder iVertexBuilder = material.buffer(iRenderTypeBuffer, this.MODEL::renderType);
            this.MODEL.renderToBuffer(matrixStack, iVertexBuilder, i, i1, 1.0F, 1.0F, 1.0F, 1.0F);
            matrixStack.popPose();
        }

        // TODO: You need to in-code rotate the 'y' model based on the axis, y being for the y axis.
        public static class BeamRopeModel extends Model {
            private final ModelRenderer lashing;
            private final ModelRenderer west;
            private final ModelRenderer up;
            private final ModelRenderer north;
            private final ModelRenderer east;
            private final ModelRenderer south;
            private final ModelRenderer down;

            public BeamRopeModel() {
                super(RenderType::entityCutout);
                texWidth = 64;
                texHeight = 32;

                lashing = new ModelRenderer(this);
                lashing.setPos(0.0F, 16.0F, 0.0F);
                lashing.texOffs(0, 16).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.0F, false);
                lashing.texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.3F, false);

                east = new ModelRenderer(this);
                east.setPos(-8.0F, 16.0F, 8.0F);
                east.texOffs(44, 15).addBox(12.0F, -1.0F, -9.0F, 4.0F, 2.0F, 2.0F, 0.0F, false);
                east.texOffs(44, 11).addBox(12.5F, -1.0F, -9.0F, 3.0F, 2.0F, 2.0F, 0.5F, false);

                down = new ModelRenderer(this);
                down.setPos(-8.0F, 16.0F, 8.0F);
                down.texOffs(32, 24).addBox(7.0F, -8.0F, -9.0F, 2.0F, 4.0F, 2.0F, 0.0F, false);
                down.texOffs(32, 19).addBox(7.0F, -7.5F, -9.0F, 2.0F, 3.0F, 2.0F, 0.5F, false);

                north = new ModelRenderer(this);
                north.setPos(-8.0F, 16.0F, 8.0F);
                north.texOffs(32, 5).addBox(7.0F, -1.0F, -16.0F, 2.0F, 2.0F, 4.0F, 0.0F, false);
                north.texOffs(32, 0).addBox(7.0F, -1.0F, -15.5F, 2.0F, 2.0F, 3.0F, 0.5F, false);

                west = new ModelRenderer(this);
                west.setPos(-8.0F, 16.0F, 8.0F);
                west.texOffs(32, 15).addBox(0.0F, -1.0F, -9.0F, 4.0F, 2.0F, 2.0F, 0.0F, true);
                west.texOffs(32, 11).addBox(0.5F, -1.0F, -9.0F, 3.0F, 2.0F, 2.0F, 0.5F, true);

                south = new ModelRenderer(this);
                south.setPos(-8.0F, 16.0F, 8.0F);
                south.texOffs(44, 5).addBox(7.0F, -1.0F, -4.0F, 2.0F, 2.0F, 4.0F, 0.0F, false);
                south.texOffs(44, 0).addBox(7.0F, -1.0F, -3.5F, 2.0F, 2.0F, 3.0F, 0.5F, false);

                up = new ModelRenderer(this);
                up.setPos(-8.0F, 16.0F, 8.0F);
                up.texOffs(40, 24).addBox(7.0F, 4.0F, -9.0F, 2.0F, 4.0F, 2.0F, 0.0F, false);
            }

            @Override
            public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
                lashing.render(matrixStack, buffer, packedLight, packedOverlay);
                west.render(matrixStack, buffer, packedLight, packedOverlay);
                up.render(matrixStack, buffer, packedLight, packedOverlay);
                north.render(matrixStack, buffer, packedLight, packedOverlay);
                east.render(matrixStack, buffer, packedLight, packedOverlay);
                south.render(matrixStack, buffer, packedLight, packedOverlay);
                down.render(matrixStack, buffer, packedLight, packedOverlay);
            }

            public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
                modelRenderer.xRot = x;
                modelRenderer.yRot = y;
                modelRenderer.zRot = z;
            }

            public void calculateVisibility(Direction.Axis axis, List<Direction> directions) {
                this.lashing.visible = true;
                float rotate90 = (float) (Math.PI * 2.0F) * 0.25F;
                if (axis == Direction.Axis.X) {
                    this.setRotationAngle(lashing, 0.0F, 0.0F, rotate90);

                } else if (axis == Direction.Axis.Y) {
                    this.setRotationAngle(lashing, 0.0F, 0.0F, 0.0F);

                } else if (axis == Direction.Axis.Z) {
                    this.setRotationAngle(lashing, rotate90, 0.0F, 0.0F);

                } else {
                    this.lashing.visible = false;
                }
                this.north.visible = directions.contains(Direction.NORTH);
                this.east.visible = directions.contains(Direction.EAST);
                this.south.visible = directions.contains(Direction.SOUTH);
                this.west.visible = directions.contains(Direction.WEST);
                this.up.visible = directions.contains(Direction.UP);
                this.down.visible = directions.contains(Direction.DOWN);
            }

        }

}
