package net.p3pp3rf1y.sophisticatedbackpacks.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.api.IBlockPickResponseUpgrade;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.IBackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.util.PlayerInventoryProvider;

public class BlockPickPacket implements FabricPacket {
	public static final PacketType<BlockPickPacket> TYPE = PacketType.create(new ResourceLocation(SophisticatedBackpacks.MOD_ID, "block_pick"), BlockPickPacket::new);
	private final ItemStack filter;

	public BlockPickPacket(ItemStack filter) {
		this.filter = filter;
	}

	public BlockPickPacket(FriendlyByteBuf buffer) {
		this(buffer.readItem());
	}

	public void handle(ServerPlayer player, PacketSender responseSender) {
		PlayerInventoryProvider.get().runOnBackpacks(player, (backpack, inventoryHandlerName, identifier, slot) -> {
			IBackpackWrapper wrapper = BackpackWrapper.fromData(backpack);
			for (IBlockPickResponseUpgrade upgrade : wrapper.getUpgradeHandler().getWrappersThatImplement(IBlockPickResponseUpgrade.class)) {
				if (upgrade.pickBlock(player, filter)) {
					return true;
				}
			}
			return false;
		});
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeItem(filter);
	}

	@Override
	public PacketType<?> getType() {
		return TYPE;
	}
}
