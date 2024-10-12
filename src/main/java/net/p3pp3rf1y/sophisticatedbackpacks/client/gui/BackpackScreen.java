package net.p3pp3rf1y.sophisticatedbackpacks.client.gui;

import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.client.KeybindHandler;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.BackpackContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.network.BackpackOpenPacket;
import net.p3pp3rf1y.sophisticatedcore.client.gui.StorageScreenBase;
import net.p3pp3rf1y.sophisticatedcore.network.PacketHelper;

public class BackpackScreen extends StorageScreenBase<BackpackContainer> {
	public static BackpackScreen constructScreen(BackpackContainer screenContainer, Inventory inv, Component title) {
		return new BackpackScreen(screenContainer, inv, title);
	}

	public BackpackScreen(BackpackContainer screenContainer, Inventory inv, Component titleIn) {
		super(screenContainer, inv, titleIn);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == 256 || KeybindHandler.BACKPACK_OPEN_KEYBIND.isDown()) {
			if (getMenu().isFirstLevelStorage() && (keyCode == 256 || mouseNotOverBackpack())) {
				if (getMenu().getBackpackContext().wasOpenFromInventory()) {
					this.minecraft.player.closeContainer();
					this.minecraft.setScreen(new InventoryScreen(this.minecraft.player));
				} else {
					onClose();
				}
				return true;
			} else if (!getMenu().isFirstLevelStorage()) {
				PacketHelper.sendToServer(new BackpackOpenPacket());
				return true;
			}
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	private boolean mouseNotOverBackpack() {
		Slot selectedSlot = this.hoveredSlot;
		return selectedSlot == null || !(selectedSlot.getItem().getItem() instanceof BackpackItem);
	}

	@Override
	protected String getStorageSettingsTabTooltip() {
		return SBPTranslationHelper.INSTANCE.translGui("settings.tooltip");
	}
}
