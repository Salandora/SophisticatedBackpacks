package net.p3pp3rf1y.sophisticatedbackpacks.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.item.Item;
import net.minecraft.loot.ConstantRange;
import net.minecraft.loot.EmptyLootEntry;
import net.minecraft.loot.ItemLootEntry;
import net.minecraft.loot.LootEntry;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableManager;
import net.minecraft.loot.LootTables;
import net.minecraft.util.ResourceLocation;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class SBPLootInjectProvider implements IDataProvider {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private final DataGenerator generator;

	SBPLootInjectProvider(DataGenerator generator) {
		this.generator = generator;
	}

	@Override
	public void act(DirectoryCache cache) throws IOException {
		Map<ResourceLocation, LootTable.Builder> tables = new HashMap<>();

		tables.put(LootTables.CHESTS_SIMPLE_DUNGEON, getLootTable(92,
				getItemLootEntry(ModItems.BACKPACK.get(), 4),
				getItemLootEntry(ModItems.IRON_BACKPACK.get(), 2),
				getItemLootEntry(ModItems.PICKUP_UPGRADE.get(), 2)));
		tables.put(LootTables.CHESTS_ABANDONED_MINESHAFT, getLootTable(89,
				getItemLootEntry(ModItems.BACKPACK.get(), 5),
				getItemLootEntry(ModItems.IRON_BACKPACK.get(), 3),
				getItemLootEntry(ModItems.GOLD_BACKPACK.get(), 1),
				getItemLootEntry(ModItems.MAGNET_UPGRADE.get(), 2)));
		tables.put(LootTables.CHESTS_DESERT_PYRAMID, getLootTable(89,
				getItemLootEntry(ModItems.BACKPACK.get(), 5),
				getItemLootEntry(ModItems.IRON_BACKPACK.get(), 3),
				getItemLootEntry(ModItems.GOLD_BACKPACK.get(), 1),
				getItemLootEntry(ModItems.MAGNET_UPGRADE.get(), 2)));
		tables.put(LootTables.CHESTS_SHIPWRECK_TREASURE, getLootTable(92,
				getItemLootEntry(ModItems.IRON_BACKPACK.get(), 4),
				getItemLootEntry(ModItems.GOLD_BACKPACK.get(), 2),
				getItemLootEntry(ModItems.ADVANCED_MAGNET_UPGRADE.get(), 2)));
		tables.put(LootTables.CHESTS_WOODLAND_MANSION, getLootTable(92,
				getItemLootEntry(ModItems.IRON_BACKPACK.get(), 4),
				getItemLootEntry(ModItems.GOLD_BACKPACK.get(), 2),
				getItemLootEntry(ModItems.ADVANCED_MAGNET_UPGRADE.get(), 2)));
		tables.put(LootTables.CHESTS_NETHER_BRIDGE, getLootTable(90,
				getItemLootEntry(ModItems.IRON_BACKPACK.get(), 5),
				getItemLootEntry(ModItems.GOLD_BACKPACK.get(), 3),
				getItemLootEntry(ModItems.FEEDING_UPGRADE.get(), 2)));
		tables.put(LootTables.BASTION_TREASURE, getLootTable(90,
				getItemLootEntry(ModItems.IRON_BACKPACK.get(), 3),
				getItemLootEntry(ModItems.GOLD_BACKPACK.get(), 5),
				getItemLootEntry(ModItems.FEEDING_UPGRADE.get(), 2)));
		tables.put(LootTables.CHESTS_END_CITY_TREASURE, getLootTable(90,
				getItemLootEntry(ModItems.DIAMOND_BACKPACK.get(), 3),
				getItemLootEntry(ModItems.GOLD_BACKPACK.get(), 5),
				getItemLootEntry(ModItems.ADVANCED_MAGNET_UPGRADE.get(), 2)));

		for (Map.Entry<ResourceLocation, LootTable.Builder> e : tables.entrySet()) {
			Path path = getPath(generator.getOutputFolder(), e.getKey());
			IDataProvider.save(GSON, cache, LootTableManager.toJson(e.getValue().setParameterSet(LootParameterSets.CHEST).build()), path);
		}
	}

	@Override
	public String getName() {
		return "SophisticatedBackpacks chest loot additions";
	}

	private static Path getPath(Path root, ResourceLocation id) {
		return root.resolve("data/" + SophisticatedBackpacks.MOD_ID + "/loot_tables/inject/" + id.getPath() + ".json");
	}

	private LootEntry.Builder<?> getItemLootEntry(Item item, int weight) {
		return ItemLootEntry.builder(item).weight(weight);
	}

	private static LootTable.Builder getLootTable(int emptyWeight, LootEntry.Builder<?>... entries) {
		LootPool.Builder pool = LootPool.builder().name("main").rolls(ConstantRange.of(1));
		for (LootEntry.Builder<?> entry : entries) {
			pool.addEntry(entry);
		}
		pool.addEntry(EmptyLootEntry.func_216167_a().weight(emptyWeight));
		return LootTable.builder().addLootPool(pool);
	}
}
