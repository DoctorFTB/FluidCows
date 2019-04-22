package ftblag.fluidcows.block.sorter;

import com.google.common.base.Predicate;
import ftblag.fluidcows.FluidCows;
import ftblag.fluidcows.entity.EntityFluidCow;
import ftblag.fluidcows.util.FCUtils;
import ftblag.fluidcows.util.storage.IInventoryHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
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
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SorterTileEntity extends TileEntity implements IInventoryHelper, ITickable {

    public static final Predicate<EntityFluidCow> PREDICATE = EntityFluidCow::isChild;

    public NonNullList<ItemStack> inventory = NonNullList.withSize(1, ItemStack.EMPTY);
    public boolean isBlackList;
    public Set<String> filter = new HashSet<>(5);

    @Override
    public void update() {
        if (getWorld().isRemote)
            return;

        List<EntityFluidCow> cows = getWorld().getEntitiesWithinAABB(EntityFluidCow.class, getWorkArea(), PREDICATE);
        if (cows.size() <= 0)
            return;

        for (EntityFluidCow cow : cows) {
            if ((!isBlackList && filter.contains(cow.fluid.getName())) || (isBlackList && !filter.contains(cow.fluid.getName()))) {
                BlockPos newPos = pos.offset(getWorld().getBlockState(getPos()).getValue(SorterBlock.FACING));
                cow.setPositionAndUpdate(newPos.getX() + .5, newPos.getY() + .5, newPos.getZ() + .5);
            }
        }
    }

    public AxisAlignedBB getWorkArea() {
        EnumFacing facing = getWorld().getBlockState(getPos()).getValue(SorterBlock.FACING);
        int size = Math.min(getStackInSlot(0).isEmpty() ? 1 : getStackInSlot(0).getCount() * 2, 14);
        return FCUtils.getAABBWithOffset(getPos(), facing.getOpposite(), size);
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
        return stack.getItem() == FluidCows.ranger;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        ItemStackHelper.saveAllItems(tag, inventory);
        tag.setBoolean("black", isBlackList);
        NBTTagList list = new NBTTagList();
        for (String fName : filter) {
            list.appendTag(new NBTTagString(fName));
        }
        tag.setTag("filter", list);
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        ItemStackHelper.loadAllItems(tag, inventory);
        isBlackList = tag.getBoolean("black");
        filter.clear();
        NBTTagList list = tag.getTagList("filter", Constants.NBT.TAG_STRING);
        for (int i = 0; i < list.tagCount() && i < 5; i++) {
            String str = list.getStringTagAt(i);
            if (FluidRegistry.isFluidRegistered(str)) {
                filter.add(str);
            }
        }
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
