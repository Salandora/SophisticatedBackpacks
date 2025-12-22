package net.p3pp3rf1y.sophisticatedbackpacks.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.client.init.ModItems;
import net.p3pp3rf1y.sophisticatedbackpacks.client.render.ClientBackpackContentsTooltip;
import net.p3pp3rf1y.sophisticatedbackpacks.network.SBPPacketHandler;

import javax.annotation.Nullable;

@Environment(EnvType.CLIENT)
public class SophisticatedBackpacksClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		KeybindHandler.registerKeyMappings();
		KeybindHandler.register();

		ModItems.registerScreens();

		TooltipComponentCallback.EVENT.register(SophisticatedBackpacksClient::registerTooltipComponent);

		ClientEventHandler.registerHandlers();

		SBPPacketHandler.INSTANCE.initClientListener();
	}
	@Nullable
	private static ClientTooltipComponent registerTooltipComponent(TooltipComponent data) {
		if (data instanceof BackpackItem.BackpackContentsTooltip) {
			return new ClientBackpackContentsTooltip((BackpackItem.BackpackContentsTooltip) data);
		}

		return null;
	}
}
