package ftblag.fluidcows.gson;

import ftblag.fluidcows.FluidCows;
import ftblag.fluidcows.entity.EntityFluidCow;
import ftblag.fluidcows.util.FCUtils;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FCConfig {

    public static final String COMMENT = "_Comment",
            RATE = "SpawnRate",
            ENABLE = "IsEnabled",
            WORLD = "WorldCooldown",
            STALL = "StallCooldown",
            BREEDING_CHANCE = "BreedingChance",
            BREEDING_COOLDOWN = "BreedingCooldown",
            GROWING_BABY = "GrowingAge",
            PARENT_1 = "Parent First",
            PARENT_2 = "Parent Second";
    public static final String COMMENT_CLIENT = "_Comment Client",
            CLIENT = "Client",
            HIDEFLUIDCOW = "HideFluidLayerCow";
    public static final String COMMENT_GENERAL = "_Comment General",
            GENERAL = "General",
            BREEDING = "BreedingItemWork",
            PROJECTETICK = "ProjectETickRemove",
            NOTENOWANDSTICK = "NotEnoughtWandsTickRemove",
            TORCHERINOTICK = "TorcherinoTickRemove",
            RANDOMTHINGSTICK = "randomthingsTickRemove",
            BREEDINGITEMMACHINES = "DisableBreedingItemForMachines",
            SPAWNWEIGHT = "FluidCowsSpawnWeight",
            SPAWNMIN = "FluidCowsSpawnMin",
            SPAWNMAX = "FluidCowsSpawnMax",
            SPAWNBLACKLIST = "FluidCowsSpawnBlackListBiomes",
            ACCELERATORMAX = "AcceleratorMaxSubstance",
            ACCELERATORRADIUS = "AcceleratorRadius",
            ACCELERATORPERCOW = "AcceleratorSubstancePerCow",
            ACCELERATORMULTIPLIER = "AcceleratorMultiplier",
            ACCELERATORWATER = "AcceleratorWaterPerConvert",
            BLACKLISTDIMIDS = "BlackListDimIds",
            ENABLECONVERTCOWTODISPLAYER = "EnableConvertCowToDisplayer",
            BLACKLISTCOWTODISPLAYER = "BlackListCowToDisplayer",
            FEEDERBLACKLIST = "FeederBlackList",
            EIOBLACKLISTSPAWNING = "EIOBlackListSpawning",
            EIOBLACKLISTSOULVIAL = "EIOBlackListSoulVial",
            EIONEEDSCLONING = "EIONeedsCloning",
            EIOENTITYCOSTMULTIPLIER = "EIOEntityCostMultiplier";
    private static final FluidInfo def = new FluidInfo(0, false, Integer.MAX_VALUE, Integer.MAX_VALUE, 0, Integer.MAX_VALUE, Integer.MIN_VALUE);

    private static JsonConfig parser;

    public static ArrayList<Fluid> FLUIDS = new ArrayList<>();
    public static int sumWeight;

    public static boolean hideFluidCow;

    public static HashMap<CustomPair<String, String>, List<Fluid>> breed = new HashMap<>();
    public static HashSet<Fluid> canBreed = new HashSet<>();
    public static boolean breedingItemWork;
    public static boolean projecteTickRemove, notenoughwandsTickRemove, torcherinoTickRemove, randomthingsTickRemove, disableBreedingItemForMachines;
    public static int spawnWeight, spawnMin, spawnMax;
    public static String[] spawnBlackListBiomes;
    public static int acceleratorMax, acceleratorRadius, acceleratorPerCow, acceleratorMultiplier, acceleratorWater;

    public static Set<Integer> blackListDimIds;
    public static boolean enableConvertCowToDisplayer;
    public static Set<String> blackListCowToDisplayer;
    public static Set<String> feederBlackList;

    public static boolean EIOBlackListSpawning, EIOBlackListSoulVial, EIONeedsCloning;
    public static int EIOEntityCostMultiplier;

    private static HashMap<String, FluidInfo> registry = new HashMap<>();

    public static boolean loaded;

    public static void setFile(File file) {
        parser = new JsonConfig(file);
    }

    public static void load() {
        parser.load();

        parser.getOrDefString(COMMENT, RATE, "Spawn rate");
        parser.getOrDefString(COMMENT, ENABLE, "False = Disabled, true = Enabled");
        parser.getOrDefString(COMMENT, WORLD, "Cooldown if cow milked in world (not in stall, like mechanisms)");
        parser.getOrDefString(COMMENT, STALL, "Cooldown if cow milked from Stall");
        parser.getOrDefString(COMMENT, BREEDING_CHANCE, "Chance of breeding to succeed");
        parser.getOrDefString(COMMENT, BREEDING_COOLDOWN, "How many ticks it takes before the cow can breed again");
        parser.getOrDefString(COMMENT, GROWING_BABY, "How many ticks it takes for the baby cow to grow up");
        parser.getOrDefString(COMMENT, PARENT_1, "First parent to fluid (empty is disable) example usage: \"lava\" \"water\"");
        parser.getOrDefString(COMMENT, PARENT_2, "Second parent to fluid (empty is disable) example usage: \"lava\" \"water\"");

        parser.getOrDefString(COMMENT, "Tip#1", "Cow rewards? Yes! Set enable to true, rate to zero, remove parents and make recipe with CraftTweaker!");
        parser.getOrDefString(COMMENT, "Tip#2", "Only breeding cow? Yes! Set enable to true, rate to zero and add parents!");

        parser.getOrDefString(COMMENT_CLIENT, HIDEFLUIDCOW, "Disable fluid render layer cow in stall");

        hideFluidCow = parser.getOrDefBoolean(CLIENT, HIDEFLUIDCOW, false);

        parser.getOrDefString(COMMENT_GENERAL, BREEDING, "If true u can use the breeding item to get lower baby growing age");
        parser.getOrDefString(COMMENT_GENERAL, PROJECTETICK, "If true - \"Watch of Flowing Time\" not work on Cow Stall. From mod \"ProjectE\"");
        parser.getOrDefString(COMMENT_GENERAL, NOTENOWANDSTICK, "If true - \"Acceleration Wand\" not work on Cow Stall. From mod \"Not Enough Wand\"");
        parser.getOrDefString(COMMENT_GENERAL, TORCHERINOTICK, "If true - all types \"Torcherino\" not work on Cow Stall. From mod \"Torcherino\"");
        parser.getOrDefString(COMMENT_GENERAL, RANDOMTHINGSTICK, "If true - \"Time in a bottle\" not work on Cow Stall. From mod \"Random Things\"");
        parser.getOrDefString(COMMENT_GENERAL, BREEDINGITEMMACHINES, "Disables get breeding item via machines");

        parser.getOrDefString(COMMENT_GENERAL, SPAWNWEIGHT, "Fluid cows spawn weight");
        parser.getOrDefString(COMMENT_GENERAL, SPAWNMIN, "Fluid cows spawn min");
        parser.getOrDefString(COMMENT_GENERAL, SPAWNMAX, "Fluid cows spawn max");
        parser.getOrDefString(COMMENT_GENERAL, SPAWNBLACKLIST, "Fluid cows spawn black list biomes (modid:name)");

        parser.getOrDefString(COMMENT_GENERAL, ACCELERATORMAX, "Accelerator max substance per one wheat");
        parser.getOrDefString(COMMENT_GENERAL, ACCELERATORRADIUS, "Accelerator working radius");
        parser.getOrDefString(COMMENT_GENERAL, ACCELERATORPERCOW, "Accelerator one substance per one cow");
        parser.getOrDefString(COMMENT_GENERAL, ACCELERATORMULTIPLIER, "Accelerator speed up multiplier");
        parser.getOrDefString(COMMENT_GENERAL, ACCELERATORWATER, "Accelerator water per one substance convert");

        parser.getOrDefString(COMMENT_GENERAL, BLACKLISTDIMIDS, "In what dim Id cow not spawn");
        parser.getOrDefString(COMMENT_GENERAL, ENABLECONVERTCOWTODISPLAYER, "If true u can convert cow into displayer via halter");
        parser.getOrDefString(COMMENT_GENERAL, BLACKLISTCOWTODISPLAYER, "Black list for 'cow to displayer' convert");
        parser.getOrDefString(COMMENT_GENERAL, FEEDERBLACKLIST, "Black list for 'what cows cant feed with Feeder'");

        parser.getOrDefString(COMMENT_GENERAL, EIOBLACKLISTSPAWNING, "EIO Powered Spawner cant spawn any cow");
        parser.getOrDefString(COMMENT_GENERAL, EIOBLACKLISTSOULVIAL, "EIO Soul Vial cant store any cow");
        parser.getOrDefString(COMMENT_GENERAL, EIONEEDSCLONING, "EIO Powered Spawner cloning cow every time (prevents spawn random cows from spawner)");
        parser.getOrDefString(COMMENT_GENERAL, EIOENTITYCOSTMULTIPLIER, "EIO Powered Spawner need multiplier energy cost to spawn cow");

        breedingItemWork = parser.getOrDefBoolean(GENERAL, BREEDING, false);
        projecteTickRemove = parser.getOrDefBoolean(GENERAL, PROJECTETICK, false);
        notenoughwandsTickRemove = parser.getOrDefBoolean(GENERAL, NOTENOWANDSTICK, false);
        torcherinoTickRemove = parser.getOrDefBoolean(GENERAL, TORCHERINOTICK, false);
        randomthingsTickRemove = parser.getOrDefBoolean(GENERAL, RANDOMTHINGSTICK, false);
        disableBreedingItemForMachines = parser.getOrDefBoolean(GENERAL, BREEDINGITEMMACHINES, false);

        spawnWeight = parser.getOrDefInt(GENERAL, SPAWNWEIGHT, 8);
        spawnMin = parser.getOrDefInt(GENERAL, SPAWNMIN, 4);
        spawnMax = parser.getOrDefInt(GENERAL, SPAWNMAX, 4);
        spawnBlackListBiomes = parser.getOrDefStringArray(GENERAL, SPAWNBLACKLIST, new String[] { "modid:name1", "modid:name2" });

        acceleratorMax = parser.getOrDefInt(GENERAL, ACCELERATORMAX, 6);
        acceleratorRadius = parser.getOrDefInt(GENERAL, ACCELERATORRADIUS, 5);
        acceleratorPerCow = parser.getOrDefInt(GENERAL, ACCELERATORPERCOW, 1);
        acceleratorMultiplier = parser.getOrDefInt(GENERAL, ACCELERATORMULTIPLIER, 5);
        acceleratorWater = parser.getOrDefInt(GENERAL, ACCELERATORWATER, 10);

        blackListDimIds = IntStream.of(parser.getOrDefIntArray(GENERAL, BLACKLISTDIMIDS, new int[0])).boxed().collect(Collectors.toSet());
        enableConvertCowToDisplayer = parser.getOrDefBoolean(GENERAL, ENABLECONVERTCOWTODISPLAYER, true);
        blackListCowToDisplayer = Arrays.stream(parser.getOrDefStringArray(GENERAL, BLACKLISTCOWTODISPLAYER, new String[0])).collect(Collectors.toSet());
        feederBlackList = Arrays.stream(parser.getOrDefStringArray(GENERAL, FEEDERBLACKLIST, new String[0])).collect(Collectors.toSet());


        EIOBlackListSpawning = parser.getOrDefBoolean(GENERAL, EIOBLACKLISTSPAWNING, false);
        EIOBlackListSoulVial = parser.getOrDefBoolean(GENERAL, EIOBLACKLISTSOULVIAL, false);
        EIONeedsCloning = parser.getOrDefBoolean(GENERAL, EIONEEDSCLONING, true);
        EIOEntityCostMultiplier = parser.getOrDefInt(GENERAL, EIOENTITYCOSTMULTIPLIER, 0);



        registry.clear();
        FLUIDS.clear();
        sumWeight = 0;
        breed.clear();

        for (Fluid fluid : FCUtils.getBucketFluids()) {
            String fName = fluid.getName();
            int rate = parser.getOrDefInt(fName, RATE, 100);
            boolean enable = parser.getOrDefBoolean(fName, ENABLE, true);
            int world = parser.getOrDefInt(fName, WORLD, 4000);
            int stall = parser.getOrDefInt(fName, STALL, 4000);
            int breedingChance = parser.getOrDefInt(fName, BREEDING_CHANCE, 50);
            int breedingCooldown = parser.getOrDefInt(fName, BREEDING_COOLDOWN, 6000);
            int growBaby = parser.getOrDefInt(fName, GROWING_BABY, -24000);
            registry.put(fName, new FluidInfo(rate, enable, world, stall, breedingChance, breedingCooldown, growBaby));
            if (enable && rate > 0) {
                FLUIDS.add(fluid);
                sumWeight += rate;
            }
        }
        for (Fluid fluid : FCUtils.getBucketFluids()) {
            String fName = fluid.getName();
            String parent1 = parser.getOrDefString(fName, PARENT_1, "");
            String parent2 = parser.getOrDefString(fName, PARENT_2, "");
            if (!parent1.isEmpty() && !parent2.isEmpty() && isEnable(fName) && isEnable(parent1) && isEnable(parent2)) {
                if (FluidRegistry.isFluidRegistered(parent1) && FluidRegistry.isFluidRegistered(parent2)) {

                    CustomPair<String, String> pair = CustomPair.of(parent1, parent2);
                    List<Fluid> list = breed.containsKey(pair) ? breed.get(pair) : new ArrayList<>();
                    list.add(fluid);
                    breed.put(pair, list);

                    canBreed.add(fluid);
                    FluidCows.info("Breeding: Add new! First parent -> \"" + parent1 + "\"; Second parent -> \"" + parent2 + "\"; result -> \"" + fName + "\"");
                } else {
                    FluidCows.warn("Breeding: Failed to add! First parent -> \"" + parent1 + "\"; Second parent -> \"" + parent2 + "\"; result -> \"" + fName + "\"");
                }
            }
        }

        FluidCows.info("Added " + breed.size() + " breeding variants!");

        parser.save();
        loaded = true;
    }

    public static boolean isEnable(String name) {
        return registry.getOrDefault(name, def).enable;
    }

    public static int getRate(String name) {
        return registry.getOrDefault(name, def).rate;
    }

    public static int getWorldCD(String name) {
        return registry.getOrDefault(name, def).world;
    }

    public static int getStallCD(String name) {
        return registry.getOrDefault(name, def).stall;
    }

    public static int getChance(String name) {
        return registry.getOrDefault(name, def).breedingChance;
    }

    public static int getBreedingCooldown(String name) {
        return registry.getOrDefault(name, def).breedingCooldown;
    }

    public static int getGrowBaby(String name) {
        return registry.getOrDefault(name, def).growBaby;
    }

    public static boolean canMateWith(EntityFluidCow parentFirst, EntityFluidCow parentSecond) {
        return parentFirst.fluid.getName().equals(parentSecond.fluid.getName()) || breed.containsKey(CustomPair.of(parentFirst.fluid.getName(), parentSecond.fluid.getName()));
    }

    public static EntityFluidCow mateWith(EntityFluidCow parentFirst, EntityFluidCow parentSecond) {
        Fluid fluid;
        if (parentFirst.fluid.getName().equals(parentSecond.fluid.getName()))
            fluid = parentFirst.fluid;
        else {
            CustomPair<String, String> pair = CustomPair.of(parentFirst.fluid.getName(), parentSecond.fluid.getName());
            List<Fluid> resList = breed.get(pair);
            Fluid res = resList.size() == 1 ? resList.get(0) : resList.get(parentFirst.getRNG().nextInt(resList.size()));
            if (parentFirst.getRNG().nextInt(100) < getChance(res.getName()))
                fluid = res;
            else
                fluid = parentFirst.getRNG().nextBoolean() ? parentFirst.fluid : parentSecond.fluid;
        }
        return new EntityFluidCow(parentFirst.world, fluid);
    }

    private static class FluidInfo {
        public int rate;
        public boolean enable;
        public int world;
        public int stall;
        public int breedingChance;
        public int breedingCooldown;
        public int growBaby;

        public FluidInfo(int rate, boolean enable, int world, int stall, int breedingChance, int breedingCooldown, int growBaby) {
            this.rate = rate;
            this.enable = enable;
            this.world = world;
            this.stall = stall;
            this.breedingChance = breedingChance;
            this.breedingCooldown = breedingCooldown;
            this.growBaby = growBaby;
        }
    }
}
