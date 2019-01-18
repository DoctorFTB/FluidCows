package ftblag.fluidcows.events;

import ftblag.fluidcows.FluidCows;
import ftblag.fluidcows.entity.EntityFluidCow;
import net.minecraft.entity.passive.EntityCow;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = FluidCows.MODID)
public class FCCommonEvents {

    @SubscribeEvent
    public static void onSpawn(EntityJoinWorldEvent e) {
        if (e.getEntity() instanceof EntityFluidCow && !e.getWorld().isRemote) {
            if (((EntityFluidCow) e.getEntity()).fluid == null) {
                EntityCow cow = new EntityCow(e.getWorld());
                cow.setPosition(e.getEntity().posX, e.getEntity().posY, e.getEntity().posZ);
                e.getEntity().setDead();
                e.getWorld().spawnEntity(cow);
            }
        }
    }
}
