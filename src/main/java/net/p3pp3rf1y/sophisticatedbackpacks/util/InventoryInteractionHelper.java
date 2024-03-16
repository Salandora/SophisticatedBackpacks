package net.p3pp3rf1y.sophisticatedbackpacks.util;

import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.p3pp3rf1y.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.p3pp3rf1y.sophisticatedbackpacks.Config;
import net.p3pp3rf1y.sophisticatedbackpacks.api.IItemHandlerInteractionUpgrade;
import net.p3pp3rf1y.sophisticatedbackpacks.common.BackpackWrapperLookup;
import net.p3pp3rf1y.sophisticatedcore.api.IStorageWrapper;

import java.util.List;

public class InventoryInteractionHelper {
	private InventoryInteractionHelper() {}

	public static boolean tryInventoryInteraction(UseOnContext context) {
		Player player = context.getPlayer();
		if (player == null) {
			return false;
		}
		return tryInventoryInteraction(context.getClickedPos(), context.getLevel(), context.getItemInHand(), context.getClickedFace(), player);
	}

	@SuppressWarnings("unused")
	public static boolean tryInventoryInteraction(BlockPos pos, Level world, ItemStack backpack, Direction face, Player player) {
		if (Config.COMMON.noInteractionBlocks.isBlockInteractionDisallowed(world.getBlockState(pos).getBlock())) {
			return false;
		}

		Storage<ItemVariant> storage = ItemStorage.SIDED.find(world, pos, null);
		if (storage instanceof SlottedStorage<ItemVariant> invStorage) {
			return player.level.isClientSide || BackpackWrapperLookup.get(backpack)
					.map(wrapper -> tryRunningInteractionWrappers(invStorage, wrapper, player))
					.orElse(false);
		}

		return false;
	}

	private static boolean tryRunningInteractionWrappers(SlottedStorage<ItemVariant> itemHandler, IStorageWrapper wrapper, Player player) {
		List<IItemHandlerInteractionUpgrade> wrappers = wrapper.getUpgradeHandler().getWrappersThatImplement(IItemHandlerInteractionUpgrade.class);
		if (wrappers.isEmpty()) {
			return false;
		}
		wrappers.forEach(upgrade -> upgrade.onHandlerInteract(itemHandler, player));
		return true;
	}
}
