package ftblag.fluidcows.integration.jei.breeding;

import ftblag.fluidcows.FluidCows;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.util.ResourceLocation;

public class BreedingCategory implements IRecipeCategory<BreedingWrapper> {

    public static final String UID = FluidCows.MODID + ".breeding";
    private static final ResourceLocation location = new ResourceLocation(FluidCows.MODID, "textures/gui/breeding.png");
    private final IDrawableStatic background;

    public BreedingCategory(IGuiHelper guiHelper) {
        background = guiHelper.createDrawable(location, 0, 0, 106, 28);
    }

    @Override
    public String getUid() {
        return UID;
    }

    @Override
    public String getTitle() {
        return "Cow breeding";
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
    public void setRecipe(IRecipeLayout recipeLayout, BreedingWrapper recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup items = recipeLayout.getItemStacks();

        items.init(0, true, 12, 8);
        items.set(ingredients);

        items.init(1, true, 44, 8);
        items.set(ingredients);

        items.init(2, false, 76, 8);
        items.set(ingredients);
    }
}
