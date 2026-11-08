package com.moos2333.moreswordarts.client;

import com.moos2333.moreswordarts.entity.EntityAerialSlashBeam;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderAerialSlashBeam extends Render<EntityAerialSlashBeam> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("moreswordarts:textures/entities/aerial_slash_beam.png");

    public RenderAerialSlashBeam(RenderManager renderManager) {
        super(renderManager);
        this.shadowSize = 0.25F;
        this.shadowOpaque = 0.75F;
    }

    @Override
    public void doRender(EntityAerialSlashBeam entity, double x, double y, double z, float yaw, float partialTick) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.disableCull();
        GlStateManager.enableBlend();
        GlStateManager.disableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.scale(1.5F, 0.5F, 1.5F);

        this.bindTexture(this.getEntityTexture(entity));

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        GlStateManager.rotate(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F);

        buffer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
        buffer.pos(-0.5F, -0.25F, 0.0F).tex(0.0F, 1.0F).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.pos(0.5F, -0.25F, 0.0F).tex(1.0F, 1.0F).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.pos(0.5F, 0.75F, 0.0F).tex(1.0F, 0.0F).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.pos(-0.5F, 0.75F, 0.0F).tex(0.0F, 0.0F).normal(0.0F, 1.0F, 0.0F).endVertex();
        tessellator.draw();

        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityAerialSlashBeam entity) {
        return TEXTURE;
    }

    public static class Factory implements IRenderFactory<EntityAerialSlashBeam> {
        @Override
        public Render<? super EntityAerialSlashBeam> createRenderFor(RenderManager manager) {
            return new RenderAerialSlashBeam(manager);
        }
    }
}