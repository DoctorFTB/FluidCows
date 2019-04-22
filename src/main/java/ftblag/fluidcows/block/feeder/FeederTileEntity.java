package ftblag.fluidcows.block.feeder;

import com.google.common.base.Predicate;
import ftblag.fluidcows.FluidCows;
import ftblag.fluidcows.entity.EntityFluidCow;
import ftblag.fluidcows.gson.FCConfig;
import ftblag.fluidcows.util.FCUtils;
import ftblag.fluidcows.util.storage.IInventoryHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nullable;
import java.util.List;

public class FeederTileEntity extends TileEntity implements IInventoryHelper, ITickable {

    public static final Predicate<EntityFluidCow> PREDICATE = cow -> !cow.isChild() && cow.getGrowingAge() == 0 && !cow.isInLove() && !FCConfig.feederBlackList.contains(cow.fluid.getName());

    public NonNullList<ItemStack> inventory = NonNullList.withSize(2, ItemStack.EMPTY);

    @Override
    public void update() {
        if (getWorld().isRemote)
            return;

        if (getStackInSlot(0).getCount() < 2)
            return;

        List<EntityFluidCow> cows = getWorld().getEntitiesWithinAABB(EntityFluidCow.class, getWorkArea(), PREDICATE);
        if (cows.size() < 2)
            return;

        frst: for (EntityFluidCow first : cows) {
            for (EntityFluidCow second : cows) {
                if (first.getEntityId() != second.getEntityId() && FCConfig.canMateWith(first, second)) {
                    if (getStackInSlot(0).getCount() >= 2) {
                        if (!first.isInLove() && !second.isInLove()) {
                            decrStackSize(0, 2);
                            first.setInLove(null);
                            second.setInLove(null);
                        }
                    } else {
                        break frst;
                    }
                }
            }
        }
    }

    public AxisAlignedBB getWorkArea() {
        EnumFacing facing = getWorld().getBlockState(getPos()).getValue(FeederBlock.FACING);
        int size = Math.min(getStackInSlot(1).isEmpty() ? 1 : getStackInSlot(1).getCount() * 2, 14);
        return FCUtils.getAABBWithOffset(getPos(), facing, size);
    }

    @Override
    public NonNullList<ItemStack> getInventory() {
        return inventory;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return world.getTileEntity(getPos()) == this && player.getDistanceSq(getPos().getX() + .5D, getPos().getY() + .5D, getPos().getZ() + .5D) <= 64.0D;
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack stack) {
        return (i == 0 && stack.getItem() == Items.WHEAT) || (i == 1 && stack.getItem() == FluidCows.ranger);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        ItemStackHelper.saveAllItems(tag, inventory);
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        ItemStackHelper.loadAllItems(tag, inventory);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(new InvWrapper(this)) : super.getCapability(capability, facing);
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(pos, 0, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        super.onDataPacket(net, packet);
        readFromNBT(packet.getNbtCompound());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }

    public void markDirtyClient() {
        markDirty();
        if (getWorld() != null) {
            IBlockState state = getWorld().getBlockState(getPos());
            getWorld().notifyBlockUpdate(getPos(), state, state, 3);
        }
    }
}
