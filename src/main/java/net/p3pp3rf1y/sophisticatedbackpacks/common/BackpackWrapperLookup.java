package net.p3pp3rf1y.sophisticatedbackpacks.common;

import team.reborn.energy.api.EnergyStorage;

import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.porting_lib.base.util.LazyOptional;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.IBackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModBlocks;
import net.p3pp3rf1y.sophisticatedcore.api.IStorageWrapper;

import static net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems.BACKPACKS;

public class BackpackWrapperLookup {
	public static final ItemApiLookup<LazyOptional<IBackpackWrapper>, Void> ITEM = ItemApiLookup.get(SophisticatedBackpacks.getRL("item_backpack_wrapper"), (Class<LazyOptional<IBackpackWrapper>>) (Class<?>) LazyOptional.class, Void.class);

	public static LazyOptional<IBackpackWrapper> get(ItemStack provider) {
		LazyOptional<IBackpackWrapper> wrapper = ITEM.find(provider, null);
		if (wrapper != null) {
			return wrapper;
		}
		return LazyOptional.empty();
    }

    static {
		ITEM.registerForItems(BackpackItem.initCapabilities(), BACKPACKS);

		FluidStorage.ITEM.registerForItems((stack, ctx) -> get(stack).flatMap(IStorageWrapper::getFluidHandler).orElse(null), BACKPACKS);
		EnergyStorage.ITEM.registerForItems((stack, ctx) -> get(stack).flatMap(IStorageWrapper::getEnergyStorage).orElse(null), BACKPACKS);

        ItemStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> blockEntity.getCapability(ItemStorage.SIDED, direction).getValueUnsafer(), ModBlocks.BACKPACK_TILE_TYPE);
        FluidStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> blockEntity.getCapability(FluidStorage.SIDED, direction).getValueUnsafer(), ModBlocks.BACKPACK_TILE_TYPE);
        EnergyStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> blockEntity.getCapability(EnergyStorage.SIDED, direction).getValueUnsafer(), ModBlocks.BACKPACK_TILE_TYPE);
    }
}
