package net.p3pp3rf1y.sophisticatedbackpacks.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackBlockEntity;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems;

public class CopyBackpackDataFunction implements LootItemFunction {
	private static final CopyBackpackDataFunction INSTANCE = new CopyBackpackDataFunction();
	public static final Codec<CopyBackpackDataFunction> CODEC = MapCodec.unit(INSTANCE).stable().codec();

	protected CopyBackpackDataFunction() {
		super();
	}

	@Override
	public ItemStack apply(ItemStack stack, LootContext lootContext) {
		BlockEntity be = lootContext.getParamOrNull(LootContextParams.BLOCK_ENTITY);
		if (be instanceof BackpackBlockEntity backpackBlockEntity) {
			return backpackBlockEntity.getBackpackWrapper().getBackpack();
		}

		return stack;
	}

	@Override
	public LootItemFunctionType getType() {
		return ModItems.COPY_BACKPACK_DATA;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder implements LootItemFunction.Builder {
		@Override
		public LootItemFunction build() {
			return new CopyBackpackDataFunction();
		}
	}
}
