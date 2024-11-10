package net.p3pp3rf1y.sophisticatedbackpacks.network;

import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.BackpackContainer;
import net.p3pp3rf1y.sophisticatedcore.util.StreamCodecHelper;

public record BackpackClosePayload() implements CustomPacketPayload {
	public static final Type<BackpackClosePayload> TYPE = new Type<>(SophisticatedBackpacks.getRL("backpack_close"));
	public static final StreamCodec<ByteBuf, BackpackClosePayload> STREAM_CODEC = StreamCodecHelper.singleton(BackpackClosePayload::new);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handlePayload(BackpackClosePayload payload, ServerPlayNetworking.Context context) {
		Player player = context.player();
		if (player.containerMenu instanceof BackpackContainer) {
			player.closeContainer();
		}
	}
}
