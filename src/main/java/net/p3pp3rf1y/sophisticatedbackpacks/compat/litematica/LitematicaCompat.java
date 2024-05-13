package net.p3pp3rf1y.sophisticatedbackpacks.compat.litematica;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackStorage;
import net.p3pp3rf1y.sophisticatedbackpacks.common.BackpackWrapperLookup;
import net.p3pp3rf1y.sophisticatedcore.compat.ICompat;
import net.p3pp3rf1y.sophisticatedcore.compat.litematica.LitematicaCompat.LitematicaWrapper;
import net.p3pp3rf1y.sophisticatedcore.compat.litematica.network.LitematicaPacketHandler;
import net.p3pp3rf1y.sophisticatedcore.inventory.InventoryHandler;
import net.p3pp3rf1y.sophisticatedcore.upgrades.UpgradeHandler;

import java.util.UUID;

import static net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems.BACKPACKS;
import static net.p3pp3rf1y.sophisticatedcore.compat.litematica.LitematicaCompat.LITEMATICA_CAPABILITY;

public class LitematicaCompat implements ICompat {
	public static void alwaysInit() {
		LitematicaPacketHandler.registerS2CMessage(BackpackContentsMessage.class, BackpackContentsMessage::new);
		LITEMATICA_CAPABILITY.registerForItems((stack, context) ->
						BackpackWrapperLookup.get(stack).map(wrapper -> new LitematicaWrapper(wrapper, (uuid) -> new BackpackContentsMessage(uuid, getBackpackTag(uuid))))
								.orElse(null),
				BACKPACKS);
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
