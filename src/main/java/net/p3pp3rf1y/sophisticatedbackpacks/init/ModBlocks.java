package net.p3pp3rf1y.sophisticatedbackpacks.init;

import team.reborn.energy.api.EnergyStorage;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackBlock;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackBlockEntity;

import java.util.function.Supplier;

public class ModBlocks {

    private ModBlocks() {
    }

	public static final BackpackBlock BACKPACK = register("backpack", BackpackBlock::new);
	public static final BackpackBlock COPPER_BACKPACK = register("copper_backpack", BackpackBlock::new);
	public static final BackpackBlock IRON_BACKPACK = register("iron_backpack", BackpackBlock::new);
	public static final BackpackBlock GOLD_BACKPACK = register("gold_backpack", BackpackBlock::new);
	public static final BackpackBlock DIAMOND_BACKPACK = register("diamond_backpack", BackpackBlock::new);
	public static final BackpackBlock NETHERITE_BACKPACK = register("netherite_backpack", () -> new BackpackBlock(1200));

	public static BackpackBlock[] BACKPACKS = new BackpackBlock[] { BACKPACK, COPPER_BACKPACK, IRON_BACKPACK, GOLD_BACKPACK, DIAMOND_BACKPACK, NETHERITE_BACKPACK };

	@SuppressWarnings("ConstantConditions") //no datafixer type needed
	public static final BlockEntityType<BackpackBlockEntity> BACKPACK_TILE_TYPE = registerEntityType("backpack", () ->
			BlockEntityType.Builder.of(BackpackBlockEntity::new, BACKPACKS)
					.build(null));

	public static <T extends Block> T register(String id, Supplier<T> supplier) {
		return Registry.register(BuiltInRegistries.BLOCK, SophisticatedBackpacks.getRL(id), supplier.get());
	}
	public static <T extends BlockEntityType<?>> T registerEntityType(String id, Supplier<T> supplier) {
		return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, SophisticatedBackpacks.getRL(id), supplier.get());
	}

	public static void registerEvents() {
		UseBlockCallback.EVENT.register(BackpackBlock::playerInteract);
		registerCapabilities();
	}

	private static void registerCapabilities() {
		ItemStorage.SIDED.registerForBlockEntity(BackpackBlockEntity::getExternalItemHandler, ModBlocks.BACKPACK_TILE_TYPE);
		FluidStorage.SIDED.registerForBlockEntity(BackpackBlockEntity::getExternalFluidHandler, ModBlocks.BACKPACK_TILE_TYPE);
		EnergyStorage.SIDED.registerForBlockEntity(BackpackBlockEntity::getExternalEnergyStorage, ModBlocks.BACKPACK_TILE_TYPE);
	}
}
