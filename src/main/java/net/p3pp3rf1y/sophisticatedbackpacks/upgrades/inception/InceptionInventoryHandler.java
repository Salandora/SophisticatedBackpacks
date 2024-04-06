package net.p3pp3rf1y.sophisticatedbackpacks.upgrades.inception;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedbackpacks.util.CombinedInvWrapper;
import net.p3pp3rf1y.sophisticatedcore.inventory.IItemHandlerSimpleInserter;
import net.p3pp3rf1y.sophisticatedcore.inventory.ITrackedContentsItemHandler;
import net.p3pp3rf1y.sophisticatedcore.inventory.ItemStackKey;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class InceptionInventoryHandler implements ITrackedContentsItemHandler {
	private CombinedInvWrapper<ITrackedContentsItemHandler> combinedInventories;
	private final ITrackedContentsItemHandler wrappedInventoryHandler;
	private final InventoryOrder inventoryOrder;
	private final SubBackpacksHandler subBackpacksHandler;
	private List<ITrackedContentsItemHandler> handlers;
	private int[] baseIndex;

	public InceptionInventoryHandler(ITrackedContentsItemHandler wrappedInventoryHandler, InventoryOrder inventoryOrder, SubBackpacksHandler subBackpacksHandler) {
		this.wrappedInventoryHandler = wrappedInventoryHandler;
		this.inventoryOrder = inventoryOrder;
		this.subBackpacksHandler = subBackpacksHandler;
		subBackpacksHandler.addRefreshListener(sbs -> refreshHandlerDelegate());

		refreshHandlerDelegate();
	}

	private void refreshHandlerDelegate() {
		handlers = new ArrayList<>();
		if (inventoryOrder == InventoryOrder.MAIN_FIRST) {
			handlers.add(wrappedInventoryHandler);
		}
		subBackpacksHandler.getSubBackpacks().forEach(sbp -> handlers.add(sbp.getInventoryForInputOutput()));
		if (inventoryOrder == InventoryOrder.INCEPTED_FIRST) {
			handlers.add(wrappedInventoryHandler);
		}
		combinedInventories = new CombinedInvWrapper<>(handlers);

		baseIndex = new int[handlers.size()];
		int index = 0;
		for (int i = 0; i < handlers.size(); i++) {
			index += handlers.get(i).getSlotCount();
			baseIndex[i] = index;
		}
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
		combinedInventories.setStackInSlot(slot, stack);
	}

	@Override
	public int getSlotCount() {
		return combinedInventories.getSlotCount();
	}

	@Override
	public SingleSlotStorage<ItemVariant> getSlot(int slot) {
		return combinedInventories.getSlot(slot);
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return combinedInventories.getStackInSlot(slot);
	}

	@Override
	public long insertSlot(int slot, ItemVariant resource, long maxAmount, TransactionContext ctx) {
		return combinedInventories.insertSlot(slot, resource, maxAmount, ctx);
	}

	@Override
	public long extractSlot(int slot, ItemVariant resource, long maxAmount, TransactionContext ctx) {
		return combinedInventories.extractSlot(slot, resource, maxAmount, ctx);
	}

	@Override
	public int getSlotLimit(int slot) {
		return combinedInventories.getSlotLimit(slot);
	}

	@Override
	public boolean isItemValid(int slot, ItemVariant resource, int count) {
		return combinedInventories.isItemValid(slot, resource, count);
	}

	@Override
	public long insert(ItemVariant resource, long maxAmount, TransactionContext ctx) {
		long remaining = maxAmount;
		for (IItemHandlerSimpleInserter handler : handlers) {
			remaining -= handler.insert(resource, remaining, ctx);
			if (remaining <= 0) {
				break;
			}
		}

		return maxAmount - remaining;
	}

	@Override
	public long extract(ItemVariant resource, long maxAmount, TransactionContext ctx) {
		long remaining = maxAmount;
		for (IItemHandlerSimpleInserter handler : handlers) {
			remaining -= handler.extract(resource, remaining, ctx);
			if (remaining <= 0) {
				break;
			}
		}

		return maxAmount - remaining;
	}

	@Override
	public Iterator<StorageView<ItemVariant>> iterator() {
		return combinedInventories.iterator();
	}

	@Override
	public Set<ItemStackKey> getTrackedStacks() {
		Set<ItemStackKey> ret = new HashSet<>();
		handlers.forEach(h -> ret.addAll(h.getTrackedStacks()));
		return ret;
	}

	@Override
	public void registerTrackingListeners(Consumer<ItemStackKey> onAddStackKey, Consumer<ItemStackKey> onRemoveStackKey, Runnable onAddFirstEmptySlot, Runnable onRemoveLastEmptySlot) {
		handlers.forEach(h -> h.registerTrackingListeners(onAddStackKey, onRemoveStackKey, onAddFirstEmptySlot, onRemoveLastEmptySlot));
	}

	@Override
	public void unregisterStackKeyListeners() {
		handlers.forEach(ITrackedContentsItemHandler::unregisterStackKeyListeners);
	}

	@Override
	public boolean hasEmptySlots() {
		return handlers.stream().anyMatch(ITrackedContentsItemHandler::hasEmptySlots);
	}

	@Override
	public int getInternalSlotLimit(int slot) {
		int index = getIndexForSlot(slot);
		ITrackedContentsItemHandler handler = getHandlerFromIndex(index);
		int localSlot = getSlotFromIndex(slot, index);
		return handler.getInternalSlotLimit(localSlot);
	}

	private int getIndexForSlot(int slot) {
		if (slot < 0) {return -1;}

		for (int i = 0; i < baseIndex.length; i++) {
			if (slot - baseIndex[i] < 0) {
				return i;
			}
		}
		return -1;
	}

	private int getSlotFromIndex(int slot, int index) {
		if (index <= 0 || index >= baseIndex.length) {
			return slot;
		}
		return slot - baseIndex[index - 1];
	}

	private ITrackedContentsItemHandler getHandlerFromIndex(int index) {
		if (index < 0 || index >= handlers.size()) {
			return handlers.get(0);
		}
		return handlers.get(index);
	}
}
