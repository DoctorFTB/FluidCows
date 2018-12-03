package ftblag.fluidcows.gson;

import ftblag.fluidcows.FluidCows;
import ftblag.fluidcows.entity.EntityFluidCow;
import ftblag.fluidcows.util.FCUtils;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import java.io.File;
import java.util.*;

public class FCConfig {

    public static final String COMMENT = "_Comment", RATE = "SpawnRate", ENABLE = "IsEnabled", WORLD = "WorldCooldown", STALL = "StallCooldown", BREEDING_CHANCE = "BreedingChance", BREEDING_COOLDOWN = "BreedingCooldown", GROWING_BABY = "GrowingAge", PARENT_1 = "Parent First", PARENT_2 = "Parent Second";
    public static final String COMMENT_GENERAL = "_Comment General", GENERAL = "General", BREEDING = "BreedingItemWork";
    private static final FluidInfo def = new FluidInfo(0, false, Integer.MAX_VALUE, Integer.MAX_VALUE, 0, Integer.MAX_VALUE, Integer.MIN_VALUE);

    private static JsonConfig parser;

    public static ArrayList<Fluid> FLUIDS = new ArrayList<>();
    public static int sumWeight;

    public static HashMap<CustomPair<String, String>, List<Fluid>> breed = new HashMap<>();
    public static HashSet<Fluid> canBreed = new HashSet<>();
    public static boolean breedingItemWork;
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

        parser.getOrDefString(COMMENT_GENERAL, BREEDING, "If true u can use the breeding item to get lower baby growing age");

        breedingItemWork = parser.getOrDefBoolean(GENERAL, BREEDING, false);

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
        for (Fluid fluid : FLUIDS /*FCUtils.getBucketFluids()*/) {
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
                    FluidCows.log.info("Breeding: Add new! First parent -> \"" + parent1 + "\"; Second parent -> \"" + parent2 + "\"; result -> \"" + fName + "\"");
                } else {
                    FluidCows.log.warn("Breeding: Failed to add! First parent -> \"" + parent1 + "\"; Second parent -> \"" + parent2 + "\"; result -> \"" + fName + "\"");
                }
            }
        }

        FluidCows.log.info("Added " + breed.size() + " breeding variants!");

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
