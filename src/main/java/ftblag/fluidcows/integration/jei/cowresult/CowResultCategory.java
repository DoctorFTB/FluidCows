package ftblag.fluidcows.integration.jei.cowresult;

import ftblag.fluidcows.FluidCows;
import ftblag.fluidcows.block.stall.StallTileEntity;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.*;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class CowResultCategory implements IRecipeCategory<CowResultWrapper> {

    public static final String UID = FluidCows.MODID + ".cowresult";
    private static final ResourceLocation location = new ResourceLocation(FluidCows.MODID, "textures/gui/cowresult.png");
    private final IDrawableStatic background;
    private final IDrawableAnimated arrow;
    private final IDrawable overlay;

    public CowResultCategory(IGuiHelper guiHelper) {
        background = guiHelper.createDrawable(location, 0, 0, 70, 82);
        IDrawableStatic arrowDrawable = guiHelper.createDrawable(location, 88, 0, 24, 17);
        arrow = guiHelper.createAnimatedDrawable(arrowDrawable, 200, IDrawableAnimated.StartDirection.LEFT, false);
        overlay = guiHelper.createDrawable(location, 71, 1, 16, 74);
    }

    @Override
    public String getUid() {
        return UID;
    }

    @Override
    public String getTitle() {
        return "Cow Result";
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
        arrow.draw(minecraft, 23, 31);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, CowResultWrapper recipeWrapper, IIngredients ingredients) {
        IGuiFluidStackGroup fluids = recipeLayout.getFluidStacks();
        IGuiItemStackGroup items = recipeLayout.getItemStacks();

        items.init(0, true, 3, 32);
        items.set(ingredients);
        fluids.init(1, false, 50, 4, 16, 74, StallTileEntity.amount, false, overlay);
        fluids.set(ingredients);
    }
}
