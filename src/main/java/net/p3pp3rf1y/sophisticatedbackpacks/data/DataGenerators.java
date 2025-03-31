package net.p3pp3rf1y.sophisticatedbackpacks.data;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.core.HolderLookup;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;

public class DataGenerators implements DataGeneratorEntrypoint {
	public DataGenerators() {}

	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator) {
		FabricDataGenerator.Pack pack = generator.createPack();
		BlockTagsProvider blockTagProvider = new BlockTagsProvider(pack, evt.getLookupProvider(), SophisticatedBackpacks.MOD_ID, evt.getExistingFileHelper()){
			@Override
			protected void addTags(HolderLookup.Provider pProvider) {
				//noop
			}
		};
		pack.addProvider(blockTagProvider);
		pack.addProvider(new ItemTagProvider(pack, evt.getLookupProvider(), blockTagProvider.contentsGetter(), evt.getExistingFileHelper()));
		pack.addProvider(SBLootTableProvider::new);
		pack.addProvider(SBLootModifierProvider::new);
		pack.addProvider(SBPRecipeProvider::new);
	}
}
