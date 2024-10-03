package net.p3pp3rf1y.sophisticatedbackpacks.upgrades.inception;

import team.reborn.energy.api.EnergyStorage;

import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public class InceptionEnergyStorage implements EnergyStorage {
	@Nullable
	private final EnergyStorage wrappedEnergyStorage;
	private final InventoryOrder inventoryOrder;
	private final SubBackpacksHandler subBackpacksHandler;

	private EnergyStorage[] energyStorages;

	public InceptionEnergyStorage(@Nullable EnergyStorage wrappedEnergyStorage, InventoryOrder inventoryOrder, SubBackpacksHandler subBackpacksHandler) {
		this.wrappedEnergyStorage = wrappedEnergyStorage;
		this.inventoryOrder = inventoryOrder;
		this.subBackpacksHandler = subBackpacksHandler;
		subBackpacksHandler.addRefreshListener(sbs -> refreshHandlers());
		refreshHandlers();
	}

	private void refreshHandlers() {
		List<EnergyStorage> storages = new ArrayList<>();
		if (wrappedEnergyStorage != null && inventoryOrder == InventoryOrder.MAIN_FIRST) {
			storages.add(wrappedEnergyStorage);
		}
		subBackpacksHandler.getSubBackpacks().forEach(sbp -> sbp.getEnergyStorage().ifPresent(storages::add));
		if (wrappedEnergyStorage != null && inventoryOrder == InventoryOrder.INCEPTED_FIRST) {
			storages.add(wrappedEnergyStorage);
		}
		energyStorages = storages.toArray(new EnergyStorage[] {});
	}

	@Override
	public long insert(long maxAmount, TransactionContext transaction) {
		long totalReceived = 0;
		for (EnergyStorage storage : energyStorages) {
			totalReceived += storage.insert(maxAmount - totalReceived, transaction);
			if (totalReceived == maxAmount) {
				break;
			}
		}

		return totalReceived;
	}

	@Override
	public long extract(long maxAmount, TransactionContext transaction) {
		long totalExtracted = 0;
		for (EnergyStorage storage : energyStorages) {
			totalExtracted += storage.extract(maxAmount - totalExtracted, transaction);
			if (totalExtracted == maxAmount) {
				break;
			}
		}

		return totalExtracted;
	}

	@Override
	public long getAmount() {
		long totalEnergyStored = 0;
		for (EnergyStorage storage : energyStorages) {
			totalEnergyStored += storage.getAmount();
		}
		return totalEnergyStored;
	}

	@Override
	public long getCapacity() {
		long totalMaxEnergy = 0;

		for (EnergyStorage storage : energyStorages) {
			if (totalMaxEnergy > Integer.MAX_VALUE - storage.getCapacity()) {
				return Integer.MAX_VALUE;
			}

			totalMaxEnergy += storage.getCapacity();
		}

		return totalMaxEnergy;
	}

	@Override
	public boolean supportsExtraction() {
		return energyStorages.length > 0;
	}

	@Override
	public boolean supportsInsertion() {
		return energyStorages.length > 0;
	}
}
