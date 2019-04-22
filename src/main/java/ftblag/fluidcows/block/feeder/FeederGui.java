package ftblag.fluidcows.block.feeder;

import ftblag.fluidcows.base.BaseGui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;

public class FeederGui extends BaseGui {
    public FeederGui(EntityPlayer p, IInventory c) {
        super(new FeederContainer(p, c), "feeder", 176, 138);
    }
}
