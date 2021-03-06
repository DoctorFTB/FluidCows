package ftblag.fluidcows.integration;

import ftblag.fluidcows.FluidCows;
import net.minecraft.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import romelo333.notenoughwands.Items.AccelerationWand;

public class NotEnoughWandsIntegration {

    public static void reg() {
        MinecraftForge.EVENT_BUS.register(new NotEnoughWandsIntegration());
    }

    @SubscribeEvent
    public void onClick(PlayerInteractEvent.RightClickBlock e) {
        if (e.getItemStack().getItem() instanceof AccelerationWand) {
            Block block = e.getWorld().getBlockState(e.getPos()).getBlock();
            if (block == FluidCows.stall || block == FluidCows.accelerator)
                e.setCanceled(true);
        }
    }
}
