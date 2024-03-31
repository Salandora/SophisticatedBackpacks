package net.p3pp3rf1y.sophisticatedbackpacks.upgrades.everlasting;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

@SuppressWarnings("java:S2160") //no need to override equals, the default implementation is good
public class EverlastingBackpackItemEntity extends ItemEntity {
	private boolean wasFloatingUp = false;

	// Add an age property here, so we can use the unlimited Lifetime and have the item rotation work normally
	private int age;

	public EverlastingBackpackItemEntity(EntityType<? extends ItemEntity> type, Level world) {
		super(type, world);
		age = 0;
		setUnlimitedLifetime();
		//lifespan = Integer.MAX_VALUE; //set to not despawn
	}

	@Override
	public void tick() {
		if (!level().isClientSide) {
			double d0 = getX() + 0.5F - random.nextFloat();
			double d1 = getY() + random.nextFloat() * 0.5F;
			double d2 = getZ() + 0.5F - random.nextFloat();
			ServerLevel serverWorld = (ServerLevel) level();
			if (random.nextInt(20) == 0) {
				serverWorld.sendParticles(ParticleTypes.HAPPY_VILLAGER, d0, d1, d2, 0, 0, 0.1D, 0, 1f);
			}
		}
		if (!isNoGravity()) {
			if (isInWater() || isInLava()) {
				onInsideBubbleColumn(false);
				wasFloatingUp = true;
			} else if (wasFloatingUp) {
				setNoGravity(true);
				setDeltaMovement(Vec3.ZERO);
			}
		}
		++age;
		super.tick();
	}

	@Override
	public boolean isInWater() {
		return getY() < level().getMinBuildHeight() + 1 || super.isInWater();
	}

	@Override
	public boolean fireImmune() {
		return true;
	}

	@Override
	public boolean ignoreExplosion() {
		return true;
	}

	@Override
	public boolean isInvulnerableTo(DamageSource source) {
		return true;
	}

	@Override
	protected void onBelowWorld() {
		//do nothing as the only thing that vanilla does here is remove entity from world, but it can't for this
	}

	@Override
	public int getAge() {
		return age;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);

		compound.putInt("EverlastingAge", this.age);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);

		this.age = compound.getInt("EverlastingAge");
	}
}
