package net.p3pp3rf1y.sophisticatedbackpacks.client.init;

import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackBlockEntity;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapper;
import net.p3pp3rf1y.sophisticatedcore.util.WorldHelper;

import static net.p3pp3rf1y.sophisticatedbackpacks.init.ModBlocks.BACKPACKS;

public class ModBlockColors {
	private ModBlockColors() {}

	public static void registerBlockColorHandlers() {
		ColorProviderRegistry.BLOCK.register((state, blockDisplayReader, pos, tintIndex) -> {
			if (tintIndex < 0 || tintIndex > 1 || pos == null) {
				return -1;
			}
			return WorldHelper.getBlockEntity(blockDisplayReader, pos, BackpackBlockEntity.class)
					.map(be -> tintIndex == 0 ? be.getBackpackWrapper().getMainColor() : be.getBackpackWrapper().getAccentColor())
					.orElse(getDefaultColor(tintIndex));
		}, BACKPACKS);
	}

	private static int getDefaultColor(int tintIndex) {
		return tintIndex == 0 ? BackpackWrapper.DEFAULT_CLOTH_COLOR : BackpackWrapper.DEFAULT_BORDER_COLOR;
	}
}
