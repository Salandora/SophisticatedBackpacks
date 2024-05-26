package net.p3pp3rf1y.sophisticatedbackpacks.init;

import com.mojang.serialization.Codec;

import io.github.fabricators_of_create.porting_lib.PortingLibRegistries;
import io.github.fabricators_of_create.porting_lib.loot.IGlobalLootModifier;
import net.minecraft.core.Registry;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.data.CopyBackpackDataFunction;
import net.p3pp3rf1y.sophisticatedbackpacks.data.SBLootEnabledCondition;
import net.p3pp3rf1y.sophisticatedbackpacks.data.SBLootModifierProvider;

import java.util.List;
import java.util.function.Supplier;

public class ModLoot {
	private ModLoot() {}

	public static final LootItemFunctionType COPY_BACKPACK_DATA = registerLootFunction("copy_backpack_data", () -> new LootItemFunctionType(new CopyBackpackDataFunction.Serializer()));
	public static final LootItemConditionType LOOT_ENABLED_CONDITION = registerLootCondition("loot_enabled", () -> new LootItemConditionType(new SBLootEnabledCondition.Serializer()));
	public static final Codec<SBLootModifierProvider.InjectLootModifier> INJECT_LOOT = registerLootModifier("inject_loot", () -> SBLootModifierProvider.InjectLootModifier.CODEC);

	private static final List<String> CHEST_TABLES = List.of("abandoned_mineshaft", "bastion_treasure", "desert_pyramid", "end_city_treasure", "nether_bridge", "shipwreck_treasure", "simple_dungeon", "woodland_mansion");

	public static <T extends LootItemFunctionType> T registerLootFunction(String id, Supplier<T> supplier) {
		return Registry.register(Registry.LOOT_FUNCTION_TYPE, SophisticatedBackpacks.getRL(id), supplier.get());
	}
	public static <T extends LootItemConditionType> T registerLootCondition(String id, Supplier<T> supplier) {
		return Registry.register(Registry.LOOT_CONDITION_TYPE, SophisticatedBackpacks.getRL(id), supplier.get());
	}
	public static <T extends Codec<? extends IGlobalLootModifier>> T registerLootModifier(String id, Supplier<T> supplier) {
		return Registry.register(PortingLibRegistries.GLOBAL_LOOT_MODIFIER_SERIALIZERS.get(), SophisticatedBackpacks.getRL(id), supplier.get());
	}
}
