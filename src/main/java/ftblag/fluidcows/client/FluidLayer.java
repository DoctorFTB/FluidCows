package ftblag.fluidcows.client;

import ftblag.fluidcows.entity.EntityFluidCow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import static net.minecraft.client.renderer.GlStateManager.*;

@SideOnly(Side.CLIENT)
public class FluidLayer implements LayerRenderer<EntityFluidCow> {
    private final RenderFluidCow renderer;

    private static final ModelFluidCow modelCow = new ModelFluidCow();

    public FluidLayer(RenderFluidCow rendererIn) {
        this.renderer = rendererIn;
    }

    @Override
    public void doRenderLayer(EntityFluidCow entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        Fluid fluid = entitylivingbaseIn.fluid;
        if (fluid == null) {
            return;
        }

        TextureAtlasSprite icon = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(fluid.getStill().toString());

        pushMatrix();

        renderer.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        enableAlpha();
        enableBlend();
        tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

        int rgb = 0xff000000 | fluid.getColor();
        color(((rgb >> 16) & 0xFF) / 255F, ((rgb >> 8) & 0xFF) / 255F, ((rgb >> 0) & 0xFF) / 255F, 0.5F);

        doPolygonOffset(-3.0F, -3.0F);
        enablePolygonOffset();


        GlStateManager.matrixMode(GL11.GL_TEXTURE);
        pushMatrix();
        loadIdentity();


        translate(icon.getMinU(), icon.getMinV(), 0);
        scale(icon.getMaxU()-icon.getMinU(), icon.getMaxV()-icon.getMinV(), 1);

        GlStateManager.matrixMode(GL11.GL_MODELVIEW);

        modelCow.isChild = renderer.getMainModel().isChild;
        /*this.renderer.getMainModel()*/modelCow.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);


        GlStateManager.matrixMode(GL11.GL_TEXTURE);
        GlStateManager.loadIdentity();
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);


        doPolygonOffset(0F, 0F);
        disablePolygonOffset();

        popMatrix();
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}
