package net.p3pp3rf1y.sophisticatedbackpacks.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.util.PlayerInventoryProvider;
import net.p3pp3rf1y.sophisticatedcore.upgrades.IUpgradeWrapper;

import java.util.Map;

public class UpgradeTogglePacket implements FabricPacket {
	public static final PacketType<UpgradeTogglePacket> TYPE = PacketType.create(new ResourceLocation(SophisticatedBackpacks.MOD_ID, "upgrade_toggle"), UpgradeTogglePacket::new);
	private final int upgradeSlot;

	public UpgradeTogglePacket(int upgradeSlot) {
		this.upgradeSlot = upgradeSlot;
	}

	public UpgradeTogglePacket(FriendlyByteBuf buffer) {
		this(buffer.readInt());
	}

	public void handle(ServerPlayer player, PacketSender responseSender) {
		PlayerInventoryProvider.get().runOnBackpacks(player, (backpack, inventoryName, identifier, slot) -> {
			Map<Integer, IUpgradeWrapper> slotWrappers = BackpackWrapper.fromData(backpack).getUpgradeHandler().getSlotWrappers();
			if (slotWrappers.containsKey(upgradeSlot)) {
				IUpgradeWrapper upgradeWrapper = slotWrappers.get(upgradeSlot);
				if (upgradeWrapper.canBeDisabled()) {
					upgradeWrapper.setEnabled(!upgradeWrapper.isEnabled());
					String translKey = upgradeWrapper.isEnabled() ? "gui.sophisticatedbackpacks.status.upgrade_switched_on" : "gui.sophisticatedbackpacks.status.upgrade_switched_off";
					player.displayClientMessage(Component.translatable(translKey, upgradeWrapper.getUpgradeStack().getHoverName()), true);
				}
			}
			return true;
		});
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeInt(upgradeSlot);
	}

	@Override
	public PacketType<?> getType() {
		return TYPE;
	}
}