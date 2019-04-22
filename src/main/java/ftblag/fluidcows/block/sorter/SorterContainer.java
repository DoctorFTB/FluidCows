package ftblag.fluidcows.block.sorter;

import ftblag.fluidcows.FluidCows;
import ftblag.fluidcows.base.BaseContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import java.util.Iterator;

public class SorterContainer extends BaseContainer {

    private SorterTileEntity te;

    public SorterContainer(EntityPlayer p, IInventory c) {
        super(p, c);
        te = (SorterTileEntity) c;

        addSlotToContainer(new Slot(c, 0, 152, 33) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return stack.getItem() == FluidCows.ranger;
            }
        });

        addPS(p, 8, 56);
    }

    public void go(String fName, boolean black, int remove) {
        if (black) {
            te.isBlackList = !te.isBlackList;
            te.markDirtyClient();
        } else {
            if (remove > 0 && remove <= 5) {
                Iterator<String> iterator = te.filter.iterator();
                int i = 0;
                while (iterator.hasNext()) {
                    iterator.next();
                    i++;
                    if (i == remove) {
                        iterator.remove();
                        te.markDirtyClient();
                        break;
                    }
                }
            }
            if (fName != null) {
                if (te.filter.size() < 5) {
                    te.filter.add(fName);
                    te.markDirtyClient();
                }
            }
        }
    }
}
