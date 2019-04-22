package ftblag.fluidcows.block.feeder;

import ftblag.fluidcows.FluidCows;
import ftblag.fluidcows.base.BaseContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class FeederContainer extends BaseContainer {
    public FeederContainer(EntityPlayer p, IInventory c) {
        super(p, c);

        addSlotToContainer(new Slot(c, 0, 80, 21) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return stack.getItem() == Items.WHEAT;
            }
        });
        addSlotToContainer(new Slot(c, 1, 152, 33) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return stack.getItem() == FluidCows.ranger;
            }
        });

        addPS(p, 8, 56);
    }
}
