package ftblag.fluidcows.base;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

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
}
