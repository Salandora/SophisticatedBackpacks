package net.p3pp3rf1y.sophisticatedbackpacks.init;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.p3pp3rf1y.sophisticatedbackpacks.network.*;

public class ModPackets {
	private ModPackets() {
	}

	public static void registerPackets() {
		ServerPlayNetworking.registerGlobalReceiver(BackpackOpenPacket.TYPE, BackpackOpenPacket::handle);
		ServerPlayNetworking.registerGlobalReceiver(UpgradeTogglePacket.TYPE, UpgradeTogglePacket::handle);
		ServerPlayNetworking.registerGlobalReceiver(RequestBackpackInventoryContentsPacket.TYPE, RequestBackpackInventoryContentsPacket::handle);
		ServerPlayNetworking.registerGlobalReceiver(InventoryInteractionPacket.TYPE, InventoryInteractionPacket::handle);
		ServerPlayNetworking.registerGlobalReceiver(BlockToolSwapPacket.TYPE, BlockToolSwapPacket::handle);
		ServerPlayNetworking.registerGlobalReceiver(EntityToolSwapPacket.TYPE, EntityToolSwapPacket::handle);
		ServerPlayNetworking.registerGlobalReceiver(BackpackClosePacket.TYPE, BackpackClosePacket::handle);
		ServerPlayNetworking.registerGlobalReceiver(AnotherPlayerBackpackOpenPacket.TYPE, AnotherPlayerBackpackOpenPacket::handle);
		ServerPlayNetworking.registerGlobalReceiver(BlockPickPacket.TYPE, BlockPickPacket::handle);
	}

	@Environment(EnvType.CLIENT)
	public static void registerClientPackets() {
		ClientPlayNetworking.registerGlobalReceiver(BackpackContentsPacket.TYPE, BackpackContentsPacket::handle);
		ClientPlayNetworking.registerGlobalReceiver(SyncClientInfoPacket.TYPE, SyncClientInfoPacket::handle);
	}
}
