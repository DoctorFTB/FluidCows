package ftblag.fluidcows.integration;

import ftblag.fluidcows.FluidCows;
import ftblag.fluidcows.block.stall.StallTileEntity;
import ftblag.fluidcows.entity.EntityFluidCow;
import ftblag.fluidcows.util.FCUtils;
import mcjty.theoneprobe.api.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInterModComms;

import javax.annotation.Nullable;
import java.util.function.Function;

public class TOPIntegration {

    public static void reg() {
        FMLInterModComms.sendFunctionMessage("theoneprobe", "getTheOneProbe", "ftblag.fluidcows.integration.TOPIntegration$Reg");
    }

    public static class Reg implements Function<ITheOneProbe, Void> {
        @Nullable
        @Override
        public Void apply(ITheOneProbe probe) {
            InfoProvider infoProvider = new InfoProvider();
            probe.registerEntityProvider(infoProvider);
            probe.registerProvider(infoProvider);
            return null;
        }
    }

    public static class InfoProvider implements IProbeInfoEntityProvider, IProbeInfoProvider {
        @Override
        public String getID() {
            return FluidCows.MODID + ".fluidcow";
        }

        @Override
        public void addProbeEntityInfo(ProbeMode mode, IProbeInfo info, EntityPlayer player, World world,
                                       Entity entity, IProbeHitEntityData data) {
            if (entity instanceof EntityFluidCow) {
                EntityFluidCow e = (EntityFluidCow) entity;
                info.horizontal().text("Fluid Name: " + e.fluid.getRarity().color + FCUtils.getFluidName(e.fluid));
                info.horizontal().text("Next usage: " + e.fluid.getRarity().color + FCUtils.toTime(e.getCD() / 20, "Now"));
                int age = e.getGrowingAge();
                info.horizontal().text((age < 0 ? "Growing Age: " : "Breeding Time: ") + e.fluid.getRarity().color + (FCUtils.toTime(Math.abs(age / 20), "Ready")));
            }
        }

        @Override
        public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
            TileEntity tile = world.getTileEntity(data.getPos());
            if (tile instanceof StallTileEntity) {
                StallTileEntity te = (StallTileEntity) tile;
                if (te.fluid != null) {
                    probeInfo.horizontal().text("Fluid Name: " + FCUtils.getFluidName(te.fluid));
                    probeInfo.horizontal().text("Next usage: " + te.fluid.getRarity().color + FCUtils.toTime(te.cd / 20, "Now"));
                    probeInfo.horizontal().text("Shift + Right Click to open inventory!");
                } else {
                    probeInfo.horizontal().text("Put cow in Stall with Cow Halter!");
                    probeInfo.horizontal().text("Shift + Right Click with Cow Halter to put it and out");
                }
            }
        }
    }
}