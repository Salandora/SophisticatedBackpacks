package net.p3pp3rf1y.sophisticatedbackpacks.init;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.p3pp3rf1y.sophisticatedbackpacks.network.*;

public class ModPayloads {
	private ModPayloads() {
	}

	public static void registerPayloads() {
		registerC2S(BackpackOpenPayload.TYPE, BackpackOpenPayload.STREAM_CODEC, BackpackOpenPayload::handlePayload);
		registerC2S(UpgradeTogglePayload.TYPE, UpgradeTogglePayload.STREAM_CODEC, UpgradeTogglePayload::handlePayload);
		registerC2S(RequestBackpackInventoryContentsPayload.TYPE, RequestBackpackInventoryContentsPayload.STREAM_CODEC, RequestBackpackInventoryContentsPayload::handlePayload);
		registerC2S(InventoryInteractionPayload.TYPE, InventoryInteractionPayload.STREAM_CODEC, InventoryInteractionPayload::handlePayload);
		registerC2S(BlockToolSwapPayload.TYPE, BlockToolSwapPayload.STREAM_CODEC, BlockToolSwapPayload::handlePayload);
		registerC2S(EntityToolSwapPayload.TYPE, EntityToolSwapPayload.STREAM_CODEC, EntityToolSwapPayload::handlePayload);
		registerC2S(BackpackClosePayload.TYPE, BackpackClosePayload.STREAM_CODEC, BackpackClosePayload::handlePayload);
		registerC2S(AnotherPlayerBackpackOpenPayload.TYPE, AnotherPlayerBackpackOpenPayload.STREAM_CODEC, AnotherPlayerBackpackOpenPayload::handlePayload);
		registerC2S(BlockPickPayload.TYPE, BlockPickPayload.STREAM_CODEC, BlockPickPayload::handlePayload);
		registerC2S(RequestPlayerSettingsPayload.TYPE, RequestPlayerSettingsPayload.STREAM_CODEC, RequestPlayerSettingsPayload::handlePayload);

		PayloadTypeRegistry.playS2C().register(BackpackContentsPayload.TYPE, BackpackContentsPayload.STREAM_CODEC);
		PayloadTypeRegistry.playS2C().register(SyncClientInfoPayload.TYPE, SyncClientInfoPayload.STREAM_CODEC);

		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			ClientPlayNetworking.registerGlobalReceiver(BackpackContentsPayload.TYPE, BackpackContentsPayload::handlePayload);
			ClientPlayNetworking.registerGlobalReceiver(SyncClientInfoPayload.TYPE, SyncClientInfoPayload::handlePayload);
		}
	}

	public static <T extends CustomPacketPayload> void registerC2S(CustomPacketPayload.Type<T> id, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, ServerPlayNetworking.PlayPayloadHandler<T> handler) {
		PayloadTypeRegistry.playC2S().register(id, codec);
		ServerPlayNetworking.registerGlobalReceiver(id, handler);
	}
}
