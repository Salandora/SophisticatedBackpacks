package net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper;

import com.github.salandora.sophisticatedfabriclib.fluid.api.v1.FluidStack;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.p3pp3rf1y.sophisticatedcore.api.IStorageFluidHandler;
import net.p3pp3rf1y.sophisticatedcore.api.IStorageWrapper;
import net.p3pp3rf1y.sophisticatedcore.upgrades.tank.TankUpgradeItem;
import net.p3pp3rf1y.sophisticatedcore.upgrades.tank.TankUpgradeWrapper;

import javax.annotation.Nonnull;
import java.util.List;

public class BackpackFluidHandler implements IStorageFluidHandler {
	private final IStorageWrapper backpackWrapper;

	public BackpackFluidHandler(IStorageWrapper backpackWrapper) {
		this.backpackWrapper = backpackWrapper;
	}

	@Override
	public int getTanks() {
		return getAllTanks().size();
	}

	@Override
	public FluidStack getFluidInTank(int tank) {
		return isInvalidTank(tank) ? FluidStack.EMPTY : getAllTanks().get(tank).getContents();
	}

	// Fabric: Added for internal use to reset the content when a Transaction was cancelled
	@Override
	public void setFluidInTank(int tank, FluidStack fluidStack) {
		if (isInvalidTank(tank)) {
			return;
		}

		getAllTanks().get(tank).setContents(fluidStack);
	}

	@Nonnull
	private List<TankUpgradeWrapper> getAllTanks() {
		return backpackWrapper.getUpgradeHandler().getTypeWrappers(TankUpgradeItem.TYPE);
	}

	@Override
	public long getTankCapacity(int tank) {
		return isInvalidTank(tank) ? 0 : getAllTanks().get(tank).getTankCapacity();
	}

	@Override
	public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
		if (isInvalidTank(tank)) {
			return false;
		}

		FluidStack contents = getAllTanks().get(tank).getContents();
		return contents.isEmpty() || FluidStack.isSameFluidSameComponents(contents, stack);
	}

	@Override
	public long fill(FluidStack resource, FluidAction action, boolean ignoreInOutLimit) {
		int filled = 0;
		FluidStack toFill = resource;
		for (TankUpgradeWrapper tank : getAllTanks()) {
			filled += tank.fill(toFill, action, ignoreInOutLimit);
			if (filled == resource.getAmount()) {
				return resource.getAmount();
			}
			toFill = new FluidStack(toFill.getFluid(), resource.getAmount() - filled);
		}

		return filled;

	}

	@Override
	public long fill(FluidStack resource, FluidAction action) {
		return fill(resource, action, false);
	}

	@Override
	public FluidStack drain(TagKey<Fluid> resourceTag, long maxDrain, FluidAction action, boolean ignoreInOutLimit) {
		FluidStack drained = FluidStack.EMPTY;
		long toDrain = maxDrain;
		for (TankUpgradeWrapper tank : getAllTanks()) {
			if ((drained.isEmpty() && tank.getContents().is(resourceTag)) || FluidStack.isSameFluidSameComponents(tank.getContents(), drained)) {
				if (drained.isEmpty()) {
					drained = tank.drain(toDrain, action, ignoreInOutLimit);
				} else {
					drained.grow(tank.drain(toDrain, action, ignoreInOutLimit).getAmount());
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
	public FluidStack drain(FluidStack resource, FluidAction action, boolean ignoreInOutLimit) {
		long drained = 0;
		long toDrain = resource.getAmount();
		for (TankUpgradeWrapper tank : getAllTanks()) {
			if (FluidStack.isSameFluidSameComponents(tank.getContents(), resource)) {
				drained += tank.drain(toDrain, action, ignoreInOutLimit).getAmount();
				if (drained == resource.getAmount()) {
					return resource;
				}
				toDrain = resource.getAmount() - drained;
			}
		}

		return drained == 0 ? FluidStack.EMPTY : new FluidStack(resource.getFluid(), drained);
	}

	@Override
	public FluidStack drain(FluidStack resource, FluidAction action) {
		return drain(resource, action, false);
	}

	@Override
	public FluidStack drain(long maxDrain, FluidAction action, boolean ignoreInOutLimit) {
		for (TankUpgradeWrapper tank : getAllTanks()) {
			FluidStack drained = tank.drain(maxDrain, action, ignoreInOutLimit);
			if (!drained.isEmpty()) {
				return drained;
			}
		}
		return FluidStack.EMPTY;
	}

	@Override
	public FluidStack drain(long maxDrain, FluidAction action) {
		return drain(maxDrain, action, false);
	}

	private boolean isInvalidTank(int tank) {
		return tank < 0 || tank >= getTanks();
	}

	@Override
	public ItemStack getContainer() {
		return backpackWrapper.getWrappedStorageStack();
	}
}
