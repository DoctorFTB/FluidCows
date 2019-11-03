package ftblag.fluidcows.item;

import ftblag.fluidcows.FluidCows;
import ftblag.fluidcows.base.BaseItem;
import ftblag.fluidcows.block.stall.StallTileEntity;
import ftblag.fluidcows.entity.EntityFluidCow;
import ftblag.fluidcows.gson.FCConfig;
import ftblag.fluidcows.util.FCUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemCowHalter extends BaseItem {
    public ItemCowHalter() {
        super("cow_halter");
        setMaxStackSize(1);
    }

    public static boolean constCow(ItemStack stack) {
        return stack.getTagCompound() != null && stack.getTagCompound().hasKey(EntityFluidCow.TYPE_FLUID) && stack.getTagCompound().hasKey(EntityFluidCow.TYPE_CD);
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase entity, EnumHand hand) {
        if (!(entity instanceof EntityFluidCow) || player.world.isRemote || hand == EnumHand.OFF_HAND || constCow(stack) || player.dimension != entity.dimension)
            return false;
        if (((EntityFluidCow) entity).getGrowingAge() < 0)
            return false;
        if (entity.isDead) {
            return false;
        }
        ItemStack halter = new ItemStack(this);
        NBTTagCompound tag = new NBTTagCompound();
        ((EntityFluidCow) entity).writeEntityToHalter(tag);
        halter.setTagCompound(tag);
        if (!player.inventory.addItemStackToInventory(halter)) {
            player.dropItem(halter, false);
        }
        player.setActiveHand(hand);
        player.world.removeEntity(entity);
        stack.shrink(1);
        return true;
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (world.isRemote || hand == EnumHand.OFF_HAND)
            return EnumActionResult.FAIL;
        ItemStack stack = player.getHeldItem(hand);
        if (world.getBlockState(pos).getBlock() == FluidCows.stall) {
            StallTileEntity te = (StallTileEntity) world.getTileEntity(pos);
            if (constCow(stack) && te.fluid == null) {
                te.setEntity(stack.getTagCompound());
                stack.setTagCompound(null);
                return EnumActionResult.SUCCESS;
            } else if (!constCow(stack) && te.fluid != null) {
                stack.setTagCompound(te.removeEntity());
                return EnumActionResult.SUCCESS;
            }
        } else if (constCow(stack)) {
            BlockPos spawn = pos.offset(facing);
            EntityFluidCow cow = new EntityFluidCow(world);
            cow.setPositionAndRotation(spawn.getX() + .5, spawn.getY() + .5, spawn.getZ() + .5, Math.abs(player.rotationYaw), 0);
            cow.readEntityFromNBT(stack.getTagCompound());
            world.spawnEntity(cow);
            stack.setTagCompound(null);
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.FAIL;
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase player) {
        if (player instanceof EntityPlayer) {
            if (target instanceof EntityFluidCow) {
                if (FCConfig.enableConvertCowToDisplayer) {
                    EntityFluidCow cow = (EntityFluidCow) target;
                    if (cow.getGrowingAge() < 0) {
                        player.sendMessage(new TextComponentString("You cant convert child cow."));
                    } else if (!FCConfig.blackListCowToDisplayer.contains(cow.fluid.getName())) {
                        ItemStack displayer = ItemCowDisplayer.applyFluidToItemStack(new ItemStack(FluidCows.displayer), cow.fluid);
                        if (((EntityPlayer) player).inventory.addItemStackToInventory(displayer)) {
                            cow.setDead();
                            return true;
                        }
                    } else {
                        player.sendMessage(new TextComponentString("You cant convert fluid from cow."));
                    }
                } else {
                    player.sendMessage(new TextComponentString("This feature disabled."));
                }
            }
        }

        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if (constCow(stack)) {
            Fluid fluid = FluidRegistry.getFluid(stack.getTagCompound().getString(EntityFluidCow.TYPE_FLUID));
            tooltip.add("Fluid name: " + FCUtils.getFluidName(fluid));
            tooltip.add("Next usage: " + FCUtils.toTime(stack.getTagCompound().getInteger(EntityFluidCow.TYPE_CD) / 20, "Now"));
        } else {
            tooltip.add("Empty. Right Click on cow to pick it up!");
        }
        tooltip.add(TextFormatting.RED + "!Warning!");
        tooltip.add("Left Click on cow to convert cow to displayer!");
        tooltip.add(TextFormatting.RED + "!Warning!");
    }
}
