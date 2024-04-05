package net.p3pp3rf1y.sophisticatedbackpacks.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedbackpacks.Config;
import net.p3pp3rf1y.sophisticatedbackpacks.common.BackpackWrapperLookup;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.BackpackContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.BackpackContext;
import net.p3pp3rf1y.sophisticatedbackpacks.settings.BackpackMainSettingsCategory;
import net.p3pp3rf1y.sophisticatedbackpacks.util.PlayerInventoryProvider;
import net.p3pp3rf1y.sophisticatedcore.network.SimplePacketBase;
import net.p3pp3rf1y.sophisticatedcore.settings.SettingsManager;
import net.p3pp3rf1y.sophisticatedcore.settings.main.MainSettingsCategory;
import net.p3pp3rf1y.sophisticatedcore.util.MenuProviderHelper;

public class AnotherPlayerBackpackOpenMessage extends SimplePacketBase {
	private final int anotherPlayerId;

	public AnotherPlayerBackpackOpenMessage(int anotherPlayerId) {
		this.anotherPlayerId = anotherPlayerId;
	}

	public AnotherPlayerBackpackOpenMessage(FriendlyByteBuf buffer) { this(buffer.readInt()); }

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeInt(this.anotherPlayerId);
	}

	@Override
	public boolean handle(Context context) {
		context.enqueueWork(() -> {
			ServerPlayer player = context.getSender();
			if (player == null || Boolean.FALSE.equals(Config.SERVER.allowOpeningOtherPlayerBackpacks.get())) {
				return;
			}

			if (player.level().getEntity(anotherPlayerId) instanceof Player anotherPlayer) {
				PlayerInventoryProvider.get().runOnBackpacks(anotherPlayer, (backpack, inventoryName, identifier, slot) -> {
					if (canAnotherPlayerOpenBackpack(anotherPlayer, backpack)) {

						BackpackContext.AnotherPlayer backpackContext = new BackpackContext.AnotherPlayer(inventoryName, identifier, slot, anotherPlayer);
						player.openMenu(MenuProviderHelper.createMenuProvider((w, bpc, pl) -> new BackpackContainer(w, pl, backpackContext), backpackContext, backpack.getHoverName()));
					} else {
						player.displayClientMessage(Component.translatable("gui.sophisticatedbackpacks.status.backpack_cannot_be_open_by_another_player"), true);
					}
					return true;
				}, true);
			}
		});
		return true;
	}

	private static boolean canAnotherPlayerOpenBackpack(Player anotherPlayer, ItemStack backpack) {
		return BackpackWrapperLookup.get(backpack).map(wrapper -> {
			MainSettingsCategory<?> category = wrapper.getSettingsHandler().getGlobalSettingsCategory();
			return SettingsManager.getSettingValue(anotherPlayer, category.getPlayerSettingsTagName(), category, BackpackMainSettingsCategory.ANOTHER_PLAYER_CAN_OPEN);
		}).orElse(false);
	}
}
