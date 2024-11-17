package net.p3pp3rf1y.sophisticatedbackpacks.api;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Shearable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public interface SophisticatedShearable {
	default boolean sophisticatedBackpacks_isShearable(@Nullable Player player, ItemStack item, Level level, BlockPos pos) {
		// Default to checking readyForShearing if we are the vanilla shearable interface, and if we aren't assume a default of true
		return !(this instanceof Shearable shearable) || shearable.readyForShearing();
	}
}
