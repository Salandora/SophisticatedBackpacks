package net.p3pp3rf1y.sophisticatedbackpacks.upgrades.inception;

import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.p3pp3rf1y.sophisticatedcore.api.IStorageFluidHandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

public class InceptionFluidHandler implements IStorageFluidHandler {
	@Nullable
	private final IStorageFluidHandler wrappedFluidHandler;
	private final InventoryOrder inventoryOrder;
	private final SubBackpacksHandler subBackpacksHandler;
	private IStorageFluidHandler[] fluidHandlers;
	private final ItemStack backpack;

	public InceptionFluidHandler(@Nullable IStorageFluidHandler wrappedFluidHandler, ItemStack backpack, InventoryOrder inventoryOrder, SubBackpacksHandler subBackpacksHandler) {
		this.wrappedFluidHandler = wrappedFluidHandler;
		this.backpack = backpack;
		this.inventoryOrder = inventoryOrder;
		this.subBackpacksHandler = subBackpacksHandler;
		subBackpacksHandler.addRefreshListener(sbs -> refreshHandlers());
		refreshHandlers();
	}

	private void refreshHandlers() {
		List<IStorageFluidHandler> handlers = new ArrayList<>();
		if (wrappedFluidHandler != null && inventoryOrder == InventoryOrder.MAIN_FIRST) {
			handlers.add(wrappedFluidHandler);
		}
		subBackpacksHandler.getSubBackpacks().forEach(sbp -> sbp.getFluidHandler().ifPresent(handlers::add));
		if (wrappedFluidHandler != null && inventoryOrder == InventoryOrder.INCEPTED_FIRST) {
			handlers.add(wrappedFluidHandler);
		}
		fluidHandlers = handlers.toArray(new IStorageFluidHandler[] {});
	}

	@Override
	public long insert(FluidVariant resource, long maxFill, TransactionContext ctx, boolean ignoreInOutLimit) {
		long remaining = maxFill;
		for (IStorageFluidHandler fluidHandler : fluidHandlers) {
			remaining -= fluidHandler.insert(resource, remaining, ctx, ignoreInOutLimit);
			if (remaining <= 0) {
				return maxFill;
			}
		}

		return maxFill - remaining;
	}

	@Override
	public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {
		return insert(resource, maxAmount, transaction, false);
	}

	@Override
	public FluidStack extract(TagKey<Fluid> resourceTag, long maxDrain, TransactionContext ctx, boolean ignoreInOutLimit) {
		FluidStack drainedStack = FluidStack.EMPTY;
		FluidStack stackToDrain = FluidStack.EMPTY;
		for (IStorageFluidHandler fluidHandler : fluidHandlers) {
			if (drainedStack.isEmpty()) {
				drainedStack = fluidHandler.extract(resourceTag, maxDrain, ctx, ignoreInOutLimit);
				if (drainedStack.getAmount() == maxDrain) {
					return drainedStack;
				}
				if (!drainedStack.isEmpty()) {
					stackToDrain = new FluidStack(drainedStack, maxDrain - drainedStack.getAmount());
				}
			} else {
				long amountDrained = fluidHandler.extract(stackToDrain, ctx, ignoreInOutLimit).getAmount();
				stackToDrain.shrink(amountDrained);
				drainedStack.grow(amountDrained);
				if (drainedStack.getAmount() == maxDrain) {
					return drainedStack;
				}
			}
		}

		return drainedStack;
	}

	@Override
	public FluidStack extract(FluidStack resource, TransactionContext ctx, boolean ignoreInOutLimit) {
		long drained = 0;
		FluidStack toDrain = resource;
		for (IStorageFluidHandler fluidHandler : fluidHandlers) {
			drained += fluidHandler.extract(toDrain, ctx, ignoreInOutLimit).getAmount();
			if (drained == resource.getAmount()) {
				return resource;
			}
			toDrain = new FluidStack(toDrain, resource.getAmount() - drained);
		}

		return drained == 0 ? FluidStack.EMPTY : new FluidStack(resource, drained);
	}

	@Override
	public FluidStack extract(int maxDrain, TransactionContext ctx, boolean ignoreInOutLimit) {
		for (IStorageFluidHandler fluidHandler : fluidHandlers) {
			FluidStack drained = fluidHandler.extract(maxDrain, ctx, ignoreInOutLimit);
			if (!drained.isEmpty()) {
				return drained;
			}
		}
		return FluidStack.EMPTY;
	}

	@Override
	public long extract(FluidVariant resource, long maxDrain, TransactionContext ctx, boolean ignoreInOutLimit) {
		long remaining = maxDrain;
		for (IStorageFluidHandler fluidHandler : fluidHandlers) {
			remaining -= fluidHandler.extract(resource, remaining, ctx, ignoreInOutLimit);
			if (remaining <= 0) {
				return maxDrain;
			}
		}

		return maxDrain - remaining;
	}


	@Override
	public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
		return extract(resource, maxAmount, transaction, false);
	}

	@Override
	public Iterator<StorageView<FluidVariant>> iterator() {
		return new CombinedStorage<>(List.of(fluidHandlers)).iterator();
	}
}
