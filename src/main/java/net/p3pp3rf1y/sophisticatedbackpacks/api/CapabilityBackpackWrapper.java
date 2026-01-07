package net.p3pp3rf1y.sophisticatedbackpacks.api;

import com.github.salandora.sophisticatedfabriclib.transfer.api.v1.wrapper.fabric.FabricFluidHandlerWrapper;
import com.github.salandora.sophisticatedfabriclib.transfer.api.v1.wrapper.fabric.FabricItemHandlerWrapper;
import com.github.salandora.sophisticatedfabriclib.util.Capabilities;
import com.github.salandora.sophisticatedfabriclib.util.LazyOptional;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.IBackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModBlocks;
import net.p3pp3rf1y.sophisticatedcore.api.IStorageWrapper;
import team.reborn.energy.api.EnergyStorage;

import static net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems.BACKPACKS;

public class CapabilityBackpackWrapper {
	@SuppressWarnings("unchecked")
	public static final ItemApiLookup<LazyOptional<IBackpackWrapper>, Void> ITEM = ItemApiLookup.get(SophisticatedBackpacks.getRL("item_backpack_wrapper"), (Class<LazyOptional<IBackpackWrapper>>) (Class<?>) LazyOptional.class, Void.class);

	public static ItemApiLookup<LazyOptional<IBackpackWrapper>, Void> getCapabilityInstance() {
		return ITEM;
	}

    static {
		ITEM.registerForItems(BackpackItem.initCapabilities(), BACKPACKS);

		Capabilities.ItemHandler.ITEM.registerForItems(((stack, ctx) -> stack.sophisticatedLibrary_getLazyCapability(ITEM).map(IStorageWrapper::getInventoryForInputOutput).orElse(null)), BACKPACKS);
		Capabilities.FluidHandler.ITEM.registerForItems(((stack, ctx) -> stack.sophisticatedLibrary_getLazyCapability(ITEM).flatMap(IStorageWrapper::getFluidHandler).orElse(null)), BACKPACKS);

		//ItemItemStorages.ITEM.registerForItems((stack, ctx) -> get(stack).map(IStorageWrapper::getInventoryForInputOutput).orElse(null), BACKPACKS);
		FluidStorage.ITEM.registerForItems((stack, ctx) -> stack.sophisticatedLibrary_getLazyCapability(ITEM).flatMap(IStorageWrapper::getFluidHandler).map(FabricFluidHandlerWrapper::of).orElse(null), BACKPACKS);
		EnergyStorage.ITEM.registerForItems((stack, ctx) -> stack.sophisticatedLibrary_getLazyCapability(ITEM).flatMap(IStorageWrapper::getEnergyStorage).orElse(null), BACKPACKS);


		Capabilities.ItemHandler.SIDED.registerForBlockEntity((be, dir) -> be.getCapability(Capabilities.ItemHandler.SIDED, dir).orElse(null), ModBlocks.BACKPACK_TILE_TYPE);
		Capabilities.FluidHandler.SIDED.registerForBlockEntity((be, dir) -> be.getCapability(Capabilities.FluidHandler.SIDED, dir).orElse(null), ModBlocks.BACKPACK_TILE_TYPE);

		ItemStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> blockEntity.getCapability(Capabilities.ItemHandler.SIDED, direction).map(FabricItemHandlerWrapper::of).orElse(null), ModBlocks.BACKPACK_TILE_TYPE);
        FluidStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> blockEntity.getCapability(Capabilities.FluidHandler.SIDED, direction).map(FabricFluidHandlerWrapper::of).orElse(null), ModBlocks.BACKPACK_TILE_TYPE);
        EnergyStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> blockEntity.getCapability(EnergyStorage.SIDED, direction).orElse(null), ModBlocks.BACKPACK_TILE_TYPE);
    }
}
