package net.p3pp3rf1y.sophisticatedbackpacks.client.init;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.common.BackpackWrapperLookup;
import net.p3pp3rf1y.sophisticatedcore.renderdata.TankPosition;
import net.p3pp3rf1y.sophisticatedcore.upgrades.IRenderedTankUpgrade;

import static net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems.BACKPACKS;

@Environment(EnvType.CLIENT)
public class ModItemColors {
	private ModItemColors() {}

	public static void registerItemColorHandlers() {
		ColorProviderRegistry.ITEM.register((backpack, layer) -> {
			if (layer > 3 || !(backpack.getItem() instanceof BackpackItem)) {
				return -1;
			}
			return BackpackWrapperLookup.get(backpack).map(backpackWrapper -> {
				if (layer == 0) {
					return backpackWrapper.getMainColor();
				} else if (layer == 1) {
					return backpackWrapper.getAccentColor();
				} else if (layer >= 2) {
					IRenderedTankUpgrade.TankRenderInfo info = backpackWrapper.getRenderInfo().getTankRenderInfos().getOrDefault(layer == 2 ? TankPosition.LEFT : TankPosition.RIGHT, null);
					if (info == null || info.getFluid().isEmpty()) {
						return -1;
					}

					return FluidVariantRendering.getColor(info.getFluid().get().getType());
				}
				return -1;
			}).orElse(BackpackWrapper.DEFAULT_CLOTH_COLOR);
		}, BACKPACKS);
	}
}
