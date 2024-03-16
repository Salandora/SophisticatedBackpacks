package net.p3pp3rf1y.sophisticatedbackpacks.crafting;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.UpgradeRecipe;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.common.BackpackWrapperLookup;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems;
import net.p3pp3rf1y.sophisticatedcore.SophisticatedCore;
import net.p3pp3rf1y.sophisticatedcore.crafting.IWrapperRecipe;
import net.p3pp3rf1y.sophisticatedcore.crafting.RecipeWrapperSerializer;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

@SuppressWarnings("removal")
public class SmithingBackpackUpgradeRecipe extends UpgradeRecipe implements IWrapperRecipe<UpgradeRecipe> {
	public static final Set<ResourceLocation> REGISTERED_RECIPES = new LinkedHashSet<>();
	private final UpgradeRecipe compose;

	public SmithingBackpackUpgradeRecipe(UpgradeRecipe compose) {
		super(compose.getId(), compose.base, compose.addition, compose.result);
		this.compose = compose;
		REGISTERED_RECIPES.add(compose.getId());
	}

	@Override
	public boolean isSpecial() {
		return true;
	}

	@Override
	public ItemStack assemble(Container inventory) {
		ItemStack upgradedBackpack = result.copy();
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
		ItemStack slotStack = inv.getItem(0);
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
	public UpgradeRecipe getCompose() {
		return compose;
	}

	public static class Serializer extends RecipeWrapperSerializer<UpgradeRecipe, SmithingBackpackUpgradeRecipe> {
		public Serializer() {
			super(SmithingBackpackUpgradeRecipe::new, RecipeSerializer.SMITHING);
		}
	}
}
