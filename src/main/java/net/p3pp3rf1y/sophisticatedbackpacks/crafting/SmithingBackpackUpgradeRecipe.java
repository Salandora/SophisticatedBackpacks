package net.p3pp3rf1y.sophisticatedbackpacks.crafting;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.common.BackpackWrapperLookup;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems;
import net.p3pp3rf1y.sophisticatedbackpacks.mixin.common.accessor.SmithingTransformRecipeAccessor;
import net.p3pp3rf1y.sophisticatedcore.SophisticatedCore;
import net.p3pp3rf1y.sophisticatedcore.crafting.IWrapperRecipe;
import net.p3pp3rf1y.sophisticatedcore.crafting.RecipeWrapperSerializer;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

public class SmithingBackpackUpgradeRecipe extends SmithingTransformRecipe implements IWrapperRecipe<SmithingTransformRecipe> {
	public static final Set<ResourceLocation> REGISTERED_RECIPES = new LinkedHashSet<>();
	private final SmithingTransformRecipe compose;

	public SmithingBackpackUpgradeRecipe(SmithingTransformRecipe compose) {
		super(compose.getId(), ((SmithingTransformRecipeAccessor) compose).getTemplate(), ((SmithingTransformRecipeAccessor) compose).getBase(), ((SmithingTransformRecipeAccessor) compose).getAddition(), ((SmithingTransformRecipeAccessor) compose).getResult());
		this.compose = compose;
		REGISTERED_RECIPES.add(compose.getId());
	}

	@Override
	public boolean isSpecial() {
		return true;
	}

	@Override
	public ItemStack assemble(Container inventory, RegistryAccess registryManager) {
		ItemStack upgradedBackpack = ((SmithingTransformRecipeAccessor) this).getResult().copy();
		if (SophisticatedCore.getCurrentServer() != null && SophisticatedCore.getCurrentServer().isSameThread()) {
			getBackpack(inventory).flatMap(backpack -> Optional.ofNullable(backpack.getTag())).ifPresent(tag -> upgradedBackpack.setTag(tag.copy()));
			BackpackWrapperLookup.get(upgradedBackpack)
					.ifPresent(wrapper -> {
						BackpackItem backpackItem = ((BackpackItem) upgradedBackpack.getItem());
						wrapper.setSlotNumbers(backpackItem.getNumberOfSlots(), backpackItem.getNumberOfUpgradeSlots());
					});
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
