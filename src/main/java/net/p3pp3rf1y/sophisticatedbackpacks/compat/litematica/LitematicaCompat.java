package net.p3pp3rf1y.sophisticatedbackpacks.compat.litematica;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackStorage;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.network.BackpackContentsPacket;
import net.p3pp3rf1y.sophisticatedcore.compat.ICompat;
import net.p3pp3rf1y.sophisticatedcore.compat.litematica.LitematicaCompat.LitematicaWrapper;
import net.p3pp3rf1y.sophisticatedcore.inventory.InventoryHandler;
import net.p3pp3rf1y.sophisticatedcore.upgrades.UpgradeHandler;

import java.util.UUID;

import static net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems.BACKPACKS;
import static net.p3pp3rf1y.sophisticatedcore.compat.litematica.LitematicaCompat.LITEMATICA_CAPABILITY;

public class LitematicaCompat implements ICompat {
	private static CompoundTag getBackpackTag(UUID backpackUuid) {
		CompoundTag backpackContents = BackpackStorage.get().getOrCreateBackpackContents(backpackUuid);

		CompoundTag inventoryContents = new CompoundTag();
		Tag inventoryNbt = backpackContents.get(InventoryHandler.INVENTORY_TAG);
		if (inventoryNbt != null) {
			inventoryContents.put(InventoryHandler.INVENTORY_TAG, inventoryNbt);
		}
		Tag upgradeNbt = backpackContents.get(UpgradeHandler.UPGRADE_INVENTORY_TAG);
		if (upgradeNbt != null) {
			inventoryContents.put(UpgradeHandler.UPGRADE_INVENTORY_TAG, upgradeNbt);
		}

		return inventoryContents;
	}

	@Override
	public void setup() {
		LITEMATICA_CAPABILITY.registerForItems((stack, context) -> new LitematicaWrapper(BackpackWrapper.fromData(stack), (uuid) -> new BackpackContentsPacket(uuid, getBackpackTag(uuid))), BACKPACKS);
	}
}
