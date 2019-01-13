package ftblag.fluidcows;

import ftblag.fluidcows.block.stall.StallBlock;
import ftblag.fluidcows.block.stall.StallTileEntity;
import ftblag.fluidcows.client.GuiHandler;
import ftblag.fluidcows.client.RenderFluidCow;
import ftblag.fluidcows.client.RenderStallTile;
import ftblag.fluidcows.entity.EntityFluidCow;
import ftblag.fluidcows.gson.FCConfig;
import ftblag.fluidcows.integration.HwylaIntegration;
import ftblag.fluidcows.integration.MekanismIntegration;
import ftblag.fluidcows.integration.NotEnoughWandsIntegration;
import ftblag.fluidcows.integration.ProjectEIntegration;
import ftblag.fluidcows.integration.TOPIntegration;
import ftblag.fluidcows.integration.TorcherinoIntegration;
import ftblag.fluidcows.item.ItemCowDisplayer;
import ftblag.fluidcows.item.ItemCowHalter;
import ftblag.fluidcows.util.FCUtils;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(modid = FluidCows.MODID, name = FluidCows.NAME, version = FluidCows.VERSION, dependencies = "after:*;required-after:forge@[14.23.5.2768,);")
public class FluidCows {

    public static final String MODID = "fluidcows", NAME = "Fluid Cows", VERSION = "@VERSION@";

    private static Logger log;
    public static StallBlock stall;
    public static ItemCowHalter halter;
    public static ItemCowDisplayer displayer;

    @Mod.Instance
    public static FluidCows instance;

    static {
        FluidRegistry.enableUniversalBucket();
        FluidRegistry.addBucketForFluid(FluidRegistry.WATER);
        FluidRegistry.addBucketForFluid(FluidRegistry.LAVA);
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        log = e.getModLog();
        FCConfig.setFile(new File(e.getModConfigurationDirectory(), MODID + "_v2.json"));
        if (Loader.isModLoaded("theoneprobe")) {
            TOPIntegration.reg();
        }
        if (Loader.isModLoaded("waila")) {
            HwylaIntegration.reg();
        }
        EntityRegistry.registerModEntity(new ResourceLocation(MODID, "fluidcow"), EntityFluidCow.class, MODID + "." + "fluidcow", 0, this, 64, 1, true);
//        EntityRegistry.addSpawn(EntityFluidCow.class, 8, 4, 4, EnumCreatureType.CREATURE, ForgeRegistries.BIOMES.getValues().toArray(new Biome[0]));
        ForgeRegistries.BLOCKS.register(stall = new StallBlock());
        ForgeRegistries.ITEMS.registerAll(new ItemBlock(stall).setRegistryName(stall.getRegistryName()), halter = new ItemCowHalter(), displayer = new ItemCowDisplayer());
        GameRegistry.addShapedRecipe(new ResourceLocation(MODID, "stall"), null, new ItemStack(stall), "B B", "BHB", "GGG", 'B', Blocks.IRON_BARS, 'H', Blocks.HAY_BLOCK, 'G', new ItemStack(Blocks.CONCRETE, 1, 7));
        GameRegistry.addShapedRecipe(new ResourceLocation(MODID, "halter"), null, new ItemStack(halter), "  L", " S ", "S  ", 'L', Items.LEATHER, 'S', Items.STICK);
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            preInit();
        }
    }

    @SideOnly(Side.CLIENT)
    public void preInit() {
        RenderingRegistry.registerEntityRenderingHandler(EntityFluidCow.class, new RenderFluidCow.Factory());
        ClientRegistry.bindTileEntitySpecialRenderer(StallTileEntity.class, new RenderStallTile());
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(stall), 0, new ModelResourceLocation(Item.getItemFromBlock(stall).getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(halter, 0, new ModelResourceLocation(halter.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(displayer, 0, new ModelResourceLocation(displayer.getRegistryName(), "inventory"));
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        FCConfig.load();
        if (FCConfig.FLUIDS.size() > 0)
            EntityRegistry.addSpawn(EntityFluidCow.class, 8, 4, 4, EnumCreatureType.CREATURE, ForgeRegistries.BIOMES.getValues().toArray(new Biome[0]));
        info("This is info! Support " + FCUtils.getBucketFluids().size() + " fluids. Can spawn " + FCConfig.FLUIDS.size() + " cows.");
        if (Loader.isModLoaded("projecte") && FCConfig.projecteTickRemove) {
            ProjectEIntegration.reg();
        }
        if (Loader.isModLoaded("notenoughwands") && FCConfig.notenoughwandsTickRemove) {
            NotEnoughWandsIntegration.reg();
        }
        if (Loader.isModLoaded("torcherino") && FCConfig.torcherinoTickRemove) {
            TorcherinoIntegration.reg();
        }
        if (Loader.isModLoaded("mekanism")) {
            MekanismIntegration.reg();
        }
    }

    public static void info(String msg) {
        log.info("[FluidCows] " + msg);
    }

    public static void warn(String msg) {
        log.warn("[FluidCows] " + msg);
    }

    public static void debug(String msg) {
        log.info("[FluidCows] DEBUG: " + msg);
    }
}
