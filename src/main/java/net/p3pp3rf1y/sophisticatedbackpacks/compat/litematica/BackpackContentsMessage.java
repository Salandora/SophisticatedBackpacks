package net.p3pp3rf1y.sophisticatedbackpacks.compat.litematica;

import com.google.common.collect.Maps;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackStorage;
import net.p3pp3rf1y.sophisticatedbackpacks.common.BackpackWrapperLookup;
import net.p3pp3rf1y.sophisticatedcore.compat.litematica.LitematicaCompat;
import net.p3pp3rf1y.sophisticatedcore.inventory.InventoryHandler;
import net.p3pp3rf1y.sophisticatedcore.network.SimplePacketBase;
import net.p3pp3rf1y.sophisticatedcore.upgrades.UpgradeHandler;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BackpackContentsMessage extends SimplePacketBase {
	public static BackpackContentsMessage create(List<UUID> uuids) {
		Map<UUID, CompoundTag> contents = Maps.toMap(uuids, BackpackContentsMessage::getBackpackTag);
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


	private final Map<UUID, CompoundTag> backpackContents;

	public BackpackContentsMessage(Map<UUID, CompoundTag> backpackContents) {
		this.backpackContents = backpackContents;
	}
	public BackpackContentsMessage(FriendlyByteBuf buffer) {
		this(buffer.readMap(FriendlyByteBuf::readUUID, FriendlyByteBuf::readNbt));
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeMap(this.backpackContents, FriendlyByteBuf::writeUUID, FriendlyByteBuf::writeNbt);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public boolean handle(Context context) {
		context.enqueueWork(() -> {
			Player player = context.getClientPlayer();
			if (player == null || this.backpackContents == null) {
				return;
			}

			this.backpackContents.forEach(BackpackStorage.get()::setBackpackContents);
			BackpackWrapperLookup.invalidateCache();
			if (LitematicaCompat.getTask() != null) {
				LitematicaCompat.getTask().incrementReceived(this.backpackContents.size());
			}
		});
		return true;
	}
}
