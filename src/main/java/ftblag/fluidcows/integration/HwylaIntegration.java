package ftblag.fluidcows.integration;

import ftblag.fluidcows.block.stall.StallTileEntity;
import ftblag.fluidcows.entity.EntityFluidCow;
import ftblag.fluidcows.util.FCUtils;
import mcp.mobius.waila.api.*;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLInterModComms;

import javax.annotation.Nonnull;
import java.util.List;

public class HwylaIntegration {

    public static void reg() {
        FMLInterModComms.sendFunctionMessage("waila", "register", "ftblag.fluidcows.integration.HwylaIntegration.reg");
    }

    public static void reg(IWailaRegistrar registrar) {
        registrar.registerBodyProvider(new InfoEntityProvider(), EntityFluidCow.class);
        registrar.registerBodyProvider(new InfoProvider(), StallTileEntity.class);
    }

    public static class InfoEntityProvider implements IWailaEntityProvider {
        @Nonnull
        @Override
        public List<String> getWailaBody(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor, IWailaConfigHandler config) {
            if (accessor.getEntity() instanceof EntityFluidCow) {
                EntityFluidCow e = (EntityFluidCow) accessor.getEntity();
                currenttip.add("Fluid Name: " + FCUtils.getFluidName(e.fluid));
                currenttip.add("Next usage: " + e.fluid.getRarity().color + FCUtils.toTime(e.getCD() / 20));
            }
            return currenttip;
        }
    }

    public static class InfoProvider implements IWailaDataProvider {
        @Nonnull
        @Override
        public List<String> getWailaBody(ItemStack itemStack, List<String> tooltip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
            if (accessor.getTileEntity() instanceof StallTileEntity) {
                StallTileEntity te = (StallTileEntity) accessor.getTileEntity();
                if (te.fluid != null) {
                    tooltip.add("Fluid Name: " + FCUtils.getFluidName(te.fluid));
                    tooltip.add("Next usage: " + te.fluid.getRarity().color + FCUtils.toTime(te.cd / 20));
                    tooltip.add("Shift + Right Click to open inventory!");
                } else {
                    tooltip.add("Put cow in Stall with Cow Halter!");
                    tooltip.add("Shift + Right Click to put it and out");
                }
            }
            return tooltip;
        }
    }
}
