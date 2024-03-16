package net.p3pp3rf1y.sophisticatedbackpacks.data;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class DataGenerators implements DataGeneratorEntrypoint {
	public DataGenerators() {}

	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator) {
		generator.addProvider(SBLootModifierProvider::new);
		generator.addProvider(SBPBlockLootTableProvider::new);
		generator.addProvider(SBPLootInjectProvider::new);
		generator.addProvider(SBPRecipeProvider::new);
	}
}
