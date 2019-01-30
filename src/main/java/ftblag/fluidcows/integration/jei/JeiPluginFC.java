package ftblag.fluidcows.integration.jei;

import ftblag.fluidcows.FCTab;
import ftblag.fluidcows.FluidCows;
import ftblag.fluidcows.gson.CustomPair;
import ftblag.fluidcows.gson.FCConfig;
import ftblag.fluidcows.integration.jei.accelerator.AcceleratorCategory;
import ftblag.fluidcows.integration.jei.accelerator.AcceleratorWrapper;
import ftblag.fluidcows.integration.jei.breeding.BreedingCategory;
import ftblag.fluidcows.integration.jei.breeding.BreedingWrapper;
import ftblag.fluidcows.integration.jei.cowresult.CowResultCategory;
import ftblag.fluidcows.integration.jei.cowresult.CowResultWrapper;
import ftblag.fluidcows.util.FCUtils;
import mezz.jei.api.*;
import mezz.jei.api.ingredients.IIngredientBlacklist;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.Fluid;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@JEIPlugin
public class JeiPluginFC implements IModPlugin {

    private static List<BreedingWrapper> getBreeding() {
        List<BreedingWrapper> ret = new ArrayList<>();
        for (Map.Entry<CustomPair<String, String>, List<Fluid>> entry : FCConfig.breed.entrySet()) {
            for (Fluid value : entry.getValue())
                ret.add(new BreedingWrapper(entry.getKey().getLeft(), entry.getKey().getRight(), value, FCConfig.getChance(value.getName())));
        }
        return ret;
    }

    private static List<CowResultWrapper> getCowResult() {
        List<CowResultWrapper> ret = new ArrayList<>();
        for (Fluid fluid : FCUtils.getBucketFluids())
            if (FCConfig.isEnable(fluid.getName()))
                ret.add(new CowResultWrapper(fluid.getName()));
        return ret;
    }

    private static List<AcceleratorWrapper> getAccelerators() {
        List<AcceleratorWrapper> ret = new ArrayList<>();
        ret.add(new AcceleratorWrapper(true));
        ret.add(new AcceleratorWrapper(false));
        return ret;
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {
        subtypeRegistry.useNbtForSubtypes(FluidCows.displayer);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
        registry.addRecipeCategories(new BreedingCategory(guiHelper), new CowResultCategory(guiHelper), new AcceleratorCategory(guiHelper));
    }

    @Override
    public void register(IModRegistry registry) {
        registry.addRecipes(getBreeding(), BreedingCategory.UID);

        registry.addRecipes(getCowResult(), CowResultCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(FluidCows.stall), CowResultCategory.UID);

        registry.addRecipes(getAccelerators(), AcceleratorCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(FluidCows.accelerator), AcceleratorCategory.UID);

        NonNullList<ItemStack> list;
        FluidCows.displayer.getSubItems(FCTab.tab, list = NonNullList.create());
        IIngredientBlacklist blacklist = registry.getJeiHelpers().getIngredientBlacklist();
        for (ItemStack stack : list) {
            String fName = stack.getTagCompound().getString("fluid");
            boolean enable = FCConfig.isEnable(fName);
            if (!enable)
                blacklist.addIngredientToBlacklist(stack);
        }
    }
}
