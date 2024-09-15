package net.p3pp3rf1y.sophisticatedbackpacks.data;

import com.mojang.serialization.Codec;

import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.p3pp3rf1y.sophisticatedbackpacks.Config;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems;

public class SBLootEnabledCondition implements LootItemCondition {
	private static final SBLootEnabledCondition INSTANCE = new SBLootEnabledCondition();
	public static final Codec<SBLootEnabledCondition> CODEC = Codec.unit(INSTANCE);

	private SBLootEnabledCondition() {
	}

	@Override
	public LootItemConditionType getType() {
		return ModItems.LOOT_ENABLED_CONDITION;
	}

	@Override
	public boolean test(LootContext lootContext) {
		return Boolean.TRUE.equals(Config.COMMON.chestLootEnabled.get());
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder implements LootItemCondition.Builder {
		@Override
		public LootItemCondition build() {
			return new SBLootEnabledCondition();
		}
	}
}
