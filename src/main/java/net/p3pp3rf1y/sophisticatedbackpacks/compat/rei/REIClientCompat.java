package net.p3pp3rf1y.sophisticatedbackpacks.compat.rei;

import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.screen.ExclusionZones;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.plugin.common.BuiltinPlugin;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.BackpackScreen;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.BackpackSettingsScreen;
import net.p3pp3rf1y.sophisticatedbackpacks.compat.common.DyeRecipesMaker;
import net.p3pp3rf1y.sophisticatedbackpacks.crafting.BackpackUpgradeRecipe;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems;
import net.p3pp3rf1y.sophisticatedcore.compat.common.ClientRecipeHelper;
import net.p3pp3rf1y.sophisticatedcore.compat.rei.StorageGhostIngredientHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class REIClientCompat implements REIClientPlugin {
    @Override
    public void registerExclusionZones(ExclusionZones zones) {
        zones.register(BackpackScreen.class, screen -> {
            List<Rect2i> ret = new ArrayList<>();
            screen.getUpgradeSlotsRectangle().ifPresent(ret::add);
            ret.addAll(screen.getUpgradeSettingsControl().getTabRectangles());
            screen.getSortButtonsRectangle().ifPresent(ret::add);
            return ret.stream().map(r -> new Rectangle(r.getX(), r.getY(), r.getWidth(), r.getHeight())).toList();
        });

        zones.register(BackpackSettingsScreen.class, screen -> screen.getSettingsTabControl().getTabRectangles().stream().map(r -> new Rectangle(r.getX(), r.getY(), r.getWidth(), r.getHeight())).toList());
    }

	@Override
	public void registerCategories(CategoryRegistry registry) {
		registry.addWorkstations(BuiltinPlugin.CRAFTING, EntryStacks.of(ModItems.CRAFTING_UPGRADE));
		registry.addWorkstations(BuiltinPlugin.STONE_CUTTING, EntryStacks.of(ModItems.STONECUTTER_UPGRADE));
	}

    @Override
    public void registerScreens(ScreenRegistry registry) {
        registry.registerDraggableStackVisitor(new StorageGhostIngredientHandler<>() {
            @Override
            public <R extends Screen> boolean isHandingScreen(R screen) {
                return screen instanceof BackpackScreen;
            }
        });
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registerRecipes(registry, DyeRecipesMaker.getRecipes(), BuiltinPlugin.CRAFTING);
        registerRecipes(registry, ClientRecipeHelper.getAndTransformAvailableRecipes(BackpackUpgradeRecipe.REGISTERED_RECIPES, ShapedRecipe.class, ClientRecipeHelper::copyShapedRecipe), BuiltinPlugin.CRAFTING);
    }

    public static void registerRecipes(DisplayRegistry registry, Collection<?> recipes, CategoryIdentifier<?> identifier) {
        recipes.forEach(recipe -> {
            Collection<Display> displays = registry.tryFillDisplay(recipe);
            for (Display display : displays) {
                if (Objects.equals(display.getCategoryIdentifier(), identifier)) {
                    registry.add(display, recipe);
                }
            }
        });
    }
}
