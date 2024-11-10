package net.p3pp3rf1y.sophisticatedbackpacks.compat.litematica;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.loader.api.FabricLoader;

public class LitematicaPayloads {
	public static void registerPackets() {
		PayloadTypeRegistry.playS2C().register(LitematicaBackpackContentsPayload.TYPE, LitematicaBackpackContentsPayload.STREAM_CODEC);

		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			ClientPlayNetworking.registerGlobalReceiver(LitematicaBackpackContentsPayload.TYPE, LitematicaBackpackContentsPayload::handlePayload);
		}
	}
}
