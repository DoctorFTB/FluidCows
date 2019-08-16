package ftblag.fluidcows.entity;

import ftblag.fluidcows.FluidCows;
import ftblag.fluidcows.entity.ai.FluidCowAIMate;
import ftblag.fluidcows.gson.FCConfig;
import ftblag.fluidcows.item.ItemCowDisplayer;
import ftblag.fluidcows.item.ItemCowHalter;
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
import net.minecraft.util.text.TextComponentString;
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

    boolean first = true;

    private boolean alreadyGrowth = false;
    private int cooldown = -1;

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
        alreadyGrowth = false;
        if (getGrowingAge() >= 0 && getCD() > 0) {
            cooldown--;
        }
        if (!getEntityWorld().isRemote) {
            if (first || (cooldown != 0 && cooldown % 300 == 0)) {
                syncCD();
                first = false;
            }
        }
    }

    @Override
    public boolean getCanSpawnHere() {
        return super.getCanSpawnHere() && !FCConfig.blackListDimIds.contains(world.provider.getDimension());
    }

    public boolean growTicks() {
        if (!getEntityWorld().isRemote) {
            int age = getGrowingAge();
            if (!alreadyGrowth && age < 0) {
                age--;
                age = Math.min(age + FCConfig.acceleratorMultiplier, 0);
                setGrowingAge(age);
                if (age == 0) {
                    onGrowingAdult();
                }
                alreadyGrowth = true;
                return true;
            }
        }
        return false;
    }
    public int getCD() {
        return cooldown;
    }

    public void updateCD(int newCD) {
        cooldown = newCD;
    }

    public void syncCD()
    {
        dataManager.set(CD, cooldown);
        dataManager.setDirty(CD);
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        if (CD.equals(key))
        {
            cooldown = dataManager.get(CD);
        }
        super.notifyDataManagerChange(key);
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
            if (fluidItem != null) {
                int fill = fluidItem.fill(new FluidStack(fluid, Fluid.BUCKET_VOLUME), true);
                if (fill == Fluid.BUCKET_VOLUME) {
                    player.playSound(SoundEvents.ENTITY_COW_MILK, 1.0F, 1.0F);
                    copy = fluidItem.getContainer().copy();
                    stack.shrink(1);
                    if (stack.isEmpty()) {
                        player.setHeldItem(hand, copy);
                    } else if (!player.inventory.addItemStackToInventory(copy)) {
                        player.dropItem(copy, false);
                    }
                    updateCD(FCConfig.getWorldCD(fluid.getName()));
                    syncCD();
                    return true;
                }
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
        if (getCD() == 0 && fluid != null) {
            if (source instanceof EntityDamageSource) {
                EntityDamageSource sour = (EntityDamageSource) source;
                if (sour.damageType.equals("player")) {
                    EntityPlayer pl = (EntityPlayer) sour.getTrueSource();
                    if (!(pl instanceof FakePlayer)) {
                        ItemStack hand = pl.getHeldItemMainhand();
                        if (!hand.isEmpty()) {
                            if (hand.getItem() == Items.STICK) {
                                if (fluid.canBePlacedInWorld()) {
                                    if (getEntityWorld().isAirBlock(getPosition()) && !getEntityWorld().isRemote) {
                                        getEntityWorld().setBlockState(getPosition(), fluid.getBlock().getDefaultState());
                                    }
                                    updateCD(FCConfig.getWorldCD(fluid.getName()));
                                    syncCD();
                                } else {
                                    pl.sendMessage(new TextComponentString("This fluid not supported!"));
                                }
                            } else if (hand.getItem() == FluidCows.halter) {
                                hand.getItem().hitEntity(hand, this, pl);
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return super.attackEntityFrom(source, amount);
    }

    @Override
    public String getName() {
        String name = super.getName();
        if (fluid != null) {
            String fName = FCUtils.getFluidName(fluid);
            name += " (" + fName + ")";
        }
        return name;
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
        if (fluid == null)
            setDead();
        updateCD(compound.getInteger(TYPE_CD));
        syncCD();
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        if (fluid != null) {
            String str = FluidRegistry.getFluidName(fluid);
            if (str != null) {
                buffer.writeBoolean(true);
                ByteBufUtils.writeUTF8String(buffer, FluidRegistry.getFluidName(fluid));
            } else {
                buffer.writeBoolean(false);
            }
        } else {
            buffer.writeBoolean(false);
        }
        ByteBufUtils.writeVarInt(buffer, getCD(), 5);
    }

    @Override
    public void readSpawnData(ByteBuf buffer) {
        boolean tmp = buffer.readBoolean();
        if (tmp)
            fluid = FluidRegistry.getFluid(ByteBufUtils.readUTF8String(buffer));
        updateCD(ByteBufUtils.readVarInt(buffer, 5));
    }

    @Override
    public ItemStack getPickedResult(RayTraceResult target) {
        return ItemCowDisplayer.applyFluidToItemStack(new ItemStack(FluidCows.displayer), fluid);
    }
}
