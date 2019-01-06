package ftblag.fluidcows.item;

import ftblag.fluidcows.base.BaseItem;
import ftblag.fluidcows.entity.EntityFluidCow;
import ftblag.fluidcows.gson.FCConfig;
import ftblag.fluidcows.util.FCUtils;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;

public class ItemCowDisplayer extends BaseItem {
    public ItemCowDisplayer() {
        super("cow_displayer");
    }

    public static ItemStack applyFluidToItemStack(ItemStack stack, Fluid fluid) {
        NBTTagCompound tag = stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
        tag.setString("fluid", fluid.getName());
        stack.setTagCompound(tag);
        return stack;
    }

    public static Entity spawnCreature(World worldIn, Fluid fluid, double x, double y, double z) {
        EntityFluidCow entity = new EntityFluidCow(worldIn, fluid);

        entity.setLocationAndAngles(x, y, z, MathHelper.wrapDegrees(worldIn.rand.nextFloat() * 360.0F), 0.0F);
        entity.rotationYawHead = entity.rotationYaw;
        entity.renderYawOffset = entity.rotationYaw;
        entity.onInitialSpawn(worldIn.getDifficultyForLocation(new BlockPos(entity)), null);
        worldIn.spawnEntity(entity);
        entity.playLivingSound();

        return entity;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            for (Fluid fluid : FCUtils.getBucketFluids()) {
                items.add(applyFluidToItemStack(new ItemStack(this), fluid));
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flagIn) {
        if (stack.hasTagCompound() && FCConfig.loaded) {
            String fName = stack.getTagCompound().getString("fluid");
            boolean enable = FCConfig.isEnable(fName);
            if (enable) {
                Fluid fluid = FluidRegistry.getFluid(fName);
                int rate = FCConfig.getRate(fName);
                boolean canBreeding = FCConfig.canBreed.contains(fluid);
                String desc = canBreeding ? rate == 0 ? "Cow can only be breed" : "Cow spawns in the world but can also be breed" : rate != 0 ? "Cow spawns in the world" : enable ? "This cow is crafted" : "NULL";
                tooltip.add("Fluid Name: " + FCUtils.getFluidName(fluid));
                tooltip.add("World cooldown: " + FCConfig.getWorldCD(fName));
                tooltip.add("Stall cooldown: " + FCConfig.getStallCD(fName));
                tooltip.add(TextFormatting.RED + "Spawn: " + desc);
                tooltip.add("Right click to spawn cow");
            }
        }
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("fluid", Constants.NBT.TAG_STRING)) {
            String name = FCUtils.getFluidName(FluidRegistry.getFluid(stack.getTagCompound().getString("fluid")));
            return I18n.translateToLocal("entity.fluidcows.fluidcow.name").trim() + ": " + name;
        }
        return "Error";
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack itemstack = player.getHeldItem(hand);

        if (worldIn.isRemote) {
            return EnumActionResult.SUCCESS;
        } else if (!player.canPlayerEdit(pos.offset(facing), facing, itemstack)) {
            return EnumActionResult.FAIL;
        } else {
            BlockPos blockpos = pos.offset(facing);
            double d0 = this.getYOffset(worldIn, blockpos);
            String fName = itemstack.getTagCompound().getString("fluid");

            if (!FCConfig.isEnable(fName)) {
                player.sendMessage(new TextComponentString("Failed to spawn cow, due to its disabled"));
                return EnumActionResult.FAIL;
            }

            Entity entity = spawnCreature(worldIn, FluidRegistry.getFluid(fName), (double) blockpos.getX() + 0.5D, (double) blockpos.getY() + d0, (double) blockpos.getZ() + 0.5D);

            if (entity != null) {
                if (!player.capabilities.isCreativeMode) {
                    itemstack.shrink(1);
                }
            } else {
                player.sendMessage(new TextComponentString("Failed to spawn cow!"));
                if (!player.capabilities.isCreativeMode) {
                    itemstack.shrink(1);
                }
            }

            return EnumActionResult.SUCCESS;
        }
    }

    protected double getYOffset(World world, BlockPos pos) {
        AxisAlignedBB axisalignedbb = (new AxisAlignedBB(pos)).expand(0.0D, -1.0D, 0.0D);
        List<AxisAlignedBB> list = world.getCollisionBoxes(null, axisalignedbb);

        if (list.isEmpty()) {
            return 0.0D;
        } else {
            double d0 = axisalignedbb.minY;

            for (AxisAlignedBB axisalignedbb1 : list) {
                d0 = Math.max(axisalignedbb1.maxY, d0);
            }

            return d0 - (double) pos.getY();
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);

        if (worldIn.isRemote) {
            return new ActionResult<ItemStack>(EnumActionResult.PASS, itemstack);
        } else {
            RayTraceResult raytraceresult = this.rayTrace(worldIn, playerIn, true);

            if (raytraceresult != null && raytraceresult.typeOfHit == RayTraceResult.Type.BLOCK) {
                BlockPos blockpos = raytraceresult.getBlockPos();

                if (!(worldIn.getBlockState(blockpos).getBlock() instanceof BlockLiquid)) {
                    return new ActionResult<ItemStack>(EnumActionResult.PASS, itemstack);
                } else if (worldIn.isBlockModifiable(playerIn, blockpos) && playerIn.canPlayerEdit(blockpos, raytraceresult.sideHit, itemstack)) {
                    Entity entity = spawnCreature(worldIn, FluidRegistry.getFluid(itemstack.getTagCompound().getString("fluid")), (double) blockpos.getX() + 0.5D, (double) blockpos.getY() + 0.5D, (double) blockpos.getZ() + 0.5D);

                    if (entity == null) {
                        return new ActionResult<ItemStack>(EnumActionResult.PASS, itemstack);
                    } else {
                        if (!playerIn.capabilities.isCreativeMode) {
                            itemstack.shrink(1);
                        }

                        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
                    }
                } else {
                    return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemstack);
                }
            } else {
                return new ActionResult<ItemStack>(EnumActionResult.PASS, itemstack);
            }
        }
    }
}
