package ftblag.fluidcows.integration.jei.accelerator;

import ftblag.fluidcows.FluidCows;
import ftblag.fluidcows.block.accelerator.AcceleratorTileEntity;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class AcceleratorCategory implements IRecipeCategory<AcceleratorWrapper> {

    public static final String UID = FluidCows.MODID + ".accelerator";
    private static final ResourceLocation location = new ResourceLocation(FluidCows.MODID, "textures/gui/accelerator.png");
    private final IDrawableStatic background;
    private final IDrawableAnimated arrow;
    private final IDrawable overlay;

    public AcceleratorCategory(IGuiHelper guiHelper) {
        background = guiHelper.createDrawable(location, 0, 0, 85, 82);
        IDrawableStatic arrowDrawable = guiHelper.createDrawable(location, 103, 0, 24, 17);
        arrow = guiHelper.createAnimatedDrawable(arrowDrawable, 200, IDrawableAnimated.StartDirection.LEFT, false);
        overlay = guiHelper.createDrawable(location, 86, 1, 16, 74);
    }

    @Override
    public String getUid() {
        return UID;
    }

    @Override
    public String getTitle() {
        return "Accelerator substance convert";
    }

    @Override
    public String getModName() {
        return FluidCows.NAME;
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public void drawExtras(Minecraft minecraft) {
        arrow.draw(minecraft, 44, 32);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, AcceleratorWrapper recipeWrapper, IIngredients ingredients) {
        IGuiFluidStackGroup fluids = recipeLayout.getFluidStacks();
        IGuiItemStackGroup items = recipeLayout.getItemStacks();

        items.init(0, true, 24, 32);
        items.set(ingredients);
        fluids.init(1, true, 4, 4, 16, 74, AcceleratorTileEntity.fluidAmount, false, overlay);
        if (recipeWrapper.hasWater) {
            fluids.set(ingredients);
        }
    }
}
