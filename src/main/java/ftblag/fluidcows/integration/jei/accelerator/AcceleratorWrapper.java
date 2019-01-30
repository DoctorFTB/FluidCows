package ftblag.fluidcows.integration.jei.accelerator;

import ftblag.fluidcows.gson.FCConfig;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class AcceleratorWrapper implements IRecipeWrapper {

    boolean hasWater;
    String chance;
    ItemStack inputItems;
    FluidStack inputFluids;

    public AcceleratorWrapper(boolean hasWater) {
        this.hasWater = hasWater;
        int middle = FCConfig.acceleratorMax / 2;
        this.chance = hasWater ? middle + "-" + FCConfig.acceleratorMax : "0-" + middle;
        inputItems = new ItemStack(Items.WHEAT);
        inputFluids = new FluidStack(FluidRegistry.WATER, FCConfig.acceleratorWater);
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInput(VanillaTypes.ITEM, inputItems);
        if (hasWater)
            ingredients.setInput(VanillaTypes.FLUID, inputFluids);
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        minecraft.fontRenderer.drawString(chance, 68, 37, 0);
    }
}
