package ftblag.fluidcows.entity;

import ftblag.fluidcows.FluidCows;
import ftblag.fluidcows.entity.ai.FluidCowAIMate;
import ftblag.fluidcows.gson.FCConfig;
import ftblag.fluidcows.item.ItemCowDisplayer;
import ftblag.fluidcows.util.FCUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.items.ItemHandlerHelper;

public class EntityFluidCow extends EntityCowCopy implements IEntityAdditionalSpawnData {

    public static final String TYPE_FLUID = "t_fluid", TYPE_CD = "t_cd";
    private static final DataParameter<Integer> CD = EntityDataManager.createKey(EntityFluidCow.class, DataSerializers.VARINT);
    public Fluid fluid = FCUtils.getRandFluid();

    public EntityFluidCow(World worldIn) {
        super(worldIn);
        if (fluid != null)
            updateCD(FCConfig.getWorldCD(fluid.getName()));
    }

    public EntityFluidCow(World world, Fluid fluid) {
        super(world);
        this.fluid = fluid;
        updateCD(FCConfig.getWorldCD(fluid.getName()));
    }

    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(2, new FluidCowAIMate(this, 1.0D));
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(CD, 0);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (!getEntityWorld().isRemote && getGrowingAge() >= 0) {
            if (getCD() > 0) {
                updateCD(getCD() - 1);
            }
        }
    }

    public int getCD() {
        return dataManager.get(CD);
    }

    public void updateCD(int newCD) {
        dataManager.set(CD, newCD);
        dataManager.setDirty(CD);
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (stack.isEmpty()) {
            return false;
        }

        if (getCD() == 0 && fluid != null && !isChild()) {
            ItemStack copy = ItemHandlerHelper.copyStackWithSize(stack, 1);
            IFluidHandlerItem fluidItem = FluidUtil.getFluidHandler(copy);
            if (fluidItem != null && fluidItem.fill(new FluidStack(fluid, Fluid.BUCKET_VOLUME), true) == Fluid.BUCKET_VOLUME) {
                player.playSound(SoundEvents.ENTITY_COW_MILK, 1.0F, 1.0F);
                copy = fluidItem.getContainer().copy();
                stack.shrink(1);
                if (stack.isEmpty()) {
                    player.setHeldItem(hand, copy);
                } else if (!player.inventory.addItemStackToInventory(copy)) {
                    player.dropItem(copy, false);
                }
                updateCD(FCConfig.getWorldCD(fluid.getName()));
                return true;
            }
        }

        if (!(player instanceof FakePlayer)) {
            if (FCConfig.breedingItemWork) {
                if (this.isChild() && stack.getItem() == Items.WHEAT) {
                    this.consumeItemFromStack(player, stack);
                    this.ageUp((int) ((float) (-this.getGrowingAge() / 20) * 0.1F), true);
                    return true;
                }
            }

            if (stack.getItem() == Items.WHEAT && getGrowingAge() == 0 && !isInLove()) {
                consumeItemFromStack(player, stack);
                setInLove(player);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return !FCConfig.disableBreedingItemForMachines && stack.getItem() == Items.WHEAT;
    }

    @Override
    public boolean canMateWith(EntityAnimal otherAnimal) {
        return super.canMateWith(otherAnimal) && FCConfig.canMateWith(this, (EntityFluidCow) otherAnimal);
    }

    @Override
    public EntityFluidCow createChild(EntityAgeable ageable) {
        return FCConfig.mateWith(this, (EntityFluidCow) ageable);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (getCD() == 0 && fluid != null && fluid.canBePlacedInWorld()) {
            if (source instanceof EntityDamageSource) {
                EntityDamageSource sour = (EntityDamageSource) source;
                if (sour.damageType.equals("player")) {
                    EntityPlayer pl = (EntityPlayer) sour.getTrueSource();
                    if (!(pl instanceof FakePlayer)) {
                        ItemStack hand = pl.getHeldItemMainhand();
                        if (!hand.isEmpty() && hand.getItem() == Items.STICK) {
                            if (getEntityWorld().isAirBlock(getPosition()) && !getEntityWorld().isRemote) {
                                getEntityWorld().setBlockState(getPosition(), fluid.getBlock().getDefaultState());
                            }
                            updateCD(FCConfig.getWorldCD(fluid.getName()));
                        }
                    }
                }
            }
        }
        return super.attackEntityFrom(source, amount);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        if (fluid != null)
            compound.setString(TYPE_FLUID, FluidRegistry.getFluidName(fluid));
        compound.setInteger(TYPE_CD, getCD());
    }

    public void writeEntityToHalter(NBTTagCompound compound) {
        compound.setString(TYPE_FLUID, FluidRegistry.getFluidName(fluid));
        compound.setInteger(TYPE_CD, getCD());
        compound.setInteger("InLove", this.inLove);
        compound.setInteger("Age", this.getGrowingAge());
        compound.setInteger("ForcedAge", this.forcedAge);
        compound.setFloat("Health", this.getHealth());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        fluid = FluidRegistry.getFluid(compound.getString(TYPE_FLUID));
        if (fluid == null || !FCConfig.isEnable(fluid.getName()))
            fluid = FCUtils.getRandFluid();
        updateCD(compound.getInteger(TYPE_CD));
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        buffer.writeBoolean(fluid != null);
        if (fluid != null)
            ByteBufUtils.writeUTF8String(buffer, FluidRegistry.getFluidName(fluid));
        ByteBufUtils.writeVarInt(buffer, getCD(), 4);
    }

    @Override
    public void readSpawnData(ByteBuf buffer) {
        boolean tmp = buffer.readBoolean();
        if (tmp)
            fluid = FluidRegistry.getFluid(ByteBufUtils.readUTF8String(buffer));
        updateCD(ByteBufUtils.readVarInt(buffer, 4));
    }

    @Override
    public ItemStack getPickedResult(RayTraceResult target) {
        return ItemCowDisplayer.applyFluidToItemStack(new ItemStack(FluidCows.displayer), fluid);
    }
}
