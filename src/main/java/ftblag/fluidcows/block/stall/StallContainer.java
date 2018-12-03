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

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index < this.c.getSizeInventory()) {
                if (!this.mergeItemStack(itemstack1, this.c.getSizeInventory(), this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 0, this.c.getSizeInventory(), false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }

        return itemstack;
    }
}
