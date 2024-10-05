package net.p3pp3rf1y.sophisticatedbackpacks.network;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackStorage;
import net.p3pp3rf1y.sophisticatedcore.client.render.ClientStorageContentsTooltipBase;

import javax.annotation.Nullable;
import java.util.UUID;

public class BackpackContentsPacket implements FabricPacket {
	public static final PacketType<BackpackContentsPacket> TYPE = PacketType.create(new ResourceLocation(SophisticatedBackpacks.MOD_ID, "backpack_contents"), BackpackContentsPacket::new);
	private final UUID backpackUuid;
	@Nullable
	private final CompoundTag backpackContents;

	public BackpackContentsPacket(UUID backpackUuid, @Nullable CompoundTag backpackContents) {
		this.backpackUuid = backpackUuid;
		this.backpackContents = backpackContents;
	}

	public BackpackContentsPacket(FriendlyByteBuf buffer) {
		this(buffer.readUUID(), buffer.readNbt());
	}

	public void handle(LocalPlayer player, PacketSender responseSender) {
		if (backpackContents == null) {
			return;
		}

		BackpackStorage.get().setBackpackContents(backpackUuid, backpackContents);
		ClientStorageContentsTooltipBase.refreshContents();
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeUUID(backpackUuid);
		buffer.writeNbt(backpackContents);
	}

	@Override
	public PacketType<?> getType() {
		return TYPE;
	}
}
