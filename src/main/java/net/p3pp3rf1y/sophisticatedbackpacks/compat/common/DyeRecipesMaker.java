package net.p3pp3rf1y.sophisticatedbackpacks.compat.common;

import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.common.BackpackWrapperLookup;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems;
import net.p3pp3rf1y.sophisticatedcore.util.ColorHelper;

import java.util.ArrayList;
import java.util.List;

public class DyeRecipesMaker {
	private DyeRecipesMaker() {}

	public static List<CraftingRecipe> getRecipes() {
		List<CraftingRecipe> recipes = new ArrayList<>();
		addSingleColorRecipes(recipes);
		addMultipleColorsRecipe(recipes);

		return recipes;
	}

	private static void addMultipleColorsRecipe(List<CraftingRecipe> recipes) {
		NonNullList<Ingredient> ingredients = NonNullList.create();
		ingredients.add(Ingredient.of(ConventionalItemTags.YELLOW_DYES));
		ingredients.add(Ingredient.of(ModItems.BACKPACK));
		ingredients.add(Ingredient.EMPTY);
		ingredients.add(Ingredient.of(ConventionalItemTags.LIME_DYES));
		ingredients.add(Ingredient.of(ConventionalItemTags.BLUE_DYES));
		ingredients.add(Ingredient.of(ConventionalItemTags.BLACK_DYES));

		ItemStack backpackOutput = new ItemStack(ModItems.BACKPACK);
		int clothColor = ColorHelper.calculateColor(BackpackWrapper.DEFAULT_CLOTH_COLOR, BackpackWrapper.DEFAULT_CLOTH_COLOR, List.of(
				DyeColor.BLUE, DyeColor.YELLOW, DyeColor.LIME
		));
		int trimColor = ColorHelper.calculateColor(BackpackWrapper.DEFAULT_BORDER_COLOR, BackpackWrapper.DEFAULT_BORDER_COLOR, List.of(
				DyeColor.BLUE, DyeColor.BLACK
		));

		BackpackWrapperLookup.get(backpackOutput).ifPresent(wrapper -> wrapper.setColors(clothColor, trimColor));

		ResourceLocation id = SophisticatedBackpacks.getRL("multiple_colors");
		recipes.add(new ShapedRecipe(id, "", CraftingBookCategory.MISC, 3, 1, ingredients, backpackOutput));
	}

	private static void addSingleColorRecipes(List<CraftingRecipe> recipes) {
		for (DyeColor color : DyeColor.values()) {
			ResourceLocation id = SophisticatedBackpacks.getRL("single_color_" + color.getSerializedName());
			ItemStack backpackOutput = new ItemStack(ModItems.BACKPACK);
			BackpackWrapperLookup.get(backpackOutput).ifPresent(
					wrapper -> wrapper.setColors(ColorHelper.getColor(color.getTextureDiffuseColors()), ColorHelper.getColor(color.getTextureDiffuseColors())));
			NonNullList<Ingredient> ingredients = NonNullList.create();
			ingredients.add(Ingredient.of(ModItems.BACKPACK));
			ingredients.add(Ingredient.of(TagKey.create(Registries.ITEM, new ResourceLocation("c", color.getName() + "_dyes"))));
			recipes.add(new ShapedRecipe(id, "", CraftingBookCategory.MISC, 1, 2, ingredients, backpackOutput));
		}
	}
}
