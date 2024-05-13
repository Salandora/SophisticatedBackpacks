package net.p3pp3rf1y.sophisticatedbackpacks.common;

import com.google.common.collect.MapMaker;
import team.reborn.energy.api.EnergyStorage;

import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.IBackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModBlocks;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems.BACKPACKS;

public class BackpackWrapperLookup {
    public static final ItemApiLookup<IBackpackWrapper, Void> ITEM = ItemApiLookup.get(SophisticatedBackpacks.getRL("item_backpack_wrapper"), IBackpackWrapper.class, Void.class);
	private static final Map<ItemStack, BackpackWrapper> WRAPPERS = new MapMaker().weakKeys().weakValues().makeMap();

    public static Optional<IBackpackWrapper> get(ItemStack provider) {
        return Optional.ofNullable(ITEM.find(provider, null));
    }

	public static void invalidateCache(UUID uuid) {
		Set<Map.Entry<ItemStack, BackpackWrapper>> entries = WRAPPERS.entrySet();
		entries.stream().filter(entry -> entry.getValue().getContentsUuid().isPresent() && entry.getValue().getContentsUuid().get() == uuid).forEach(entries::remove);
	}

    static {
		BackpackWrapperLookup.ITEM.registerForItems((itemStack, context) -> WRAPPERS.computeIfAbsent(itemStack, BackpackWrapper::new), BACKPACKS);

        ItemStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> blockEntity.getCapability(ItemStorage.SIDED, direction), ModBlocks.BACKPACK_TILE_TYPE);
        FluidStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> blockEntity.getCapability(FluidStorage.SIDED, direction), ModBlocks.BACKPACK_TILE_TYPE);
        EnergyStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> blockEntity.getCapability(EnergyStorage.SIDED, direction), ModBlocks.BACKPACK_TILE_TYPE);
    }
}
