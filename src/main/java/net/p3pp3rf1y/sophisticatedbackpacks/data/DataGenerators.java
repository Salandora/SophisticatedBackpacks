package net.p3pp3rf1y.sophisticatedbackpacks.data;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;

public class DataGenerators implements DataGeneratorEntrypoint {
	public DataGenerators() {}

	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator) {
		FabricDataGenerator.Pack pack = generator.createPack();
		pack.addProvider(ItemTagProvider::new);
		pack.addProvider(SBLootTableProvider::new);
		pack.addProvider(SBLootModifierProvider::new);
		pack.addProvider(SBPRecipeProvider::new);
	}
}
