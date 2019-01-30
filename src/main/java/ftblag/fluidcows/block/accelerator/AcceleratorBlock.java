package ftblag.fluidcows.block.accelerator;

import ftblag.fluidcows.FluidCows;
import ftblag.fluidcows.base.BaseBlock;
import ftblag.fluidcows.gson.FCConfig;
import ftblag.fluidcows.util.FCUtils;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nullable;

public class AcceleratorBlock extends BaseBlock implements ITileEntityProvider {

    public AcceleratorBlock() {
        super(Material.ROCK, "accelerator");
        setHardness(2);
        setHarvestLevel("pickaxe", 1);
        GameRegistry.registerTileEntity(AcceleratorTileEntity.class, "accelerator_te");
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            ItemStack stack = player.getHeldItem(hand);
            AcceleratorTileEntity te = (AcceleratorTileEntity) world.getTileEntity(pos);
            if (!stack.isEmpty()) {
                if (stack.getItem() == Items.POTIONITEM && ItemStack.areItemStackTagsEqual(stack, FCUtils.WATER_BOTTLE)) {
                    if (te.getTank().fill(FCUtils.WATER_BOTTLE_STACK.copy(), false) == 250) {
                        if (player.addItemStackToInventory(new ItemStack(Items.GLASS_BOTTLE))) {
                            stack.shrink(1);
                            te.getTank().fill(FCUtils.WATER_BOTTLE_STACK.copy(), true);
                            te.markDirtyClient();
                            return true;
                        }
                    }
                }
            }
            if (FluidUtil.interactWithFluidHandler(player, hand, te)) {
                te.markDirtyClient();
                return true;
            }
            player.openGui(FluidCows.instance, 2, world, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new AcceleratorTileEntity();
    }
}
