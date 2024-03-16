package net.p3pp3rf1y.sophisticatedbackpacks.data;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SBPLootInjectProvider implements DataProvider {
	private static final String INJECT_FOLDER = "inject/";
	public static final ResourceLocation ABANDONED_MINESHAFT = new ResourceLocation(SophisticatedBackpacks.ID, INJECT_FOLDER + BuiltInLootTables.ABANDONED_MINESHAFT.getPath());
	public static final ResourceLocation BASTION_TREASURE = new ResourceLocation(SophisticatedBackpacks.ID, INJECT_FOLDER + BuiltInLootTables.BASTION_TREASURE.getPath());
	public static final ResourceLocation DESERT_PYRAMID = new ResourceLocation(SophisticatedBackpacks.ID, INJECT_FOLDER + BuiltInLootTables.DESERT_PYRAMID.getPath());
	public static final ResourceLocation END_CITY_TREASURE = new ResourceLocation(SophisticatedBackpacks.ID, INJECT_FOLDER + BuiltInLootTables.END_CITY_TREASURE.getPath());
	public static final ResourceLocation NETHER_BRIDGE = new ResourceLocation(SophisticatedBackpacks.ID, INJECT_FOLDER + BuiltInLootTables.NETHER_BRIDGE.getPath());
	public static final ResourceLocation SHIPWRECK_TREASURE = new ResourceLocation(SophisticatedBackpacks.ID, INJECT_FOLDER + BuiltInLootTables.SHIPWRECK_TREASURE.getPath());
	public static final ResourceLocation SIMPLE_DUNGEON = new ResourceLocation(SophisticatedBackpacks.ID, INJECT_FOLDER + BuiltInLootTables.SIMPLE_DUNGEON.getPath());
	public static final ResourceLocation WOODLAND_MANSION = new ResourceLocation(SophisticatedBackpacks.ID, INJECT_FOLDER + BuiltInLootTables.WOODLAND_MANSION.getPath());
	public static final Set<ResourceLocation> ALL_TABLES = Set.of(ABANDONED_MINESHAFT, BASTION_TREASURE, DESERT_PYRAMID, END_CITY_TREASURE, NETHER_BRIDGE, SHIPWRECK_TREASURE, SIMPLE_DUNGEON, WOODLAND_MANSION);

	private final DataGenerator generator;

	SBPLootInjectProvider(DataGenerator generator) {
		this.generator = generator;
	}

	@Override
	public void run(CachedOutput cache) throws IOException {
		Map<ResourceLocation, LootTable.Builder> tables = new HashMap<>();

		tables.put(SIMPLE_DUNGEON, getLootTable(92,
				getItemLootEntry(ModItems.BACKPACK, 4),
				getItemLootEntry(ModItems.IRON_BACKPACK, 2),
				getItemLootEntry(ModItems.PICKUP_UPGRADE, 2)));
		tables.put(ABANDONED_MINESHAFT, getLootTable(89,
				getItemLootEntry(ModItems.BACKPACK, 5),
				getItemLootEntry(ModItems.IRON_BACKPACK, 3),
				getItemLootEntry(ModItems.GOLD_BACKPACK, 1),
				getItemLootEntry(ModItems.MAGNET_UPGRADE, 2)));
		tables.put(DESERT_PYRAMID, getLootTable(89,
				getItemLootEntry(ModItems.BACKPACK, 5),
				getItemLootEntry(ModItems.IRON_BACKPACK, 3),
				getItemLootEntry(ModItems.GOLD_BACKPACK, 1),
				getItemLootEntry(ModItems.MAGNET_UPGRADE, 2)));
		tables.put(SHIPWRECK_TREASURE, getLootTable(92,
				getItemLootEntry(ModItems.IRON_BACKPACK, 4),
				getItemLootEntry(ModItems.GOLD_BACKPACK, 2),
				getItemLootEntry(ModItems.ADVANCED_MAGNET_UPGRADE, 2)));
		tables.put(WOODLAND_MANSION, getLootTable(92,
				getItemLootEntry(ModItems.IRON_BACKPACK, 4),
				getItemLootEntry(ModItems.GOLD_BACKPACK, 2),
				getItemLootEntry(ModItems.ADVANCED_MAGNET_UPGRADE, 2)));
		tables.put(NETHER_BRIDGE, getLootTable(90,
				getItemLootEntry(ModItems.IRON_BACKPACK, 5),
				getItemLootEntry(ModItems.GOLD_BACKPACK, 3),
				getItemLootEntry(ModItems.FEEDING_UPGRADE, 2)));
		tables.put(BASTION_TREASURE, getLootTable(90,
				getItemLootEntry(ModItems.IRON_BACKPACK, 3),
				getItemLootEntry(ModItems.GOLD_BACKPACK, 5),
				getItemLootEntry(ModItems.FEEDING_UPGRADE, 2)));
		tables.put(END_CITY_TREASURE, getLootTable(90,
				getItemLootEntry(ModItems.DIAMOND_BACKPACK, 3),
				getItemLootEntry(ModItems.GOLD_BACKPACK, 5),
				getItemLootEntry(ModItems.ADVANCED_MAGNET_UPGRADE, 2)));

		for (Map.Entry<ResourceLocation, LootTable.Builder> e : tables.entrySet()) {
			Path path = getPath(generator.getOutputFolder(), e.getKey());
			DataProvider.saveStable(cache, LootTables.serialize(e.getValue().setParamSet(LootContextParamSets.CHEST).build()), path);
		}
	}

	@Override
	public String getName() {
		return "SophisticatedBackpacks chest loot additions";
	}

	private static Path getPath(Path root, ResourceLocation id) {
		return root.resolve("data/" + SophisticatedBackpacks.ID + "/loot_tables/" + id.getPath() + ".json");
	}

	private LootPoolEntryContainer.Builder<?> getItemLootEntry(Item item, int weight) {
		return LootItem.lootTableItem(item).setWeight(weight);
	}

	private static LootTable.Builder getLootTable(int emptyWeight, LootPoolEntryContainer.Builder<?>... entries) {
		LootPool.Builder pool = LootPool.lootPool().name("main").setRolls(ConstantValue.exactly(1));
		for (LootPoolEntryContainer.Builder<?> entry : entries) {
			pool.add(entry);
		}
		pool.add(EmptyLootItem.emptyItem().setWeight(emptyWeight));
		return LootTable.lootTable().withPool(pool);
	}
}
