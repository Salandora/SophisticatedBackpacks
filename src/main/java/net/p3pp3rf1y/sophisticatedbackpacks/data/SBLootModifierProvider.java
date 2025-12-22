package net.p3pp3rf1y.sophisticatedbackpacks.data;

import com.github.salandora.sophisticatedlibrary.loot.api.v1.GlobalLootModifierProvider;
import com.github.salandora.sophisticatedlibrary.loot.api.v1.IGlobalLootModifier;
import com.github.salandora.sophisticatedlibrary.loot.api.v1.LootModifier;
import com.github.salandora.sophisticatedlibrary.loot.api.v1.LootTableIdCondition;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class SBLootModifierProvider extends GlobalLootModifierProvider {

	SBLootModifierProvider(FabricDataOutput packOutput, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(packOutput, registriesFuture, SophisticatedBackpacks.MOD_ID);
	}

	@Override
	protected void generate(BiConsumer<ResourceLocation, IGlobalLootModifier> consumer) {
		addInjectLootTableModifier(consumer, SBInjectLootSubProvider.SIMPLE_DUNGEON, BuiltInLootTables.SIMPLE_DUNGEON);
		addInjectLootTableModifier(consumer, SBInjectLootSubProvider.ABANDONED_MINESHAFT, BuiltInLootTables.ABANDONED_MINESHAFT);
		addInjectLootTableModifier(consumer, SBInjectLootSubProvider.DESERT_PYRAMID, BuiltInLootTables.DESERT_PYRAMID);
		addInjectLootTableModifier(consumer, SBInjectLootSubProvider.WOODLAND_MANSION, BuiltInLootTables.WOODLAND_MANSION);
		addInjectLootTableModifier(consumer, SBInjectLootSubProvider.SHIPWRECK_TREASURE, BuiltInLootTables.SHIPWRECK_TREASURE);
		addInjectLootTableModifier(consumer, SBInjectLootSubProvider.BASTION_TREASURE, BuiltInLootTables.BASTION_TREASURE);
		addInjectLootTableModifier(consumer, SBInjectLootSubProvider.END_CITY_TREASURE, BuiltInLootTables.END_CITY_TREASURE);
		addInjectLootTableModifier(consumer, SBInjectLootSubProvider.NETHER_BRIDGE, BuiltInLootTables.NETHER_BRIDGE);
	}

	private void addInjectLootTableModifier(BiConsumer<ResourceLocation, IGlobalLootModifier> consumer, ResourceLocation lootTable, ResourceLocation lootTableToInjectInto) {
		consumer.accept(lootTableToInjectInto, new InjectLootModifier(lootTable, lootTableToInjectInto));
	}

	public static class InjectLootModifier extends LootModifier {
		public static final Codec<InjectLootModifier> CODEC = RecordCodecBuilder.create(inst -> LootModifier.codecStart(inst).and(
				inst.group(
						ResourceLocation.CODEC.fieldOf("loot_table").forGetter(m -> m.lootTable),
						ResourceLocation.CODEC.fieldOf("loot_table_to_inject_into").forGetter(m -> m.lootTableToInjectInto)
				)
		).apply(inst, InjectLootModifier::new));
		private final ResourceLocation lootTable;
		private final ResourceLocation lootTableToInjectInto;
		private BiFunction<ItemStack, LootContext, ItemStack> compositeFunction;

		protected InjectLootModifier(LootItemCondition[] conditions, ResourceLocation lootTable, ResourceLocation lootTableToInjectInto) {
			super(conditions);
			this.lootTable = lootTable;
			this.lootTableToInjectInto = lootTableToInjectInto;
		}

		protected InjectLootModifier(ResourceLocation lootTable, ResourceLocation lootTableToInjectInto) {
			this(new LootItemCondition[] {SBLootEnabledCondition.builder().build(),
					LootTableIdCondition.builder(lootTableToInjectInto).build()}, lootTable, lootTableToInjectInto);
		}

		@Override
		protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
			LootTable table = context.getResolver().getLootTable(lootTable);
			getRandomItemsRaw(table, context, generatedLoot::add);
			return generatedLoot;
		}

		public void getRandomItemsRaw(LootTable table, LootContext context, Consumer<ItemStack> lootConsumer) {
			if (compositeFunction == null) {
				compositeFunction = LootItemFunctions.compose(table.functions);
			}

			LootContext.VisitedEntry<?> visitedEntry = LootContext.createVisitedEntry(table);
			if (context.pushVisitedElement(visitedEntry)) {
				Consumer<ItemStack> consumer = LootItemFunction.decorate(compositeFunction, lootConsumer, context);
				for (LootPool lootPool : table.pools) {
					lootPool.addRandomItems(consumer, context);
				}

				context.popVisitedElement(visitedEntry);
			} else {
				LOGGER.warn("Detected infinite loop in loot tables");
			}

		}

		@Override
		public Codec<? extends IGlobalLootModifier> codec() {
			return ModItems.INJECT_LOOT;
		}
	}
}
