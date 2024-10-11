package net.p3pp3rf1y.sophisticatedbackpacks.init;

import com.mojang.serialization.Codec;

import io.github.fabricators_of_create.porting_lib.PortingLibRegistries;
import net.minecraft.core.Registry;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.data.CopyBackpackDataFunction;
import net.p3pp3rf1y.sophisticatedbackpacks.data.SBLootEnabledCondition;
import net.p3pp3rf1y.sophisticatedbackpacks.data.SBLootModifierProvider;

import java.util.List;

public class ModLoot {
	private ModLoot() {}

	public static final LootItemFunctionType COPY_BACKPACK_DATA = new LootItemFunctionType(new CopyBackpackDataFunction.Serializer());
	public static final LootItemConditionType LOOT_ENABLED_CONDITION = new LootItemConditionType(new SBLootEnabledCondition.Serializer());
	public static final Codec<SBLootModifierProvider.InjectLootModifier> INJECT_LOOT = SBLootModifierProvider.InjectLootModifier.CODEC;

	private static final List<String> CHEST_TABLES = List.of("abandoned_mineshaft", "bastion_treasure", "desert_pyramid", "end_city_treasure", "nether_bridge", "shipwreck_treasure", "simple_dungeon", "woodland_mansion");

	public static void register() {
		Registry.register(Registry.LOOT_FUNCTION_TYPE, SophisticatedBackpacks.getRL("copy_backpack_data"), COPY_BACKPACK_DATA);
		Registry.register(Registry.LOOT_CONDITION_TYPE, SophisticatedBackpacks.getRL("loot_enabled"), LOOT_ENABLED_CONDITION);
		Registry.register(PortingLibRegistries.GLOBAL_LOOT_MODIFIER_SERIALIZERS.get(), SophisticatedBackpacks.getRL("inject_loot"), INJECT_LOOT);
	}
}
