package net.p3pp3rf1y.sophisticatedbackpacks.upgrades.deposit;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.p3pp3rf1y.sophisticatedbackpacks.api.IItemHandlerInteractionUpgrade;
import net.p3pp3rf1y.sophisticatedcore.api.IStorageWrapper;
import net.p3pp3rf1y.sophisticatedcore.inventory.FilteredItemHandler;
import net.p3pp3rf1y.sophisticatedcore.upgrades.IFilteredUpgrade;
import net.p3pp3rf1y.sophisticatedcore.upgrades.UpgradeWrapperBase;
import net.p3pp3rf1y.sophisticatedcore.util.InventoryHelper;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class DepositUpgradeWrapper extends UpgradeWrapperBase<DepositUpgradeWrapper, DepositUpgradeItem>
		implements IFilteredUpgrade, IItemHandlerInteractionUpgrade {
	private final DepositFilterLogic filterLogic;

	public DepositUpgradeWrapper(IStorageWrapper backpackWrapper, ItemStack upgrade, Consumer<ItemStack> upgradeSaveHandler) {
		super(backpackWrapper, upgrade, upgradeSaveHandler);
		filterLogic = new DepositFilterLogic(upgrade, upgradeSaveHandler, upgradeItem.getFilterSlotCount());
	}

	@Override
	public DepositFilterLogic getFilterLogic() {
		return filterLogic;
	}

	@Override
	public void onHandlerInteract(SlottedStorage<ItemVariant> itemHandler, Player player) {
		if (filterLogic.getDepositFilterType() == DepositFilterType.INVENTORY) {
			filterLogic.setInventory(itemHandler);
		}

		AtomicInteger stacksAdded = new AtomicInteger(0);
		InventoryHelper.transfer(storageWrapper.getInventoryForUpgradeProcessing(),
				new FilteredItemHandler<>(itemHandler, Collections.singletonList(filterLogic), Collections.emptyList()),
				s -> stacksAdded.incrementAndGet(), null);

		int stacksDeposited = stacksAdded.get();
		String translKey = stacksDeposited > 0 ? "gui.sophisticatedbackpacks.status.stacks_deposited" : "gui.sophisticatedbackpacks.status.nothing_to_deposit";
		player.displayClientMessage(Component.translatable(translKey, stacksDeposited), true);
	}
}
