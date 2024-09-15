package net.p3pp3rf1y.sophisticatedbackpacks.mixin.common.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;

import java.util.List;

@Mixin(LootTable.class)
public interface LootTableAccessor {
	@Accessor
	List<LootPool> getPools();

	@Accessor
	List<LootItemFunction> getFunctions();
}
