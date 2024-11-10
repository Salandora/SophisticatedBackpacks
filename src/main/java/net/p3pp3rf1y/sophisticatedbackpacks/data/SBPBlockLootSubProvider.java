package net.p3pp3rf1y.sophisticatedbackpacks.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModBlocks;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems;

import java.util.concurrent.CompletableFuture;

public class SBPBlockLootSubProvider extends FabricBlockLootTableProvider {
	protected SBPBlockLootSubProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
		super(output, registryLookup);
	}

	@Override
	public void generate() {
		add(ModBlocks.BACKPACK.get(), dropBackpackWithContents(ModItems.BACKPACK.get()));
		add(ModBlocks.COPPER_BACKPACK.get(), dropBackpackWithContents(ModItems.COPPER_BACKPACK.get()));
		add(ModBlocks.IRON_BACKPACK.get(), dropBackpackWithContents(ModItems.IRON_BACKPACK.get()));
		add(ModBlocks.GOLD_BACKPACK.get(), dropBackpackWithContents(ModItems.GOLD_BACKPACK.get()));
		add(ModBlocks.DIAMOND_BACKPACK.get(), dropBackpackWithContents(ModItems.DIAMOND_BACKPACK.get()));
		add(ModBlocks.NETHERITE_BACKPACK.get(), dropBackpackWithContents(ModItems.NETHERITE_BACKPACK.get()));
	}

	private static LootTable.Builder dropBackpackWithContents(BackpackItem item) {
		LootPoolEntryContainer.Builder<?> entry = LootItem.lootTableItem(item);
		LootPool.Builder pool = LootPool.lootPool().name("main").setRolls(ConstantValue.exactly(1)).add(entry).apply(CopyBackpackDataFunction.builder());
		return LootTable.lootTable().withPool(pool);
	}
}
