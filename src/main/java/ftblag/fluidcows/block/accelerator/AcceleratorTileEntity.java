package ftblag.fluidcows.block.accelerator;

import ftblag.fluidcows.entity.EntityFluidCow;
import ftblag.fluidcows.gson.FCConfig;
import ftblag.fluidcows.util.storage.IFluidHelper;
import ftblag.fluidcows.util.storage.IInventoryHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
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
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import scala.collection.Map;

import javax.annotation.Nullable;
import java.util.List;

public class AcceleratorTileEntity extends TileEntity implements IInventoryHelper, IFluidHelper, ITickable {

    public static final int fluidAmount = Fluid.BUCKET_VOLUME * 10;
    public static final int maxSubstance = Fluid.BUCKET_VOLUME * 10;

    public NonNullList<ItemStack> inventory = NonNullList.withSize(3, ItemStack.EMPTY);
    public FluidTank tank;
    public int currentWheatSubstance;
    private int lastFluidAmount = -1;

    public AcceleratorTileEntity() {
        tank = new FluidTank(fluidAmount) {
            @Override
            protected void onContentsChanged() {
                super.onContentsChanged();
                onTankContentsChanged();
            }
        };
        tank.setTileEntity(this);
    }

    private void onTankContentsChanged() {
        if(tank.getFluidAmount() != lastFluidAmount) {
            markDirtyClient();
            lastFluidAmount = tank.getFluidAmount();
        }
    }

    @Override
    public void update() {
        if (getWorld().isRemote)
            return;
        if (getStackInSlot(1).hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null) && getStackInSlot(2).isEmpty() && tank.getFluidAmount() + 1000 <= fluidAmount) {
            ItemStack bucket = getStackInSlot(1);
            FluidActionResult result = FluidUtil.tryEmptyContainer(bucket, this, Fluid.BUCKET_VOLUME, null, true);
            if (result.isSuccess()) {
                decrStackSize(1, 1);
                setInventorySlotContents(2, result.getResult());
                markDirtyClient();
            }
        }
        if (getWorld().isBlockPowered(getPos()))
            return;
        if (!getStackInSlot(0).isEmpty()) {
            if (currentWheatSubstance + FCConfig.acceleratorMax <= maxSubstance) {

                int toAdd = Math.min(getStackInSlot(0).getCount(), 8);
                if (toAdd > 0) {
                    int middle = FCConfig.acceleratorMax / 2;

                    boolean hasWater;
                    if (hasWater = tank.getFluidAmount() >= FCConfig.acceleratorWater)
                        toAdd = Math.min(tank.getFluidAmount() / FCConfig.acceleratorWater, Math.min(getStackInSlot(0).getCount(), 32));

                    int addedMax = hasWater ? toAdd * FCConfig.acceleratorMax : toAdd * middle;

                    if (currentWheatSubstance + addedMax > maxSubstance) {
                        toAdd = currentWheatSubstance + (hasWater ? FCConfig.acceleratorMax : middle) <= maxSubstance ? 1 : 0;
                    }

                    if (toAdd > 0) {
                        if (hasWater) {
                            tank.drain(toAdd * FCConfig.acceleratorWater, true);
                            currentWheatSubstance += toAdd * middle;
                        }

                        decrStackSize(0, toAdd);
                        currentWheatSubstance += getWorld().rand.nextInt(toAdd * middle) + 1;
                        markDirtyClient();
                    }
                }
            }
        }

        if (currentWheatSubstance > FCConfig.acceleratorPerCow) {
            List<EntityFluidCow> cows = getWorld().getEntitiesWithinAABB(EntityFluidCow.class, new AxisAlignedBB(getPos()).grow(FCConfig.acceleratorRadius));
            if (!cows.isEmpty()) {
                for (EntityFluidCow cow : cows) {
                    if (cow.growTicks()) {
                        currentWheatSubstance -= FCConfig.acceleratorPerCow;
                        markDirtyClient();
                        if (currentWheatSubstance < FCConfig.acceleratorPerCow)
                            break;
                    }
                }
            }
        }
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
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (index == 0)
            return stack.getItem() == Items.WHEAT;
        else if (index == 2) {
            FluidStack fluid = FluidUtil.getFluidContained(stack);
            return fluid != null && fluid.getFluid() == FluidRegistry.WATER;
        } else
            return false;
    }

    @Override
    public FluidTank getTank() {
        return tank;
    }

    @Override
    public int getAmount() {
        return fluidAmount;
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        return resource.getFluid() == FluidRegistry.WATER ? getTank().fill(resource, doFill) : 0;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        ItemStackHelper.saveAllItems(tag, inventory);
        tag.setTag("tank", tank.writeToNBT(new NBTTagCompound()));
        tag.setInteger("substance", currentWheatSubstance);
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        ItemStackHelper.loadAllItems(tag, inventory);
        tank.readFromNBT(tag.getCompoundTag("tank"));
        tank.setTileEntity(this);
        currentWheatSubstance = tag.getInteger("substance");
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(new InvWrapper(this)) : capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY ? CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(tank) : super.getCapability(capability, facing);
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
