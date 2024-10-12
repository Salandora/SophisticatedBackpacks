package net.p3pp3rf1y.sophisticatedbackpacks.common;

import com.google.common.collect.MapMaker;
import net.minecraft.world.item.ItemStack;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.IBackpackWrapper;

import javax.annotation.Nullable;
import java.util.Map;

import static net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems.BACKPACKS;

public class BackpackWrapperLookup {
	public static final ItemApiLookup<IBackpackWrapper, Boolean> ITEM = ItemApiLookup.get(SophisticatedBackpacks.getRL("item_backpack_wrapper"), IBackpackWrapper.class, Boolean.class);

	public static IBackpackWrapper getOrCreate(ItemStack provider) {
		return ITEM.find(provider, true);
    }
	@Nullable
	public static IBackpackWrapper get(ItemStack provider) {
		return ITEM.find(provider, false);
	}

    static {
		ItemApiLookup.ItemApiProvider<IBackpackWrapper, Boolean> provider = new ItemApiLookup.ItemApiProvider<>() {
			final Map<ItemStack, IBackpackWrapper> wrapperMap = new MapMaker().weakKeys().weakValues().makeMap();

			@Override
			public IBackpackWrapper find(ItemStack stack, Boolean create) {
				if (create) {
					return wrapperMap.computeIfAbsent(stack, ignored -> new BackpackWrapper());
				} else {
					return wrapperMap.get(stack);
				}
			}
		};

		ITEM.registerForItems(provider, BACKPACKS);
	}
}
