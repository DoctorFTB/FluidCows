package ftblag.fluidcows.block.stall;

import ftblag.fluidcows.base.BaseContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

public class StallContainer extends BaseContainer {

    public StallContainer(EntityPlayer p, IInventory c) {
        super(p, c);

        addSlotToContainer(new Slot(c, 0, 50, 35) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
            }
        });

        addSlotToContainer(new Slot(c, 1, 112, 35) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return false;
            }
        });

        addPS(p, 8, 84);
    }
}
