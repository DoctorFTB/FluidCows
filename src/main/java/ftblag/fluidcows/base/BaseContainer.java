package ftblag.fluidcows.base;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public abstract class BaseContainer extends Container {

    protected final IInventory c;

    public BaseContainer(EntityPlayer p, IInventory c) {
        this.c = c;
    }

    protected void addPS(EntityPlayer p, int xS, int yS) {
        addPS(p, xS, yS, 58);
    }

    protected void addPS(EntityPlayer p, int xS, int yS, int yChange) {
        for (int i = 0; i < 3; ++i) {
            for (int k = 0; k < 9; ++k) {
                addSlotToContainer(new Slot(p.inventory, k + i * 9 + 9, xS + k * 18, i * 18 + yS));
            }
        }
        for (int i = 0; i < 9; ++i) {
            addSlotToContainer(new Slot(p.inventory, i, xS + i * 18, yS + yChange));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer p) {
        return c.isUsableByPlayer(p);
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
