package ftblag.fluidcows;

import ftblag.fluidcows.base.BaseItem;
import ftblag.fluidcows.block.accelerator.AcceleratorBlock;
import ftblag.fluidcows.block.feeder.FeederBlock;
import ftblag.fluidcows.block.sorter.SorterBlock;
import ftblag.fluidcows.block.stall.StallBlock;
import ftblag.fluidcows.block.stall.StallTileEntity;
import ftblag.fluidcows.client.GuiHandler;
import ftblag.fluidcows.client.RenderFluidCow;
import ftblag.fluidcows.client.RenderStallTile;
import ftblag.fluidcows.entity.EntityFluidCow;
import ftblag.fluidcows.gson.FCConfig;
import ftblag.fluidcows.integration.EIOIntegration;
import ftblag.fluidcows.integration.HwylaIntegration;
import ftblag.fluidcows.integration.MekanismIntegration;
import ftblag.fluidcows.integration.NotEnoughWandsIntegration;
import ftblag.fluidcows.integration.ProjectEIntegration;
import ftblag.fluidcows.integration.RandomThingsIntegration;
import ftblag.fluidcows.integration.TOPIntegration;
import ftblag.fluidcows.integration.TorcherinoIntegration;
import ftblag.fluidcows.item.ItemCowDisplayer;
import ftblag.fluidcows.item.ItemCowHalter;
import ftblag.fluidcows.network.NetworkHandler;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mod(modid = FluidCows.MODID, name = FluidCows.NAME, version = FluidCows.VERSION, dependencies = "after:*;required-after:forge@[14.23.5.2768,);")
public class FluidCows {

    public static final String MODID = "fluidcows", NAME = "Fluid Cows", VERSION = "@VERSION@";

    private static Logger log;
    public static StallBlock stall;
    public static ItemCowHalter halter;
    public static ItemCowDisplayer displayer;
    public static AcceleratorBlock accelerator;
    public static FeederBlock feeder;
    public static SorterBlock sorter;
    public static Item ranger;

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
        NetworkHandler.reg();
        EntityRegistry.registerModEntity(new ResourceLocation(MODID, "fluidcow"), EntityFluidCow.class, MODID + "." + "fluidcow", 0, this, 64, 1, true);
//        EntityRegistry.addSpawn(EntityFluidCow.class, 8, 4, 4, EnumCreatureType.CREATURE, ForgeRegistries.BIOMES.getValues().toArray(new Biome[0]));
        ForgeRegistries.BLOCKS.registerAll(
                stall = new StallBlock(),
                accelerator = new AcceleratorBlock(),
                feeder = new FeederBlock(),
                sorter = new SorterBlock());
        ForgeRegistries.ITEMS.registerAll(
                new ItemBlock(stall).setRegistryName(stall.getRegistryName()),
                halter = new ItemCowHalter(),
                displayer = new ItemCowDisplayer(),
                new ItemBlock(accelerator).setRegistryName(accelerator.getRegistryName()),
                new ItemBlock(feeder).setRegistryName(feeder.getRegistryName()),
                new ItemBlock(sorter).setRegistryName(sorter.getRegistryName()),
                ranger = new BaseItem("ranger").setMaxStackSize(7));
        GameRegistry.addShapedRecipe(new ResourceLocation(MODID, "stall"), null, new ItemStack(stall), "B B", "BHB", "GGG", 'B', Blocks.IRON_BARS, 'H', Blocks.HAY_BLOCK, 'G', new ItemStack(Blocks.CONCRETE, 1, 7));
        GameRegistry.addShapedRecipe(new ResourceLocation(MODID, "halter"), null, new ItemStack(halter), "  L", " S ", "S  ", 'L', Items.LEATHER, 'S', Items.STICK);
        GameRegistry.addShapedRecipe(new ResourceLocation(MODID, "accelerator"), null, new ItemStack(accelerator), "IHI", "RHL", "III", 'I', Items.IRON_INGOT, 'H', Blocks.HAY_BLOCK, 'R', Blocks.REDSTONE_BLOCK, 'L', Items.LEATHER);
        
        GameRegistry.addShapedRecipe(new ResourceLocation(MODID, "feeder"), null, new ItemStack(feeder), "GIG", "ILI", "GIG", 'G', new ItemStack(Blocks.CONCRETE, 1, 6), 'L', Items.LEATHER, 'I', Items.IRON_INGOT);
        GameRegistry.addShapedRecipe(new ResourceLocation(MODID, "sorter"), null, new ItemStack(sorter), "GIG", "ILI", "GWG", 'G', new ItemStack(Blocks.CONCRETE, 1, 6), 'L', Items.LEATHER, 'I', Items.IRON_INGOT, 'W', Items.WHEAT);
        GameRegistry.addShapedRecipe(new ResourceLocation(MODID, "ranger"), null, new ItemStack(ranger), "W W", "HHH", 'W', new ItemStack(Blocks.WOOL), 'H', Items.WHEAT);

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
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(accelerator), 0, new ModelResourceLocation(Item.getItemFromBlock(accelerator).getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(feeder), 0, new ModelResourceLocation(Item.getItemFromBlock(feeder).getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(sorter), 0, new ModelResourceLocation(Item.getItemFromBlock(sorter).getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ranger, 0, new ModelResourceLocation(ranger.getRegistryName(), "inventory"));
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        FCConfig.load();
        if (FCConfig.FLUIDS.size() > 0) {
            List<String> blackList = Arrays.asList(FCConfig.spawnBlackListBiomes);
            List<Biome> biomes = new ArrayList<>(ForgeRegistries.BIOMES.getValues());
            if (!blackList.isEmpty()) {
                biomes.removeIf(i -> blackList.contains(ForgeRegistries.BIOMES.getKey(i).toString()));
            }

            if (FCConfig.spawnWeight > 0)
                EntityRegistry.addSpawn(EntityFluidCow.class, FCConfig.spawnWeight, FCConfig.spawnMin, FCConfig.spawnMax, EnumCreatureType.CREATURE, biomes.toArray(new Biome[biomes.size()]));
        }
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
        if (Loader.isModLoaded("randomthings") && FCConfig.randomthingsTickRemove) {
            RandomThingsIntegration.reg();
        }
        if (Loader.isModLoaded("enderio")) {
            EIOIntegration.reg();
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
