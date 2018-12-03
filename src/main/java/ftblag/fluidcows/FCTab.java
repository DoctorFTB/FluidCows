package ftblag.fluidcows;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class FCTab extends CreativeTabs {

    public static FCTab tab = new FCTab();

    public FCTab() {
        super(FluidCows.MODID);
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(FluidCows.stall);
    }
}
