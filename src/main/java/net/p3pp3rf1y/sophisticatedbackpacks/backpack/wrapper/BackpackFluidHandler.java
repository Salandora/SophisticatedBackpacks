package net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper;

import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.p3pp3rf1y.sophisticatedcore.api.IStorageFluidHandler;
import net.p3pp3rf1y.sophisticatedcore.api.IStorageWrapper;
import net.p3pp3rf1y.sophisticatedcore.upgrades.tank.TankUpgradeItem;
import net.p3pp3rf1y.sophisticatedcore.upgrades.tank.TankUpgradeWrapper;

import java.util.Iterator;
import java.util.List;
import javax.annotation.Nonnull;

public class BackpackFluidHandler implements IStorageFluidHandler {
	private final IStorageWrapper backpackWrapper;

	public BackpackFluidHandler(IStorageWrapper backpackWrapper) {
		this.backpackWrapper = backpackWrapper;
	}

	@Nonnull
	private List<TankUpgradeWrapper> getAllTanks() {
		return backpackWrapper.getUpgradeHandler().getTypeWrappers(TankUpgradeItem.TYPE);
	}

	@Override
	public long insert(FluidVariant resource, long maxFill, TransactionContext ctx, boolean ignoreInOutLimit) {
		long remaining = maxFill;
		for (TankUpgradeWrapper tank : getAllTanks()) {
			remaining -= tank.fill(resource, remaining, ctx, ignoreInOutLimit);
			if (remaining <= 0) {
				return maxFill;
			}
		}

		return maxFill - remaining;

	}

	public long insert(FluidVariant resource, long maxFill, TransactionContext ctx) {
		return insert(resource, maxFill, ctx, false);
	}

	@Override
	public FluidStack extract(TagKey<Fluid> resourceTag, long maxDrain, TransactionContext ctx, boolean ignoreInOutLimit) {
		FluidStack drained = FluidStack.EMPTY;
		long toDrain = maxDrain;
		for (TankUpgradeWrapper tank : getAllTanks()) {
			Fluid tankFluid = tank.getContents().getFluid();
			if ((drained.isEmpty() && tankFluid.defaultFluidState().is(resourceTag)) || tank.getContents().isFluidEqual(drained)) {
				if (drained.isEmpty()) {
					drained = new FluidStack(tankFluid, tank.drain(toDrain, ctx, ignoreInOutLimit));
				} else {
					drained.grow(tank.drain(toDrain, ctx, ignoreInOutLimit));
				}

				if (drained.getAmount() == maxDrain) {
					return drained;
				}

				toDrain = maxDrain - drained.getAmount();
			}
		}

		return drained;
	}

	@Override
	public FluidStack extract(FluidStack resource, TransactionContext ctx, boolean ignoreInOutLimit) {
		long drained = 0;
		long toDrain = resource.getAmount();
		for (TankUpgradeWrapper tank : getAllTanks()) {
			if (tank.getContents().isFluidEqual(resource)) {
				drained += tank.drain(toDrain, ctx, ignoreInOutLimit);
				if (drained == resource.getAmount()) {
					return resource;
				}
				toDrain = resource.getAmount() - drained;
			}
		}

		return drained == 0 ? FluidStack.EMPTY : new FluidStack(resource, drained);
	}

	@Override
	public FluidStack extract(int maxDrain, TransactionContext ctx, boolean ignoreInOutLimit) {
		for (TankUpgradeWrapper tank : getAllTanks()) {
			FluidStack drained = new FluidStack(tank.getResource(), tank.drain(maxDrain, ctx, ignoreInOutLimit));
			if (!drained.isEmpty()) {
				return drained;
			}
		}
		return FluidStack.EMPTY;
	}

	@Override
	public long extract(FluidVariant resource, long maxAmount, TransactionContext ctx, boolean ignoreInOutLimit) {
		long remaining = maxAmount;
		for (TankUpgradeWrapper tank : getAllTanks()) {
			if (tank.getContents().isFluidEqual(resource)) {
				remaining -= tank.drain(remaining, ctx, ignoreInOutLimit);
				if (remaining >= maxAmount) {
					return maxAmount;
				}
			}
		}

		return maxAmount - remaining;
	}

	@Override
	public long extract(FluidVariant resource, long maxAmount, TransactionContext ctx) {
		return extract(resource, maxAmount, ctx, false);
	}

	@Override
	public Iterator<StorageView<FluidVariant>> iterator() {
		return new CombinedStorage<>(getAllTanks()).iterator();
	}
}
