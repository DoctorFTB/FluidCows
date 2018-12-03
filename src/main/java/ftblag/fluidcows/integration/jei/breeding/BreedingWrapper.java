package ftblag.fluidcows.integration.jei.breeding;

import ftblag.fluidcows.FluidCows;
import ftblag.fluidcows.item.ItemCowDisplayer;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import java.util.Arrays;

public class BreedingWrapper implements IRecipeWrapper {

    ItemStack parentFirst, parentSecond, result;
    int chance;

    public BreedingWrapper(String parentFirst, String parentSecond, Fluid result, int chance) {
        this.parentFirst = ItemCowDisplayer.applyFluidToItemStack(new ItemStack(FluidCows.displayer), FluidRegistry.getFluid(parentFirst));
        this.parentSecond = ItemCowDisplayer.applyFluidToItemStack(new ItemStack(FluidCows.displayer), FluidRegistry.getFluid(parentSecond));
        this.result = ItemCowDisplayer.applyFluidToItemStack(new ItemStack(FluidCows.displayer), result);
        this.chance = chance;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInputs(VanillaTypes.ITEM, Arrays.asList(parentFirst, parentSecond));
        ingredients.setOutput(VanillaTypes.ITEM, result);
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        minecraft.fontRenderer.drawString("Breeding chance: " + chance, 0, 0, 0);
    }
}
