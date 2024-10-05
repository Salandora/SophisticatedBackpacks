package net.p3pp3rf1y.sophisticatedbackpacks.data;

import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;

import java.util.List;

public class SBLootTableProvider extends LootTableProvider {
	public  SBLootTableProvider(FabricDataOutput output) {
		super(output, SBInjectLootSubProvider.ALL_TABLES,
				List.of(
						new SubProviderEntry(() -> new SBPBlockLootSubProvider(output), LootContextParamSets.BLOCK),
						new SubProviderEntry(SBInjectLootSubProvider::new, LootContextParamSets.CHEST)
				)
		);
	}
}
