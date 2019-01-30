package ftblag.fluidcows.integration;

import ftblag.fluidcows.FluidCows;
import mekanism.api.MekanismAPI;
import net.minecraftforge.oredict.OreDictionary;

public class MekanismIntegration {

    public static void reg() {
        MekanismAPI.addBoxBlacklist(FluidCows.stall, OreDictionary.WILDCARD_VALUE);
        MekanismAPI.addBoxBlacklist(FluidCows.accelerator, OreDictionary.WILDCARD_VALUE);
    }
}
