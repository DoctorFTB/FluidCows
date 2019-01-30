package ftblag.fluidcows.integration;

import ftblag.fluidcows.FluidCows;
import lumien.randomthings.item.ItemTimeInABottle;
import net.minecraft.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RandomThingsIntegration {

    public static void reg() {
        MinecraftForge.EVENT_BUS.register(new RandomThingsIntegration());
    }

    @SubscribeEvent
    public void onClick(PlayerInteractEvent.RightClickBlock e) {
        if (e.getItemStack().getItem() instanceof ItemTimeInABottle) {
            Block block = e.getWorld().getBlockState(e.getPos()).getBlock();
            if (block == FluidCows.stall || block == FluidCows.accelerator)
                e.setCanceled(true);
        }
    }
}
