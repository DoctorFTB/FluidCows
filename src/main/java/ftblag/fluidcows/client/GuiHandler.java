package ftblag.fluidcows.client;

import ftblag.fluidcows.block.accelerator.AcceleratorContainer;
import ftblag.fluidcows.block.accelerator.AcceleratorGui;
import ftblag.fluidcows.block.accelerator.AcceleratorTileEntity;
import ftblag.fluidcows.block.feeder.FeederContainer;
import ftblag.fluidcows.block.feeder.FeederGui;
import ftblag.fluidcows.block.feeder.FeederTileEntity;
import ftblag.fluidcows.block.sorter.SorterContainer;
import ftblag.fluidcows.block.sorter.SorterGui;
import ftblag.fluidcows.block.sorter.SorterTileEntity;
import ftblag.fluidcows.block.stall.StallContainer;
import ftblag.fluidcows.block.stall.StallGui;
import ftblag.fluidcows.block.stall.StallTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nullable;

public class GuiHandler implements IGuiHandler {

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity te = world.getTileEntity(pos);
        if (te == null)
            return null;
        if (ID == 1 && te instanceof StallTileEntity)
            return new StallGui(player, (IInventory) te);
        else if (ID == 2 && te instanceof AcceleratorTileEntity)
            return new AcceleratorGui(player, (IInventory) te);
        else if (ID == 3 && te instanceof FeederTileEntity)
            return new FeederGui(player, (IInventory) te);
        else if (ID == 4 && te instanceof SorterTileEntity)
            return new SorterGui(player, (IInventory) te);
        return null;
    }

    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity te = world.getTileEntity(pos);
        if (te == null)
            return null;
        if (ID == 1 && te instanceof StallTileEntity)
            return new StallContainer(player, (IInventory) te);
        else if (ID == 2 && te instanceof AcceleratorTileEntity)
            return new AcceleratorContainer(player, (IInventory) te);
        else if (ID == 3 && te instanceof FeederTileEntity)
            return new FeederContainer(player, (IInventory) te);
        else if (ID == 4 && te instanceof SorterTileEntity)
            return new SorterContainer(player, (IInventory) te);
        return null;
    }
}
