package net.p3pp3rf1y.sophisticatedbackpacks.data;

import com.github.salandora.sophisticatedlibrary.loot.api.v1.GlobalLootModifierProvider;
import com.github.salandora.sophisticatedlibrary.loot.api.v1.IGlobalLootModifier;
import com.github.salandora.sophisticatedlibrary.loot.api.v1.LootModifier;
import com.github.salandora.sophisticatedlibrary.loot.api.v1.LootTableIdCondition;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class SBLootModifierProvider extends GlobalLootModifierProvider {
	SBLootModifierProvider(FabricDataOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
		super(packOutput, registries, SophisticatedBackpacks.MOD_ID);
	}

	@Override
	protected void generate(BiConsumer<ResourceKey<LootTable>, IGlobalLootModifier> consumer) {
		consumer = withConditions(consumer, ResourceConditions.alwaysTrue());

		addInjectLootTableModifier(consumer, SBInjectLootSubProvider.SIMPLE_DUNGEON, BuiltInLootTables.SIMPLE_DUNGEON);
		addInjectLootTableModifier(consumer, SBInjectLootSubProvider.ABANDONED_MINESHAFT, BuiltInLootTables.ABANDONED_MINESHAFT);
		addInjectLootTableModifier(consumer, SBInjectLootSubProvider.DESERT_PYRAMID, BuiltInLootTables.DESERT_PYRAMID);
		addInjectLootTableModifier(consumer, SBInjectLootSubProvider.WOODLAND_MANSION, BuiltInLootTables.WOODLAND_MANSION);
		addInjectLootTableModifier(consumer, SBInjectLootSubProvider.SHIPWRECK_TREASURE, BuiltInLootTables.SHIPWRECK_TREASURE);
		addInjectLootTableModifier(consumer, SBInjectLootSubProvider.BASTION_TREASURE, BuiltInLootTables.BASTION_TREASURE);
		addInjectLootTableModifier(consumer, SBInjectLootSubProvider.END_CITY_TREASURE, BuiltInLootTables.END_CITY_TREASURE);
		addInjectLootTableModifier(consumer, SBInjectLootSubProvider.NETHER_BRIDGE, BuiltInLootTables.NETHER_BRIDGE);
	}

	private void addInjectLootTableModifier(BiConsumer<ResourceKey<LootTable>, IGlobalLootModifier> consumer, ResourceKey<LootTable> lootTable, ResourceKey<LootTable> lootTableToInjectInto) {
		consumer.accept(lootTableToInjectInto, new InjectLootModifier(lootTable, lootTableToInjectInto));
	}

	public static class InjectLootModifier extends LootModifier {
		public static final MapCodec<InjectLootModifier> CODEC = RecordCodecBuilder.mapCodec(inst -> LootModifier.codecStart(inst).and(
				inst.group(
						ResourceKey.codec(Registries.LOOT_TABLE).fieldOf("loot_table").forGetter(m -> m.lootTable),
						ResourceKey.codec(Registries.LOOT_TABLE).fieldOf("loot_table_to_inject_into").forGetter(m -> m.lootTableToInjectInto)
				)
		).apply(inst, InjectLootModifier::new));
		private final ResourceKey<LootTable> lootTable;
		private final ResourceKey<LootTable> lootTableToInjectInto;

		protected InjectLootModifier(LootItemCondition[] conditions, ResourceKey<LootTable> lootTable, ResourceKey<LootTable> lootTableToInjectInto) {
			super(conditions);
			this.lootTable = lootTable;
			this.lootTableToInjectInto = lootTableToInjectInto;
		}

		protected InjectLootModifier(ResourceKey<LootTable> lootTable, ResourceKey<LootTable> lootTableToInjectInto) {
			this(new LootItemCondition[]{SBLootEnabledCondition.builder().build(),
					LootTableIdCondition.builder(lootTableToInjectInto.location()).build()}, lootTable, lootTableToInjectInto);
		}

		@Override
		protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
			context.getResolver().get(Registries.LOOT_TABLE, lootTable).ifPresent(extraTable -> extraTable.value().getRandomItemsRaw(context, LootTable.createStackSplitter(context.getLevel(), generatedLoot::add)));
			return generatedLoot;
		}

		@Override
		public MapCodec<? extends IGlobalLootModifier> codec() {
			return ModItems.INJECT_LOOT.get();
		}
	}
}
