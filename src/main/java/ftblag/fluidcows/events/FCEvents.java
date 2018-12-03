package ftblag.fluidcows.events;

import ftblag.fluidcows.FluidCows;
import ftblag.fluidcows.item.ItemCowDisplayer;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber(modid = FluidCows.MODID, value = Side.CLIENT)
public class FCEvents {

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void color(ColorHandlerEvent.Item e) {
        e.getItemColors().registerItemColorHandler((stack, tintIndex) -> ItemCowDisplayer.getColorMultiplier(stack), FluidCows.displayer);
    }
}
