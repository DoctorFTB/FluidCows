package ftblag.fluidcows.util;

import com.google.common.collect.Sets;
import ftblag.fluidcows.FluidCows;
import ftblag.fluidcows.gson.FCConfig;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.awt.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FCUtils {

    private static final int SECONDS_PER_MINUTE = 60, HOURS_PER_DAY = 24, MINUTES_PER_HOUR = 60, SECONDS_PER_HOUR = SECONDS_PER_MINUTE * MINUTES_PER_HOUR, SECONDS_PER_DAY = SECONDS_PER_HOUR * HOURS_PER_DAY;
    public static Map<String, ResourceLocation> fluidRL = new HashMap<>();
    public static Map<String, String> fluidName = new HashMap<>();
    private static Set<String> bucketFluids = ReflectionHelper.getPrivateValue(FluidRegistry.class, null, "bucketFluids");
    public static ItemStack WATER_BOTTLE;
    public static final FluidStack WATER_BOTTLE_STACK = new FluidStack(FluidRegistry.WATER, 250);

    static {
        WATER_BOTTLE = new ItemStack(Items.POTIONITEM);
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("Potion", "minecraft:water");
        WATER_BOTTLE.setTagCompound(tag);
    }

    public static Set<Fluid> getBucketFluids() { // Copy from FluidRegistry#getBucketFluids
        Set<Fluid> currentBucketFluids = null;
        Set<Fluid> tmp = Sets.newHashSet();
        for (String fluidName : bucketFluids) {
            tmp.add(FluidRegistry.getFluid(fluidName));
        }
        currentBucketFluids = Collections.unmodifiableSet(tmp);
        return currentBucketFluids;
    }

    public static Fluid getRandFluid() {
        if (FCConfig.FLUIDS.size() > 0) {
            double search = Math.random() * FCConfig.sumWeight;
            int curr = 0;

            for (Fluid fluid : FCConfig.FLUIDS) {
                curr += FCConfig.getRate(fluid.getName());
                if (curr >= search) {
                    return fluid;
                }
            }
        }
        return null;
    }

    public static String getFluidName(Fluid fluid) {
        if (fluid == null)
            return "ERROR";
        if (!fluidName.containsKey(fluid.getName()))
            fluidName.put(fluid.getName(), fluid.getLocalizedName(new FluidStack(fluid, 0)));
        return fluidName.get(fluid.getName());
    }

    public static ResourceLocation getFluidRL(Fluid fluid) {
        if (!fluidRL.containsKey(fluid.getName()))
            fluidRL.put(fluid.getName(), new ResourceLocation(fluid.getStill().getNamespace(), "textures/" + fluid.getStill().getPath() + ".png"));
        return fluidRL.get(fluid.getName());
    }

    public static Color getColor(int[][] arrays) {
        if (arrays.length == 0) {
            return Color.WHITE;
        }

        int red = 0;
        int green = 0;
        int blue = 0;
        int len = 0;

        for (int[] array : arrays) {
            len += array.length;
            for (int color : array) {
                red += (color >> 16 & 255);
                green += (color >> 8 & 255);
                blue += (color & 255);
            }
        }

        return new Color(red / len, green / len, blue / len, 128);
    }

    public static String toTime(int secondstoAdd, String zero) {
        int newSofd = ((int) (secondstoAdd % SECONDS_PER_DAY) + SECONDS_PER_DAY) % SECONDS_PER_DAY;
        int newHour = newSofd / SECONDS_PER_HOUR;
        int newMinute = (newSofd / SECONDS_PER_MINUTE) % MINUTES_PER_HOUR;
        int newSecond = newSofd % SECONDS_PER_MINUTE;
        if (newHour == 0 && newMinute == 0 && newSecond == 0)
            return zero;
        return newHour != 0 ? String.format("%02d:%02d:%02d", newHour, newMinute, newSecond) : String.format("%02d:%02d", newMinute, newSecond);
    }

    public static ResourceLocation gLoc(String s) {
        return getRL("textures/gui/" + s + ".png");
    }

    public static ResourceLocation getRL(String s) {
        return new ResourceLocation(FluidCows.MODID, s);
    }

    public static AxisAlignedBB getAABBWithOffset(BlockPos pos, EnumFacing facing, int size) {
        AxisAlignedBB aabb = new AxisAlignedBB(pos, pos.add(1, 1, 1));
        aabb = growWithCustomY(aabb.offset(BlockPos.ORIGIN.offset(facing, size + 1)), size);
        return aabb;
    }

    private static AxisAlignedBB growWithCustomY(AxisAlignedBB aabb, int size) {
        double minX = aabb.minX - size;
        double minY = aabb.minY;
        double minZ = aabb.minZ - size;
        double maxX = aabb.maxX + size;
        double maxY = aabb.maxY + size * 2;
        double maxZ = aabb.maxZ + size;
        return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
    }
}
