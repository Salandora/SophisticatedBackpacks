package net.p3pp3rf1y.sophisticatedbackpacks.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.network.RequestBackpackInventoryContentsPayload;
import net.p3pp3rf1y.sophisticatedcore.client.render.ClientStorageContentsTooltipBase;
import net.p3pp3rf1y.sophisticatedcore.network.PacketDistributor;

import java.util.UUID;

public class ClientBackpackContentsTooltip extends ClientStorageContentsTooltipBase {
	private final ItemStack backpack;

	public static void onWorldLoad(Minecraft mc, ClientLevel level) {
		refreshContents();
		lastRequestTime = 0;
	}

	@Override
	public void renderImage(Font font, int leftX, int topY, GuiGraphics guiGraphics) {
		renderTooltip(BackpackWrapper.fromStack(backpack), font, leftX, topY, guiGraphics);
	}

	public ClientBackpackContentsTooltip(BackpackItem.BackpackContentsTooltip tooltip) {
		backpack = tooltip.getBackpack();
	}

	@Override
	protected void sendInventorySyncRequest(UUID uuid) {
		PacketDistributor.sendToServer(new RequestBackpackInventoryContentsPayload(uuid));
	}
}
