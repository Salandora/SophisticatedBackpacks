package net.p3pp3rf1y.sophisticatedbackpacks.upgrades.anvil;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModDataComponents;
import net.p3pp3rf1y.sophisticatedcore.api.IStorageWrapper;
import net.p3pp3rf1y.sophisticatedcore.init.ModCoreDataComponents;
import net.p3pp3rf1y.sophisticatedcore.upgrades.UpgradeWrapperBase;
import net.p3pp3rf1y.sophisticatedcore.util.ComponentItemHandler;

import java.util.function.Consumer;

public class AnvilUpgradeWrapper extends UpgradeWrapperBase<AnvilUpgradeWrapper, AnvilUpgradeItem> {
	private final ComponentItemHandler inventory;

	protected AnvilUpgradeWrapper(IStorageWrapper storageWrapper, ItemStack upgrade, Consumer<ItemStack> upgradeSaveHandler) {
		super(storageWrapper, upgrade, upgradeSaveHandler);

		inventory = new ComponentItemHandler(upgrade, DataComponents.CONTAINER, 2) {
			@Override
			protected void onContentsChanged(int slot, ItemStack oldStack, ItemStack newStack) {
				super.onContentsChanged(slot, oldStack, newStack);
				save();
			}

			@Override
			public boolean isItemValid(int slot, ItemStack stack) {
				return true;
			}
		};
	}

	public ComponentItemHandler getInventory() {
		return inventory;
	}

	@Override
	public boolean canBeDisabled() {
		return false;
	}

	public boolean shouldShiftClickIntoStorage() {
		return upgrade.sophisticatedCore_getOrDefault(ModCoreDataComponents.SHIFT_CLICK_INTO_STORAGE, true);
	}

	public void setShiftClickIntoStorage(boolean shiftClickIntoStorage) {
		upgrade.sophisticatedCore_set(ModCoreDataComponents.SHIFT_CLICK_INTO_STORAGE, shiftClickIntoStorage);
		save();
	}

	public String getItemName() {
		return upgrade.sophisticatedCore_getOrDefault(ModDataComponents.ITEM_NAME, "");
	}

	public void setItemName(String itemName) {
		upgrade.sophisticatedCore_set(ModDataComponents.ITEM_NAME, itemName);
		save();
	}
}
