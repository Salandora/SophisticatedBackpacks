package net.p3pp3rf1y.sophisticatedbackpacks.compat.litematica;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackStorage;
import net.p3pp3rf1y.sophisticatedcore.compat.litematica.LitematicaHelper;

import java.util.UUID;

public class BackpackContentsMessage implements FabricPacket {
	public static final PacketType<BackpackContentsMessage> TYPE = PacketType.create(new ResourceLocation(SophisticatedBackpacks.MOD_ID, "litematica_backpack_contents"), BackpackContentsMessage::new);

	private final UUID backpackUuid;
	private final CompoundTag backpackContents;

	public BackpackContentsMessage(UUID backpackUuid, CompoundTag backpackContents) {
		this.backpackUuid = backpackUuid;
		this.backpackContents = backpackContents;
	}
	public BackpackContentsMessage(FriendlyByteBuf buffer) {
		this(buffer.readUUID(), buffer.readNbt());
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeUUID(this.backpackUuid);
		buffer.writeNbt(this.backpackContents);
	}

	public void handle(LocalPlayer player, PacketSender responseSender) {
		if (this.backpackContents == null) {
			return;
		}

		BackpackStorage.get().setBackpackContents(this.backpackUuid, this.backpackContents);
		LitematicaHelper.incrementReceived(1);
	}

	@Override
	public PacketType<?> getType() {
		return TYPE;
	}
}
