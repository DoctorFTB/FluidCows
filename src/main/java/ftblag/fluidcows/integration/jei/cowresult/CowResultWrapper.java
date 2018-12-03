package ftblag.fluidcows.integration.jei.cowresult;

import ftblag.fluidcows.FluidCows;
import ftblag.fluidcows.item.ItemCowDisplayer;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class CowResultWrapper implements IRecipeWrapper {

    ItemStack cow;
    FluidStack fStack;

    public CowResultWrapper(String fName) {
        Fluid f = FluidRegistry.getFluid(fName);
        cow = ItemCowDisplayer.applyFluidToItemStack(new ItemStack(FluidCows.displayer), f);
        fStack = new FluidStack(f, Fluid.BUCKET_VOLUME);
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInput(VanillaTypes.ITEM, cow);
        ingredients.setOutput(VanillaTypes.FLUID, fStack);
    }
}
