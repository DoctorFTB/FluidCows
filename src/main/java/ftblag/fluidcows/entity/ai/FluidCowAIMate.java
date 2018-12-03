package ftblag.fluidcows.entity.ai;

import ftblag.fluidcows.entity.EntityFluidCow;
import ftblag.fluidcows.gson.FCConfig;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class FluidCowAIMate extends EntityAIBase { // Copy from EntityAIMate with changed "spawnBaby".
    private final EntityFluidCow animal;
    private final Class<? extends EntityFluidCow> mateClass;
    private World world;
    private EntityFluidCow targetMate;
    private int spawnBabyDelay;
    private double moveSpeed;

    public FluidCowAIMate(EntityFluidCow animal, double speedIn) {
        this(animal, speedIn, animal.getClass());
    }

    public FluidCowAIMate(EntityFluidCow p_i47306_1_, double p_i47306_2_, Class<? extends EntityFluidCow> p_i47306_4_) {
        this.animal = p_i47306_1_;
        this.world = p_i47306_1_.world;
        this.mateClass = p_i47306_4_;
        this.moveSpeed = p_i47306_2_;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        if (!this.animal.isInLove()) {
            return false;
        } else {
            this.targetMate = this.getNearbyMate();
            return this.targetMate != null;
        }
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.targetMate.isEntityAlive() && this.targetMate.isInLove() && this.spawnBabyDelay < 60;
    }

    @Override
    public void resetTask() {
        this.targetMate = null;
        this.spawnBabyDelay = 0;
    }

    @Override
    public void updateTask() {
        this.animal.getLookHelper().setLookPositionWithEntity(this.targetMate, 10.0F, (float) this.animal.getVerticalFaceSpeed());
        this.animal.getNavigator().tryMoveToEntityLiving(this.targetMate, this.moveSpeed);
        ++this.spawnBabyDelay;

        if (this.spawnBabyDelay >= 60 && this.animal.getDistanceSq(this.targetMate) < 9.0D) {
            this.spawnBaby();
        }
    }

    private EntityFluidCow getNearbyMate() {
        List<EntityFluidCow> list = this.world.getEntitiesWithinAABB(this.mateClass, this.animal.getEntityBoundingBox().grow(8.0D));
        double d0 = Double.MAX_VALUE;
        EntityFluidCow entityanimal = null;

        for (EntityFluidCow e : list) {
            if (this.animal.canMateWith(e) && this.animal.getDistanceSq(e) < d0) {
                entityanimal = e;
                d0 = this.animal.getDistanceSq(e);
            }
        }

        return entityanimal;
    }

    private void spawnBaby() {
        EntityAgeable child = this.animal.createChild(this.targetMate);

        final net.minecraftforge.event.entity.living.BabyEntitySpawnEvent event = new net.minecraftforge.event.entity.living.BabyEntitySpawnEvent(animal, targetMate, child);
        final boolean cancelled = net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
        child = event.getChild();
        if (!(child instanceof EntityFluidCow))
            return;
        EntityFluidCow cow = (EntityFluidCow) child;
        if (cancelled) {
            //Reset the "inLove" state for the animals
            this.animal.setGrowingAge(FCConfig.getBreedingCooldown(animal.fluid.getName()));
            this.targetMate.setGrowingAge(FCConfig.getBreedingCooldown(targetMate.fluid.getName()));
            this.animal.resetInLove();
            this.targetMate.resetInLove();
            return;
        }

        if (cow != null) {
            EntityPlayerMP entityplayermp = this.animal.getLoveCause();

            if (entityplayermp == null && this.targetMate.getLoveCause() != null) {
                entityplayermp = this.targetMate.getLoveCause();
            }

            if (entityplayermp != null) {
                entityplayermp.addStat(StatList.ANIMALS_BRED);
                CriteriaTriggers.BRED_ANIMALS.trigger(entityplayermp, this.animal, this.targetMate, cow);
            }

            this.animal.setGrowingAge(FCConfig.getBreedingCooldown(animal.fluid.getName()));
            this.targetMate.setGrowingAge(FCConfig.getBreedingCooldown(targetMate.fluid.getName()));
            this.animal.resetInLove();
            this.targetMate.resetInLove();
            cow.setGrowingAge(FCConfig.getGrowBaby(cow.fluid.getName()));
            cow.setLocationAndAngles(this.animal.posX, this.animal.posY, this.animal.posZ, 0.0F, 0.0F);
            this.world.spawnEntity(cow);
            Random random = this.animal.getRNG();

            for (int i = 0; i < 7; ++i) {
                double d0 = random.nextGaussian() * 0.02D;
                double d1 = random.nextGaussian() * 0.02D;
                double d2 = random.nextGaussian() * 0.02D;
                double d3 = random.nextDouble() * (double) this.animal.width * 2.0D - (double) this.animal.width;
                double d4 = 0.5D + random.nextDouble() * (double) this.animal.height;
                double d5 = random.nextDouble() * (double) this.animal.width * 2.0D - (double) this.animal.width;
                this.world.spawnParticle(EnumParticleTypes.HEART, this.animal.posX + d3, this.animal.posY + d4, this.animal.posZ + d5, d0, d1, d2);
            }

            if (this.world.getGameRules().getBoolean("doMobLoot")) {
                this.world.spawnEntity(new EntityXPOrb(this.world, this.animal.posX, this.animal.posY, this.animal.posZ, random.nextInt(7) + 1));
            }
        }
    }
}
