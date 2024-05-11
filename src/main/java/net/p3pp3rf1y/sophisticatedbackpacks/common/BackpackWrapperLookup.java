package net.p3pp3rf1y.sophisticatedbackpacks.common;

import team.reborn.energy.api.EnergyStorage;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.porting_lib.base.util.LazyOptional;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.IBackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModBlocks;
import net.p3pp3rf1y.sophisticatedcore.api.IStorageWrapper;
import net.p3pp3rf1y.sophisticatedcore.common.CapabilityWrapper;

import static net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems.BACKPACKS;
import static net.p3pp3rf1y.sophisticatedcore.common.CapabilityWrapper.STORAGE_WRAPPER_CAPABILITY;

@SuppressWarnings("DataFlowIssue")
public class BackpackWrapperLookup {
    public static LazyOptional<IBackpackWrapper> get(ItemStack provider) {
        return CapabilityWrapper.get(provider).cast();
    }

    static {
        STORAGE_WRAPPER_CAPABILITY.registerForItems(BackpackItem.initCapabilities(), BACKPACKS);

		FluidStorage.ITEM.registerForItems((stack, ctx) -> get(stack).flatMap(IStorageWrapper::getFluidHandler).orElse(null), BACKPACKS);
		EnergyStorage.ITEM.registerForItems((stack, ctx) -> get(stack).flatMap(IStorageWrapper::getEnergyStorage).orElse(null), BACKPACKS);

        ItemStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> blockEntity.getCapability(ItemStorage.SIDED, direction).getValueUnsafer(), ModBlocks.BACKPACK_TILE_TYPE);
        FluidStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> blockEntity.getCapability(FluidStorage.SIDED, direction).getValueUnsafer(), ModBlocks.BACKPACK_TILE_TYPE);
        EnergyStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> blockEntity.getCapability(EnergyStorage.SIDED, direction).getValueUnsafer(), ModBlocks.BACKPACK_TILE_TYPE);
    }
}
