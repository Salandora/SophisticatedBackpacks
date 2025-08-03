package net.p3pp3rf1y.sophisticatedbackpacks.compat.emi;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiCraftingRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.Bounds;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.BackpackScreen;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.BackpackSettingsScreen;
import net.p3pp3rf1y.sophisticatedbackpacks.compat.jei.DyeRecipesMaker;
import net.p3pp3rf1y.sophisticatedbackpacks.crafting.BackpackUpgradeRecipe;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems;
import net.p3pp3rf1y.sophisticatedcore.compat.emi.EmiGridMenuInfo;
import net.p3pp3rf1y.sophisticatedcore.compat.emi.EmiSettingsGhostDragDropHandler;
import net.p3pp3rf1y.sophisticatedcore.compat.emi.EmiStorageGhostDragDropHandler;
import net.p3pp3rf1y.sophisticatedcore.compat.emi.subtypes.PropertyBasedSubtypeInterpreter;
import net.p3pp3rf1y.sophisticatedcore.compat.jei.ClientRecipeHelper;

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
		registry.addDragDropHandler(BackpackSettingsScreen.class, new EmiSettingsGhostDragDropHandler<>());


		PropertyBasedSubtypeInterpreter backpackSubTypeInterpreter = new PropertyBasedSubtypeInterpreter() {{
			addProperty(s -> BackpackWrapper.fromStack(s).getMainColor(), "clothColor", String::valueOf);
			addProperty(s -> BackpackWrapper.fromStack(s).getAccentColor(), "borderColor", String::valueOf);
		}};

        registry.setDefaultComparison(EmiStack.of(ModItems.BACKPACK.get()), backpackSubTypeInterpreter.comparator());

		registerCraftingRecipes(registry, DyeRecipesMaker.getRecipes());
		registerCraftingRecipes(registry, ClientRecipeHelper.transformAllRecipesOfType(RecipeType.CRAFTING, BackpackUpgradeRecipe.class, ClientRecipeHelper::copyShapedRecipe));

        registry.addRecipeHandler(ModItems.BACKPACK_CONTAINER_TYPE.get(), EmiGridMenuInfo.crafting());
		registry.addRecipeHandler(ModItems.BACKPACK_CONTAINER_TYPE.get(), EmiGridMenuInfo.stonecutting());
		registry.addRecipeHandler(ModItems.BACKPACK_CONTAINER_TYPE.get(), EmiGridMenuInfo.smithing());

		registry.addWorkstation(VanillaEmiRecipeCategories.CRAFTING, EmiStack.of(ModItems.CRAFTING_UPGRADE.get()));
		registry.addWorkstation(VanillaEmiRecipeCategories.STONECUTTING, EmiStack.of(ModItems.STONECUTTER_UPGRADE.get()));
		registry.addWorkstation(VanillaEmiRecipeCategories.SMITHING, EmiStack.of(ModItems.SMITHING_UPGRADE.get()));

		List<WorkstationEntry> entries = new ArrayList<>();
		WORKSTATIONS.invoker().additionalWorkstations(entries::add);
		for (WorkstationEntry entry : entries) {
			registry.addWorkstation(new EmiRecipeCategory(entry.id, EmiStack.of(entry.icon)), EmiStack.of(entry.workstation));
		}
    }

	private static void registerCraftingRecipes(EmiRegistry registry, Collection<RecipeHolder<CraftingRecipe>> recipes) {
		Minecraft mc = Minecraft.getInstance();
		recipes.forEach(r -> registry.addRecipe(
						new EmiCraftingRecipe(
								r.value().getIngredients().stream().map(EmiIngredient::of).toList(),
								EmiStack.of(r.value().getResultItem(mc.level.registryAccess())),
								r.id().withPrefix("/"))
				)
		);
	}
}
