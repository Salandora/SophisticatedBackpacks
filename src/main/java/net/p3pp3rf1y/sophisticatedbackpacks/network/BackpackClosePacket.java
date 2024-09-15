package net.p3pp3rf1y.sophisticatedbackpacks.network;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.BackpackContainer;

@SuppressWarnings("java:S1118")
public class BackpackClosePacket implements FabricPacket {
	public static final PacketType<BackpackClosePacket> TYPE = PacketType.create(new ResourceLocation(SophisticatedBackpacks.MOD_ID, "backpack_close"), BackpackClosePacket::new);

	public BackpackClosePacket(FriendlyByteBuf buffer) {
	}

	public void handle(ServerPlayer player, PacketSender responseSender) {
		if (player.containerMenu instanceof BackpackContainer) {
			player.closeContainer();
		}
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		//noop
	}

	@Override
	public PacketType<?> getType() {
		return TYPE;
	}
}
