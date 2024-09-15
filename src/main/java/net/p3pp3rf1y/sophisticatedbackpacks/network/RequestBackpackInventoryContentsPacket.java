package net.p3pp3rf1y.sophisticatedbackpacks.network;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackStorage;
import net.p3pp3rf1y.sophisticatedcore.inventory.InventoryHandler;
import net.p3pp3rf1y.sophisticatedcore.network.PacketHelper;
import net.p3pp3rf1y.sophisticatedcore.upgrades.UpgradeHandler;

import java.util.UUID;

public class RequestBackpackInventoryContentsPacket implements FabricPacket {
	public static final PacketType<RequestBackpackInventoryContentsPacket> TYPE = PacketType.create(new ResourceLocation(SophisticatedBackpacks.MOD_ID, "request_backpack_inventory_contents"), RequestBackpackInventoryContentsPacket::new);
	private final UUID backpackUuid;

	public RequestBackpackInventoryContentsPacket(UUID backpackUuid) {
		this.backpackUuid = backpackUuid;
	}

	public RequestBackpackInventoryContentsPacket(FriendlyByteBuf buffer) {
		this(buffer.readUUID());
	}

	public void handle(ServerPlayer player, PacketSender responseSender) {
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
		PacketHelper.sendToPlayer(new BackpackContentsPacket(backpackUuid, inventoryContents), player);
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeUUID(backpackUuid);
	}

	@Override
	public PacketType<?> getType() {
		return TYPE;
	}
}
