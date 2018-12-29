package ftblag.fluidcows.integration;

import com.sci.torcherino.TorcherinoRegistry;
import ftblag.fluidcows.FluidCows;
import ftblag.fluidcows.block.stall.StallTileEntity;

public class TorcherinoIntegration {

    public static void reg() {
        TorcherinoRegistry.blacklistBlock(FluidCows.stall);
        TorcherinoRegistry.blacklistTile(StallTileEntity.class);
    }
}
