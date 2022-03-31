package com.vulp.druidcraftrg.client.renderer.tile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.vulp.druidcraftrg.DruidcraftRegrownRegistry;
import com.vulp.druidcraftrg.blocks.Connections;
import com.vulp.druidcraftrg.blocks.RopeBlock;
import com.vulp.druidcraftrg.blocks.tile.BeamTileEntity;
import com.vulp.druidcraftrg.init.BlockInit;
import com.vulp.druidcraftrg.init.ModelLayerInit;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class BeamRopeRenderer implements BlockEntityRenderer<BeamTileEntity> {

    public static final ResourceLocation TEXTURE = DruidcraftRegrownRegistry.location("entity/rope/beam_rope");
    private final BeamRopeModel MODEL;
    private final BlockEntityRendererProvider.Context CONTEXT;

    public BeamRopeRenderer(BlockEntityRendererProvider.Context context) {
        this.MODEL = new BeamRopeModel(context.bakeLayer(ModelLayerInit.BEAM_ROPE_LAYER));
        this.CONTEXT = context;
    }

    @Override
    public void render(BeamTileEntity tileEntity, float v, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        List<Direction> directions = tileEntity.getRopeDirections();
        this.MODEL.calculateVisibility(tileEntity.getLashingAxis(), directions);
        poseStack.pushPose();
        poseStack.translate(0.5D, -0.5D, 0.5D);
        Material material = new Material(TextureAtlas.LOCATION_BLOCKS, TEXTURE);
        VertexConsumer vertexConsumer = material.buffer(bufferSource, this.MODEL::renderType);
        this.MODEL.renderToBuffer(poseStack, vertexConsumer, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
        if (!directions.isEmpty()) {
            BlockState ropeState = BlockInit.rope.defaultBlockState();
            for (Direction ropeDirection : tileEntity.getRopeDirections()) {
                ropeState = ropeState.setValue(RopeBlock.DIR_TO_PROPERTY_MAP.get(ropeDirection), Connections.NORMAL);
            }
            poseStack.translate(-0.5D, 0.5D, -0.5D);
            CONTEXT.getBlockRenderDispatcher().renderSingleBlock(ropeState, poseStack, bufferSource, combinedLight, combinedOverlay);
        }
        poseStack.popPose();
    }

    // TODO: You need to in-code rotate the 'y' model based on the axis, y being for the y axis.
    public static class BeamRopeModel extends Model {
        private final ModelPart lashing;

        public BeamRopeModel(ModelPart root) {
            super(RenderType::entityCutout);
            this.lashing = root.getChild("lashing");
        }

        public static LayerDefinition buildModel() {
            MeshDefinition meshdefinition = new MeshDefinition();
            PartDefinition partdefinition = meshdefinition.getRoot();

            partdefinition.addOrReplaceChild("lashing", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
                    .texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.3F)), PartPose.offset(0.0F, 16.0F, 0.0F));

            return LayerDefinition.create(meshdefinition, 64, 32);
        }

        @Override
        public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
            lashing.render(poseStack, buffer, packedLight, packedOverlay);
        }

        public void setRotationAngle(ModelPart modelRenderer, float x, float y, float z) {
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
        }

    }

}
