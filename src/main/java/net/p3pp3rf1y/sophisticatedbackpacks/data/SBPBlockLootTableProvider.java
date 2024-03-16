package net.p3pp3rf1y.sophisticatedbackpacks.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModBlocks;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems;

public class SBPBlockLootTableProvider extends FabricBlockLootTableProvider {
	protected SBPBlockLootTableProvider(FabricDataGenerator output) {
		super(output);
	}

	@Override
	protected void generateBlockLootTables() {
		add(ModBlocks.BACKPACK, dropBackpackWithContents(ModItems.BACKPACK));
		add(ModBlocks.IRON_BACKPACK, dropBackpackWithContents(ModItems.IRON_BACKPACK));
		add(ModBlocks.GOLD_BACKPACK, dropBackpackWithContents(ModItems.GOLD_BACKPACK));
		add(ModBlocks.DIAMOND_BACKPACK, dropBackpackWithContents(ModItems.DIAMOND_BACKPACK));
		add(ModBlocks.NETHERITE_BACKPACK, dropBackpackWithContents(ModItems.NETHERITE_BACKPACK));
	}

	private static LootTable.Builder dropBackpackWithContents(BackpackItem item) {
		LootPoolEntryContainer.Builder<?> entry = LootItem.lootTableItem(item);
		LootPool.Builder pool = LootPool.lootPool().name("main").setRolls(ConstantValue.exactly(1)).add(entry).apply(CopyBackpackDataFunction.builder());
		return LootTable.lootTable().withPool(pool);
	}
}
