package net.p3pp3rf1y.sophisticatedbackpacks.crafting;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.IBackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems;
import net.p3pp3rf1y.sophisticatedbackpacks.mixin.common.accessor.SmithingTransformRecipeAccessor;
import net.p3pp3rf1y.sophisticatedcore.SophisticatedCore;
import net.p3pp3rf1y.sophisticatedcore.crafting.IWrapperRecipe;
import net.p3pp3rf1y.sophisticatedcore.crafting.RecipeWrapperSerializer;

import java.util.Optional;

public class SmithingBackpackUpgradeRecipe extends SmithingTransformRecipe implements IWrapperRecipe<SmithingTransformRecipe> {
	private final SmithingTransformRecipe compose;

	public SmithingBackpackUpgradeRecipe(SmithingTransformRecipe compose) {
		super(((SmithingTransformRecipeAccessor) compose).getTemplate(), ((SmithingTransformRecipeAccessor) compose).getBase(), ((SmithingTransformRecipeAccessor) compose).getAddition(), ((SmithingTransformRecipeAccessor) compose).getResult());
		this.compose = compose;
	}

	@Override
	public boolean isSpecial() {
		return true;
	}

	@Override
	public ItemStack assemble(Container inv, RegistryAccess registryAccess) {
		ItemStack upgradedBackpack = ((SmithingTransformRecipeAccessor) this).getResult().copy();
		if (SophisticatedCore.getCurrentServer() != null && SophisticatedCore.getCurrentServer().isSameThread()) {
			getBackpack(inv).flatMap(backpack -> Optional.ofNullable(backpack.getTag())).ifPresent(tag -> upgradedBackpack.setTag(tag.copy()));
			IBackpackWrapper wrapper = BackpackWrapper.fromData(upgradedBackpack);
			BackpackItem backpackItem = ((BackpackItem) upgradedBackpack.getItem());
			wrapper.setSlotNumbers(backpackItem.getNumberOfSlots(), backpackItem.getNumberOfUpgradeSlots());
		}
		return upgradedBackpack;
	}

	private Optional<ItemStack> getBackpack(Container inv) {
		ItemStack slotStack = inv.getItem(1);
		if (slotStack.getItem() instanceof BackpackItem) {
			return Optional.of(slotStack);
		}
		return Optional.empty();
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return ModItems.SMITHING_BACKPACK_UPGRADE_RECIPE_SERIALIZER;
	}

	@Override
	public SmithingTransformRecipe getCompose() {
		return compose;
	}

	public static class Serializer extends RecipeWrapperSerializer<SmithingTransformRecipe, SmithingBackpackUpgradeRecipe> {
		public Serializer() {
			super(SmithingBackpackUpgradeRecipe::new, RecipeSerializer.SMITHING_TRANSFORM);
		}
	}
}
