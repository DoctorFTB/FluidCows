package ftblag.fluidcows.base;

import ftblag.fluidcows.FCTab;
import ftblag.fluidcows.FluidCows;
import net.minecraft.item.Item;

public class BaseItem extends Item {

    public BaseItem(String name) {
        setRegistryName(FluidCows.MODID, name);
        setTranslationKey(FluidCows.MODID + "." + name);
        setCreativeTab(FCTab.tab);
    }
}
