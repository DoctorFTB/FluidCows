package ftblag.fluidcows.integration;

import ftblag.fluidcows.block.accelerator.AcceleratorTileEntity;
import ftblag.fluidcows.block.stall.StallTileEntity;
import moze_intel.projecte.gameObjs.items.TimeWatch;

public class ProjectEIntegration {

    public static void reg() {
        TimeWatch.blacklist(StallTileEntity.class);
        TimeWatch.blacklist(AcceleratorTileEntity.class);
    }
}
