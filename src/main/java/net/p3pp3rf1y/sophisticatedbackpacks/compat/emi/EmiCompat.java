package net.p3pp3rf1y.sophisticatedbackpacks.compat.emi;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiCraftingRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.stack.Comparison;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.Bounds;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.level.block.Block;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.BackpackScreen;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.BackpackSettingsScreen;
import net.p3pp3rf1y.sophisticatedbackpacks.common.BackpackWrapperLookup;
import net.p3pp3rf1y.sophisticatedbackpacks.compat.common.DyeRecipesMaker;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems;
import net.p3pp3rf1y.sophisticatedcore.client.gui.SettingsScreen;
import net.p3pp3rf1y.sophisticatedcore.compat.emi.EmiGridMenuInfo;
import net.p3pp3rf1y.sophisticatedcore.compat.emi.EmiSettingsGhostDragDropHandler;
import net.p3pp3rf1y.sophisticatedcore.compat.emi.EmiStorageGhostDragDropHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class EmiCompat implements EmiPlugin {
	public static Event<WorkstationCallback> WORKSTATIONS = EventFactory.createArrayBacked(WorkstationCallback.class, (listeners) -> (consumer) -> {
		for (WorkstationCallback listener : listeners) {
			listener.additionalWorkstations(consumer);
		}
	});

	public record WorkstationEntry(ResourceLocation id, Block icon, Item workstation) {}

	public interface WorkstationCallback {
		void additionalWorkstations(Consumer<WorkstationEntry> consumer);
	}

    @Override
    public void register(EmiRegistry registry) {
        registry.addExclusionArea(BackpackScreen.class, (screen, consumer) -> {
            screen.getUpgradeSlotsRectangle().ifPresent(r -> consumer.accept(new Bounds(r.getX(), r.getY(), r.getWidth(), r.getHeight())));
            screen.getUpgradeSettingsControl().getTabRectangles().forEach(r -> consumer.accept(new Bounds(r.getX(), r.getY(), r.getWidth(), r.getHeight())));
            screen.getSortButtonsRectangle().ifPresent(r -> consumer.accept(new Bounds(r.getX(), r.getY(), r.getWidth(), r.getHeight())));
        });

        registry.addExclusionArea(BackpackSettingsScreen.class, (screen, consumer) -> {
            if (screen == null || screen.getSettingsTabControl() == null) { // Due to how Emi collects the exclusion area this can be null
                return;
            }
            screen.getSettingsTabControl().getTabRectangles().forEach(r -> consumer.accept(new Bounds(r.getX(), r.getY(), r.getWidth(), r.getHeight())));
        });

        registry.addDragDropHandler(BackpackScreen.class, new EmiStorageGhostDragDropHandler<>());
        registry.addDragDropHandler(SettingsScreen.class, new EmiSettingsGhostDragDropHandler<>());

        registerCraftingRecipes(registry, DyeRecipesMaker.getRecipes());

        Comparison compareColor = Comparison.of((a, b) ->
            BackpackWrapperLookup.get(a.getItemStack())
                .map(stackA -> BackpackWrapperLookup.get(b.getItemStack())
                    .map(stackB -> stackA.getMainColor() == stackB.getMainColor() && stackA.getAccentColor() == stackB.getAccentColor())
                    .orElse(false))
                .orElse(false));

        registry.setDefaultComparison(EmiStack.of(ModItems.BACKPACK), compareColor);

        registry.addRecipeHandler(ModItems.BACKPACK_CONTAINER_TYPE, new EmiGridMenuInfo<>());

		registry.addWorkstation(VanillaEmiRecipeCategories.CRAFTING, EmiStack.of(ModItems.CRAFTING_UPGRADE));
		registry.addWorkstation(VanillaEmiRecipeCategories.STONECUTTING, EmiStack.of(ModItems.STONECUTTER_UPGRADE));

		List<WorkstationEntry> entries = new ArrayList<>();
		WORKSTATIONS.invoker().additionalWorkstations(entries::add);
		for (WorkstationEntry entry : entries) {
			registry.addWorkstation(new EmiRecipeCategory(entry.id, EmiStack.of(entry.icon)), EmiStack.of(entry.workstation));
		}
    }

    private static void registerCraftingRecipes(EmiRegistry registry, Collection<CraftingRecipe> recipes) {
        recipes.forEach(r -> registry.addRecipe(
            new EmiCraftingRecipe(
                r.getIngredients().stream().map(EmiIngredient::of).toList(),
                EmiStack.of(r.getResultItem(null)),
                r.getId())
            )
        );
    }
}
