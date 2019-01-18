package ftblag.fluidcows.events;

import ftblag.fluidcows.FluidCows;
import ftblag.fluidcows.util.FCUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/*
 *  Thanks modmuss50
 */
@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(modid = FluidCows.MODID, value = Side.CLIENT)
public class FCClientEvents {

    private static Map<Fluid, Integer> fluidColorMap = new HashMap<>();

    @SubscribeEvent
    public static void color(ColorHandlerEvent.Item e) {
        e.getItemColors().registerItemColorHandler((stack, tintIndex) -> getColorMultiplier(stack), FluidCows.displayer);
    }

    @SubscribeEvent
    public static void textureReload(TextureStitchEvent.Post event) {
        fluidColorMap.clear();
        for (Fluid fluid : FCUtils.getBucketFluids()) {
            fluidColorMap.put(fluid, getColor(fluid));
        }
    }

    public static int getColorMultiplier(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            return 0;
        }
        Fluid fluid = FluidRegistry.getFluid(stack.getTagCompound().getString("fluid"));
        return fluidColorMap.getOrDefault(fluid, 0);
    }

    private static int getColor(Fluid fluid) {
        TextureAtlasSprite icon = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(fluid.getStill().toString());
        int color = fluid.getColor();

        int rgb = 0xFFFFFFFF;
        if (color != rgb) {
            rgb = new Color((color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF, 128).getRGB();
        } else if (icon != Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite() && icon.getFrameTextureData(0) != null) {
            rgb = FCUtils.getColor(icon.getFrameTextureData(0)).getRGB();
        }
        return rgb;
    }
}
