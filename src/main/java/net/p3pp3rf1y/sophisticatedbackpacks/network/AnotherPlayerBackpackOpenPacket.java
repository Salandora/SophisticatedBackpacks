package net.p3pp3rf1y.sophisticatedbackpacks.network;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedbackpacks.Config;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.BackpackContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.BackpackContext;
import net.p3pp3rf1y.sophisticatedbackpacks.settings.BackpackMainSettingsCategory;
import net.p3pp3rf1y.sophisticatedbackpacks.util.PlayerInventoryProvider;
import net.p3pp3rf1y.sophisticatedcore.settings.SettingsManager;
import net.p3pp3rf1y.sophisticatedcore.settings.main.MainSettingsCategory;
import net.p3pp3rf1y.sophisticatedcore.util.MenuProviderHelper;

public class AnotherPlayerBackpackOpenPacket implements FabricPacket {
	public static final PacketType<AnotherPlayerBackpackOpenPacket> TYPE = PacketType.create(new ResourceLocation(SophisticatedBackpacks.MOD_ID, "another_player_backpack_open"), AnotherPlayerBackpackOpenPacket::new);
	private final int anotherPlayerId;

	public AnotherPlayerBackpackOpenPacket(int anotherPlayerId) {
		this.anotherPlayerId = anotherPlayerId;
	}

	public AnotherPlayerBackpackOpenPacket(FriendlyByteBuf buffer) {
		this.anotherPlayerId = buffer.readInt();
	}

	public void handle(ServerPlayer player, PacketSender responseSender) {
		if (Boolean.FALSE.equals(Config.SERVER.allowOpeningOtherPlayerBackpacks.get())) {
			return;
		}

		if (player.level().getEntity(anotherPlayerId) instanceof Player anotherPlayer) {
			PlayerInventoryProvider.get().runOnBackpacks(anotherPlayer, (backpack, inventoryName, identifier, slot) -> {
				if (canAnotherPlayerOpenBackpack(anotherPlayer, backpack)) {

					BackpackContext.AnotherPlayer backpackContext = new BackpackContext.AnotherPlayer(inventoryName, identifier, slot, anotherPlayer);
					player.openMenu(MenuProviderHelper.createMenuProvider((w, p, pl) -> new BackpackContainer(w, pl, backpackContext), backpackContext, backpack.getHoverName()));
				} else {
					player.displayClientMessage(Component.translatable("gui.sophisticatedbackpacks.status.backpack_cannot_be_open_by_another_player"), true);
				}
				return true;
			}, true);
		}
	}

	private boolean canAnotherPlayerOpenBackpack(Player anotherPlayer, ItemStack backpack) {
		MainSettingsCategory<?> category = BackpackWrapper.fromData(backpack).getSettingsHandler().getGlobalSettingsCategory();
		return SettingsManager.getSettingValue(anotherPlayer, category.getPlayerSettingsTagName(), category, BackpackMainSettingsCategory.ANOTHER_PLAYER_CAN_OPEN);
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeInt(anotherPlayerId);
	}

	@Override
	public PacketType<?> getType() {
		return TYPE;
	}
}
