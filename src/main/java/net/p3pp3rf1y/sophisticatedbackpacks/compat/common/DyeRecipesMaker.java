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
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems;
import net.p3pp3rf1y.sophisticatedcore.util.ColorHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DyeRecipesMaker {
	private DyeRecipesMaker() {
	}

	public static List<RecipeHolder<CraftingRecipe>> getRecipes() {
		List<RecipeHolder<CraftingRecipe>> recipes = new ArrayList<>();
		addSingleColorRecipes(recipes);
		addMultipleColorsRecipe(recipes);

		return recipes;
	}

	private static void addMultipleColorsRecipe(List<RecipeHolder<CraftingRecipe>> recipes) {
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

		BackpackWrapper.fromData(backpackOutput).setColors(clothColor, trimColor);

		ShapedRecipePattern pattern = new ShapedRecipePattern(3, 1, ingredients, Optional.empty());
		ResourceLocation id = new ResourceLocation(SophisticatedBackpacks.MOD_ID, "multiple_colors");
		recipes.add(new RecipeHolder<>(id, new ShapedRecipe("", CraftingBookCategory.MISC, pattern, backpackOutput)));
	}

	private static void addSingleColorRecipes(List<RecipeHolder<CraftingRecipe>> recipes) {
		for (DyeColor color : DyeColor.values()) {
			ItemStack backpackOutput = new ItemStack(ModItems.BACKPACK);
			BackpackWrapper.fromData(backpackOutput).setColors(ColorHelper.getColor(color.getTextureDiffuseColors()), ColorHelper.getColor(color.getTextureDiffuseColors()));
			NonNullList<Ingredient> ingredients = NonNullList.create();
			ingredients.add(Ingredient.of(ModItems.BACKPACK));
			ingredients.add(Ingredient.of(TagKey.create(Registries.ITEM, new ResourceLocation("c", color.getName() + "_dyes"))));
			ShapedRecipePattern pattern = new ShapedRecipePattern(1, 2, ingredients, Optional.empty());
			ResourceLocation id = new ResourceLocation(SophisticatedBackpacks.MOD_ID, "single_color_" + color.getSerializedName());
			recipes.add(new RecipeHolder<>(id, new ShapedRecipe("", CraftingBookCategory.MISC, pattern, backpackOutput)));
		}
	}
}
