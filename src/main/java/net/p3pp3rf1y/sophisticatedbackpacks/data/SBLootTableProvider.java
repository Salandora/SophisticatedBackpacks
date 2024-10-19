package net.p3pp3rf1y.sophisticatedbackpacks.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SBLootTableProvider extends LootTableProvider {
	public SBLootTableProvider(FabricDataOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
		super(packOutput, SBInjectLootSubProvider.ALL_TABLES,
				List.of(
						new SubProviderEntry($ -> new SBPBlockLootSubProvider(packOutput, registries), LootContextParamSets.BLOCK),
						new SubProviderEntry(SBInjectLootSubProvider::new, LootContextParamSets.CHEST)
				), registries
		);
	}
}
