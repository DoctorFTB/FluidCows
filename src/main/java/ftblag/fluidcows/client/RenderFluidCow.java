package ftblag.fluidcows.client;

import ftblag.fluidcows.entity.EntityFluidCow;
import net.minecraft.client.model.ModelCow;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

import javax.annotation.Nullable;

public class RenderFluidCow extends RenderLiving<EntityFluidCow> {
    private static final ResourceLocation COW_TEXTURES = new ResourceLocation("textures/entity/cow/cow.png");

    public RenderFluidCow(RenderManager r) {
        super(r, new ModelCow(), 0.7F);
        addLayer(new FluidLayer(this));
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityFluidCow entity) {
        return COW_TEXTURES;
    }

    public static class Factory implements IRenderFactory<EntityFluidCow> {
        @Override
        public Render<? super EntityFluidCow> createRenderFor(RenderManager renderManager) {
            return new RenderFluidCow(renderManager);
        }
    }
}
