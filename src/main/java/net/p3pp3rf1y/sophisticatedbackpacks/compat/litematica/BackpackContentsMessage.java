package net.p3pp3rf1y.sophisticatedbackpacks.compat.litematica;

import me.pepperbell.simplenetworking.S2CPacket;
import me.pepperbell.simplenetworking.SimpleChannel;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackStorage;
import net.p3pp3rf1y.sophisticatedbackpacks.common.BackpackWrapperLookup;
import net.p3pp3rf1y.sophisticatedcore.compat.litematica.LitematicaHelper;

import java.util.Map;
import java.util.UUID;

public class BackpackContentsMessage implements S2CPacket {
	private final Map<UUID, CompoundTag> backpackContents;

	public BackpackContentsMessage(Map<UUID, CompoundTag> backpackContents) {
		this.backpackContents = backpackContents;
	}
	public BackpackContentsMessage(FriendlyByteBuf buffer) {
		this(buffer.readMap(FriendlyByteBuf::readUUID, FriendlyByteBuf::readNbt));
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeMap(this.backpackContents, FriendlyByteBuf::writeUUID, FriendlyByteBuf::writeNbt);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void handle(Minecraft client, ClientPacketListener listener, PacketSender responseSender, SimpleChannel channel) {
		client.execute(() -> {
			if (client.player == null || this.backpackContents == null) {
				return;
			}

			this.backpackContents.forEach(BackpackStorage.get()::setBackpackContents);
			BackpackWrapperLookup.invalidateCache();
			LitematicaHelper.incrementReceived(this.backpackContents.size());
		});
	}
}
