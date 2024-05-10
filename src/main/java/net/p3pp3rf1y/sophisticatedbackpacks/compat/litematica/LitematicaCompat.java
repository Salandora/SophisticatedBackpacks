package net.p3pp3rf1y.sophisticatedbackpacks.compat.litematica;

import com.google.common.collect.Maps;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackStorage;
import net.p3pp3rf1y.sophisticatedcore.compat.ICompat;
import net.p3pp3rf1y.sophisticatedcore.compat.litematica.network.LitematicaPacketHandler;
import net.p3pp3rf1y.sophisticatedcore.inventory.InventoryHandler;
import net.p3pp3rf1y.sophisticatedcore.upgrades.UpgradeHandler;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems.BACKPACKS;
import static net.p3pp3rf1y.sophisticatedcore.compat.litematica.LitematicaCompat.REQUEST_CONTENTS_CAPABILITY;

public class LitematicaCompat implements ICompat {
	public static void alwaysInit() {
		LitematicaPacketHandler.registerS2CMessage(BackpackContentsMessage.class, BackpackContentsMessage::new);
		REQUEST_CONTENTS_CAPABILITY.registerForItems((stack, context) -> LitematicaCompat::createBackpackContentsMessage, BACKPACKS);
	}

	public static BackpackContentsMessage createBackpackContentsMessage(List<UUID> uuids) {
		Map<UUID, CompoundTag> contents = Maps.toMap(uuids, LitematicaCompat::getBackpackTag);
		return new BackpackContentsMessage(contents);
	}
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
	}
}
