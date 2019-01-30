package ftblag.fluidcows.block.accelerator;

import ftblag.fluidcows.base.BaseContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

public class AcceleratorContainer extends BaseContainer {
    public AcceleratorContainer(EntityPlayer p, IInventory c) {
        super(p, c);

        addSlotToContainer(new Slot(c, 0, 81, 35) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return stack.getItem() == Items.WHEAT;
            }
        });
        addSlotToContainer(new Slot(c, 1, 33, 6) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                FluidStack fluid = FluidUtil.getFluidContained(stack);
                return fluid != null && fluid.getFluid() == FluidRegistry.WATER;
//                return stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null) &&
//                        stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null).drain(Integer.MAX_VALUE, false).getFluid() == FluidRegistry.WATER;
            }
        });
        addSlotToContainer(new Slot(c, 2, 33, 64) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return false;
            }
        });

        addPS(p, 8, 84);
    }
}
