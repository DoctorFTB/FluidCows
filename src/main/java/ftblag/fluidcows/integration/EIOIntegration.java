package ftblag.fluidcows.integration;

import crazypants.enderio.base.recipe.spawner.EntityDataRegistry;
import ftblag.fluidcows.FluidCows;
import ftblag.fluidcows.gson.FCConfig;
import net.minecraft.util.ResourceLocation;

import java.util.function.Predicate;

public class EIOIntegration {

    private static ResourceLocation IDENT = new ResourceLocation(FluidCows.MODID, "ident");
    private static ResourceLocation COW = new ResourceLocation(FluidCows.MODID, "fluidcow");
    private static Predicate<ResourceLocation> PRED = (in) -> in.equals(COW);

    public static void reg() {
        EntityDataRegistry.getInstance().addEntityData(IDENT, PRED, FCConfig.EIOEntityCostMultiplier, FCConfig.EIOBlackListSpawning, FCConfig.EIOBlackListSoulVial, FCConfig.EIONeedsCloning);
    }
}
