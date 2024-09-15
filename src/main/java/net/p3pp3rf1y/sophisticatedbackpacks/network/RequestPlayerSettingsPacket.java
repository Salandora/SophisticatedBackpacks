package net.p3pp3rf1y.sophisticatedbackpacks.network;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.settings.BackpackMainSettingsCategory;
import net.p3pp3rf1y.sophisticatedcore.network.PacketHelper;
import net.p3pp3rf1y.sophisticatedcore.network.SyncPlayerSettingsPacket;
import net.p3pp3rf1y.sophisticatedcore.settings.SettingsManager;

public class RequestPlayerSettingsPacket implements FabricPacket {
	public static final PacketType<RequestPlayerSettingsPacket> TYPE = PacketType.create(new ResourceLocation(SophisticatedBackpacks.MOD_ID, "request_player_settings"), RequestPlayerSettingsPacket::new);

	public RequestPlayerSettingsPacket() {
	}

	public RequestPlayerSettingsPacket(FriendlyByteBuf buffer) {
	}

	public void handle(ServerPlayer player, PacketSender responseSender) {
		String playerTagName = BackpackMainSettingsCategory.SOPHISTICATED_BACKPACK_SETTINGS_PLAYER_TAG;
		PacketHelper.sendToPlayer(new SyncPlayerSettingsPacket(playerTagName, SettingsManager.getPlayerSettingsTag(player, playerTagName)), (ServerPlayer) player);
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
