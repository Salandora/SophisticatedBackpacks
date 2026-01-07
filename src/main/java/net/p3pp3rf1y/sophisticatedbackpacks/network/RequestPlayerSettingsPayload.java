package net.p3pp3rf1y.sophisticatedbackpacks.network;

import com.github.salandora.sophisticatedfabriclib.network.api.v1.IPayloadContext;
import com.github.salandora.sophisticatedfabriclib.network.api.v1.PacketDistributor;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.settings.BackpackMainSettingsCategory;
import net.p3pp3rf1y.sophisticatedcore.network.SyncPlayerSettingsPayload;
import net.p3pp3rf1y.sophisticatedcore.settings.SettingsManager;
import net.p3pp3rf1y.sophisticatedcore.util.StreamCodecHelper;

public record RequestPlayerSettingsPayload() implements CustomPacketPayload {
	public static final Type<RequestPlayerSettingsPayload> TYPE = new Type<>(SophisticatedBackpacks.getRL("request_player_settings"));
	public static final StreamCodec<ByteBuf, RequestPlayerSettingsPayload> STREAM_CODEC = StreamCodecHelper.singleton(RequestPlayerSettingsPayload::new);

	public static void handlePayload(@SuppressWarnings("unused") RequestPlayerSettingsPayload payload, IPayloadContext context) {
		Player player = context.player();
		String playerTagName = BackpackMainSettingsCategory.SOPHISTICATED_BACKPACK_SETTINGS_PLAYER_TAG;
		if (player instanceof ServerPlayer serverPlayer) {
			PacketDistributor.sendToPlayer(serverPlayer, new SyncPlayerSettingsPayload(playerTagName, SettingsManager.getPlayerSettingsTag(player, playerTagName)));
		}
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
