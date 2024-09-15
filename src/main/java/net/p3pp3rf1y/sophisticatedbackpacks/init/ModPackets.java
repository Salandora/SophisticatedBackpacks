package net.p3pp3rf1y.sophisticatedbackpacks.init;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.p3pp3rf1y.sophisticatedbackpacks.network.AnotherPlayerBackpackOpenPacket;
import net.p3pp3rf1y.sophisticatedbackpacks.network.BackpackClosePacket;
import net.p3pp3rf1y.sophisticatedbackpacks.network.BackpackContentsPacket;
import net.p3pp3rf1y.sophisticatedbackpacks.network.BackpackOpenPacket;
import net.p3pp3rf1y.sophisticatedbackpacks.network.BlockPickPacket;
import net.p3pp3rf1y.sophisticatedbackpacks.network.BlockToolSwapPacket;
import net.p3pp3rf1y.sophisticatedbackpacks.network.EntityToolSwapPacket;
import net.p3pp3rf1y.sophisticatedbackpacks.network.InventoryInteractionPacket;
import net.p3pp3rf1y.sophisticatedbackpacks.network.RequestBackpackInventoryContentsPacket;
import net.p3pp3rf1y.sophisticatedbackpacks.network.SyncClientInfoPacket;
import net.p3pp3rf1y.sophisticatedbackpacks.network.UpgradeTogglePacket;

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
