package ftblag.fluidcows.base;

import ftblag.fluidcows.FCTab;
import ftblag.fluidcows.FluidCows;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BaseBlock extends Block {
    public BaseBlock(Material materialIn, String name) {
        super(materialIn);
        setRegistryName(FluidCows.MODID, name);
        setTranslationKey(FluidCows.MODID + "." + name);
        setCreativeTab(FCTab.tab);
    }
}
