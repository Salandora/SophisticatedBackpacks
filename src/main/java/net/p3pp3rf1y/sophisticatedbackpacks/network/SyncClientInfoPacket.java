package net.p3pp3rf1y.sophisticatedbackpacks.network;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.IBackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.BackpackContainer;

import javax.annotation.Nullable;

public class SyncClientInfoPacket implements FabricPacket {
	public static final PacketType<SyncClientInfoPacket> TYPE = PacketType.create(new ResourceLocation(SophisticatedBackpacks.MOD_ID, "sync_client_info"), SyncClientInfoPacket::new);
	private final int slotIndex;
	@Nullable
	private final CompoundTag renderInfoNbt;
	private final int columnsTaken;

	public SyncClientInfoPacket(int slotNumber, @Nullable CompoundTag renderInfoNbt, int columnsTaken) {
		slotIndex = slotNumber;
		this.renderInfoNbt = renderInfoNbt;
		this.columnsTaken = columnsTaken;
	}

	public SyncClientInfoPacket(FriendlyByteBuf buffer) {
		this(buffer.readInt(), buffer.readNbt(), buffer.readInt());
	}

	public void handle(LocalPlayer player, PacketSender responseSender) {
		if (renderInfoNbt == null || !(player.containerMenu instanceof BackpackContainer)) {
			return;
		}
		ItemStack backpack = player.getInventory().items.get(slotIndex);
		IBackpackWrapper backpackWrapper = BackpackWrapper.fromData(backpack);
		backpackWrapper.getRenderInfo().deserializeFrom(renderInfoNbt);
		backpackWrapper.setColumnsTaken(columnsTaken, false);
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeInt(slotIndex);
		buffer.writeNbt(renderInfoNbt);
		buffer.writeInt(columnsTaken);
	}

	@Override
	public PacketType<?> getType() {
		return TYPE;
	}
}
