package net.p3pp3rf1y.sophisticatedbackpacks.compat.trinkets;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.SlotType;
import dev.emi.trinkets.api.TrinketInventory;
import dev.emi.trinkets.api.TrinketsApi;
import dev.emi.trinkets.api.client.TrinketRendererRegistry;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.compat.CompatModIds;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems;
import net.p3pp3rf1y.sophisticatedbackpacks.util.PlayerInventoryProvider;
import net.p3pp3rf1y.sophisticatedcore.compat.ICompat;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class TrinketsCompat implements ICompat {
    private static final BackpackTrinket TRINKET_BACKPACK = new BackpackTrinket();
    private static final ItemStack BACKPACK = new ItemStack(ModItems.BACKPACK);
	private static final int TAGS_REFRESH_COOLDOWN = 100;

	public static <T> T getFromTrinketInventory(Player player, String identifier, Function<TrinketInventory, T> getFromHandler, T defaultValue) {
		return TrinketsApi.getTrinketComponent(player).map(comp -> {
			String[] identifiers = identifier.split("/");
			if (identifiers.length == 2) {
				if (comp.getInventory().containsKey(identifiers[0])) {
					Map<String, TrinketInventory> group = comp.getInventory().get(identifiers[0]);
					if (group.containsKey(identifiers[1])) {
						return getFromHandler.apply(group.get(identifiers[1]));
					}
				}
			}
			return defaultValue;
		}).orElse(defaultValue);
	}

	public static boolean isTrinketContainer(Container container) {
		return container instanceof TrinketInventory;
	}

	public static String getIdentifierForSlot(Container container) {
		if (container instanceof TrinketInventory trinketInventory) {
			return trinketInventory.getSlotType().getGroup() + "/" + trinketInventory.getSlotType().getName();
		}

		return "";
	}

	private final Set<String> backpackTrinketIdentifiers = new HashSet<>();
	private long lastTagsRefresh = -1;

    public TrinketsCompat() {
	}

	@Override
	public void init() {
		for (BackpackItem backpack : ModItems.BACKPACKS) {
            TrinketsApi.registerTrinket(backpack, TRINKET_BACKPACK);
			if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
				TrinketRendererRegistry.registerRenderer(backpack, TRINKET_BACKPACK);
			}
        }

        PlayerInventoryProvider.get().addPlayerInventoryHandler(CompatModIds.TRINKETS, this::getTrinketTags,
                (player, identifier) -> getFromTrinketInventory(player, identifier, TrinketInventory::getContainerSize, 0),
                (player, identifier, slot) -> getFromTrinketInventory(player, identifier, ti -> ti.getItem(slot), ItemStack.EMPTY),
                false, true, true ,true);
    }

    @Override
    public void setup() {
        // noop
    }

	private Set<String> getTrinketTags(Player player, long gameTime) {
		if (lastTagsRefresh + TAGS_REFRESH_COOLDOWN < gameTime) {
			lastTagsRefresh = gameTime;

			backpackTrinketIdentifiers.clear();
			TrinketsApi.getTrinketComponent(player).ifPresent(comp -> {
				for (Map.Entry<String, Map<String, TrinketInventory>> group : comp.getInventory().entrySet()) {
					for (Map.Entry<String, TrinketInventory> inventory : group.getValue().entrySet()) {
						TrinketInventory trinketInventory = inventory.getValue();
						SlotType slotType = trinketInventory.getSlotType();

						for (int i = 0; i < trinketInventory.getContainerSize(); i++) {
							SlotReference ref = new SlotReference(trinketInventory, i);
							if (TrinketsApi.evaluatePredicateSet(slotType.getTooltipPredicates(), BACKPACK, ref, player)) {
								backpackTrinketIdentifiers.add(group.getKey() + "/" + inventory.getKey());
							}
						}
					}
				}
			});
		}
		return backpackTrinketIdentifiers;
	}
}
