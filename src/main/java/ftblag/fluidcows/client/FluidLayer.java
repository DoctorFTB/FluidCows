package ftblag.fluidcows.client;

import ftblag.fluidcows.entity.EntityFluidCow;
import ftblag.fluidcows.util.FCUtils;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.awt.Color;

import static net.minecraft.client.renderer.GlStateManager.*;

@SideOnly(Side.CLIENT)
public class FluidLayer implements LayerRenderer<EntityFluidCow> {
    private final RenderFluidCow renderer;

    public FluidLayer(RenderFluidCow rendererIn) {
        this.renderer = rendererIn;
    }

    @Override
    public void doRenderLayer(EntityFluidCow entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        Fluid fluid = entitylivingbaseIn.fluid;
        if (fluid == null) {
            return;
        }

        pushMatrix();

        this.renderer.bindTexture(FCUtils.getFluidRL(fluid));

        enableAlpha();
        enableBlend();
        tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

        int rgb = 0xff000000 | fluid.getColor();
        color(((rgb >> 16) & 0xFF) / 255F, ((rgb >> 8) & 0xFF) / 255F, ((rgb >> 0) & 0xFF) / 255F, 0.5F);

//        Color color = new Color(fluid.getColor());
//        color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, 0.5F);

        doPolygonOffset(-3.0F, -3.0F);
        enablePolygonOffset();

        this.renderer.getMainModel().render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        doPolygonOffset(0F, 0F);
        disablePolygonOffset();

        popMatrix();
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}
