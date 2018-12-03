package ftblag.fluidcows.util.storage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public interface IInventoryHelper extends IInventory {

    NonNullList<ItemStack> getInventory();

    @Override
    default int getSizeInventory() {
        return getInventory().size();
    }

    @Override
    default boolean isEmpty() {
        for (ItemStack itemstack : getInventory()) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    default ItemStack getStackInSlot(int index) {
        return getInventory().get(index);
    }

    @Override
    default ItemStack decrStackSize(int index, int count) {
        ItemStack itemstack = ItemStackHelper.getAndSplit(getInventory(), index, count);
        if (!itemstack.isEmpty()) {
            this.markDirty();
        }
        return itemstack;
    }

    @Override
    default ItemStack removeStackFromSlot(int index) {
        return ItemStackHelper.getAndRemove(getInventory(), index);
    }

    @Override
    default void setInventorySlotContents(int index, ItemStack stack) {
        getInventory().set(index, stack);
        if (stack.getCount() > getInventoryStackLimit()) {
            stack.setCount(getInventoryStackLimit());
        }
        this.markDirty();
    }

    @Override
    default int getInventoryStackLimit() {
        return 64;
    }

    @Override
    default void openInventory(EntityPlayer player) {
    }

    @Override
    default void closeInventory(EntityPlayer player) {
    }

    @Override
    default int getField(int id) {
        return 0;
    }

    @Override
    default void setField(int id, int value) {
    }

    @Override
    default int getFieldCount() {
        return 0;
    }

    @Override
    default void clear() {
        getInventory().clear();
    }

    @Override
    default String getName() {
        return "inventory";
    }

    @Override
    default boolean hasCustomName() {
        return false;
    }
}
