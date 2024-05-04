package net.p3pp3rf1y.sophisticatedbackpacks.common;

import com.google.common.collect.MapMaker;
import team.reborn.energy.api.EnergyStorage;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.IBackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModBlocks;
import net.p3pp3rf1y.sophisticatedcore.common.CapabilityWrapper;

import java.util.Map;
import java.util.Optional;

import static net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems.BACKPACKS;
import static net.p3pp3rf1y.sophisticatedcore.common.CapabilityWrapper.STORAGE_WRAPPER_CAPABILITY;

public class BackpackWrapperLookup {
	private static final Map<ItemStack, BackpackWrapper> WRAPPERS = new MapMaker().weakKeys().weakValues().makeMap();

    public static Optional<IBackpackWrapper> get(ItemStack provider) {
        return CapabilityWrapper.get(provider, IBackpackWrapper.class);
    }

    static {
        STORAGE_WRAPPER_CAPABILITY.registerForItems((itemStack, context) -> WRAPPERS.computeIfAbsent(itemStack, BackpackWrapper::new), BACKPACKS);

        ItemStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> blockEntity.getCapability(ItemStorage.SIDED, direction), ModBlocks.BACKPACK_TILE_TYPE);
        FluidStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> blockEntity.getCapability(FluidStorage.SIDED, direction), ModBlocks.BACKPACK_TILE_TYPE);
        EnergyStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> blockEntity.getCapability(EnergyStorage.SIDED, direction), ModBlocks.BACKPACK_TILE_TYPE);
    }
}
