package ftblag.fluidcows.network;

import ftblag.fluidcows.FluidCows;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class NetworkHandler {

    public static SimpleNetworkWrapper network;

    public static void reg() {
        network = NetworkRegistry.INSTANCE.newSimpleChannel(FluidCows.MODID);
        network.registerMessage(PacketAddInfo.Handler.class, PacketAddInfo.class, 0, Side.SERVER);
    }
}
