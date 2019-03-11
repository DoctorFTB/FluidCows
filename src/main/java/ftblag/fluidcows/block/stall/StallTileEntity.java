package ftblag.fluidcows.block.stall;

import ftblag.fluidcows.entity.EntityFluidCow;
import ftblag.fluidcows.gson.FCConfig;
import ftblag.fluidcows.util.FCUtils;
import ftblag.fluidcows.util.storage.IFluidHelper;
import ftblag.fluidcows.util.storage.IInventoryHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nullable;

public class StallTileEntity extends TileEntity implements IInventoryHelper, IFluidHelper, ITickable {

    public static final int amount = Fluid.BUCKET_VOLUME * 10;

    public NonNullList<ItemStack> inventory = NonNullList.withSize(2, ItemStack.EMPTY);
    public FluidTank tank;

    public boolean hasCow = false;
    @SideOnly(Side.CLIENT)
    public EntityFluidCow cow;
    public Fluid fluid;
    public int cd;
    private NBTTagCompound originalNBT;
    private int lastFluidAmount = -1;

    private boolean lastSync = false;

    public StallTileEntity() {
        tank = new FluidTank(amount) {
            @Override
            protected void onContentsChanged() {
                super.onContentsChanged();
                onTankContentsChanged();
            }
        };
        tank.setTileEntity(this);
        tank.setCanFill(false);
    }

    private void onTankContentsChanged() {
        if(tank.getFluidAmount() != lastFluidAmount) {
            markDirtyClient();
            lastFluidAmount = tank.getFluidAmount();
        }
    }

    public void setEntity(NBTTagCompound tag) {
        hasCow = true;
        fluid = FluidRegistry.getFluid(tag.getString(EntityFluidCow.TYPE_FLUID));
        if (!FCConfig.isEnable(fluid.getName()))
            fluid = FCUtils.getRandFluid();
        if (fluid == null)
            removeEntity();
        else {
            cd = tag.getInteger(EntityFluidCow.TYPE_CD);
            originalNBT = tag;
            StallBlock.update(getWorld(), getPos(), true);
            markDirtyClient();
        }
    }

    public NBTTagCompound removeEntity() {
        hasCow = false;
        NBTTagCompound tag = originalNBT;
        if (fluid != null) {
            tag.setString(EntityFluidCow.TYPE_FLUID, FluidRegistry.getFluidName(fluid));
            tag.setInteger(EntityFluidCow.TYPE_CD, cd);
        }
        originalNBT = null;
        fluid = null;
        cd = 0;
        if (getWorld() != null)
            StallBlock.update(getWorld(), getPos(), false);
        markDirtyClient();
        return tag;
    }

    public void spawnEntity() {
        if (fluid != null) {
            EntityFluidCow cow = new EntityFluidCow(world);
            cow.setPosition(getPos().getX() + .5, getPos().getY(), getPos().getZ() + .5);
            cow.readEntityFromNBT(removeEntity());
            world.spawnEntity(cow);
        }
    }

    @Override
    public void update() {
        if (!getWorld().isRemote) {
            if (cd > 0) {
                cd--;
                lastSync = false;
            }
            if (fluid != null && cd != 0 && cd % 300 == 0) {
                this.world.addBlockEvent(this.pos, this.getBlockType(), 9001, cd);
            } else if (cd == 0 && !lastSync) {
                markDirtyClient();
                lastSync = true;
            }

            if (cd == 0 && fluid != null) {
                if (tank.getFluidAmount() <= amount - Fluid.BUCKET_VOLUME) {
                    if (fillCopy(new FluidStack(fluid, Fluid.BUCKET_VOLUME), true) == Fluid.BUCKET_VOLUME)
                        cd = FCConfig.getStallCD(fluid.getName());
                    markDirtyClient();
                }
            }

            if (getStackInSlot(0).hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null) && getStackInSlot(1).isEmpty() && tank.getFluidAmount() > 0) {
                ItemStack bucket = getStackInSlot(0);
                FluidActionResult result = FluidUtil.tryFillContainer(bucket, this, Fluid.BUCKET_VOLUME, null, true);
                if (result.isSuccess()) {
                    decrStackSize(0, 1);
                    setInventorySlotContents(1, result.getResult());
                    markDirtyClient();
                }
            }
        } else {
            if (cd > 0) {
                cd--;
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
        return index == 0 && stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
    }

    @Override
    public FluidTank getTank() {
        return tank;
    }

    @Override
    public int getAmount() {
        return amount;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        ItemStackHelper.saveAllItems(tag, inventory);
        if (originalNBT != null)
            tag.setTag("originall", originalNBT);
        tag.setTag("tank", tank.writeToNBT(new NBTTagCompound()));
        if (fluid != null) {
            tag.setBoolean("cow", hasCow);
            if (hasCow) {
                tag.setString(EntityFluidCow.TYPE_FLUID, FluidRegistry.getFluidName(fluid));
                tag.setInteger(EntityFluidCow.TYPE_CD, cd);
            }
        }
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        ItemStackHelper.loadAllItems(tag, inventory);
        originalNBT = (NBTTagCompound) tag.getTag("originall");
        tank.readFromNBT(tag.getCompoundTag("tank"));
        tank.setTileEntity(this);
        hasCow = tag.getBoolean("cow");
        if (hasCow) {
            fluid = FluidRegistry.getFluid(tag.getString(EntityFluidCow.TYPE_FLUID));
            cd = tag.getInteger(EntityFluidCow.TYPE_CD);
        } else {
            fluid = null;
            cd = 0;
        }
        if (fluid != null && !FCConfig.isEnable(fluid.getName())) {
            fluid = FCUtils.getRandFluid();
            if (fluid == null)
                removeEntity();
        }
        else if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            cow = hasCow ? new EntityFluidCow(getWorld(), fluid) : null;
        }
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
    public boolean receiveClientEvent(int id, int type) {
        if (!this.world.isRemote)
            return true;

        if (id == 9001)
        {
            cd = type;
        }
        return true;
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
