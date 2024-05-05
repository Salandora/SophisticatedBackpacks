package net.p3pp3rf1y.sophisticatedbackpacks.compat.litematica.mixin;

import com.google.common.collect.Lists;
import dev.emi.trinkets.api.TrinketsApi;
import fi.dy.masa.litematica.materials.MaterialListUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;

import java.util.List;

@Mixin(MaterialListUtils.class)
public class MaterialListUtilsMixin {
	@ModifyVariable(method = "getInventoryItemCounts", at = @At("HEAD"), argsOnly = true)
	private static Container sophisticatedBackpacks$wrapContainer(Container inv) {
		if (!(inv instanceof Inventory playerInv))
			return inv;

		Container trinketInventory = sophisticatedBackpacks$getTrinketInventories(playerInv.player);
		return new CompoundContainer(inv, trinketInventory);
	}

	@Unique
	private static Container sophisticatedBackpacks$getTrinketInventories(Player player) {
		List<ItemStack> stacks = Lists.newArrayList();
		TrinketsApi.getTrinketComponent(player).ifPresent(trinket -> trinket.getEquipped(stack -> stack.getItem() instanceof BackpackItem).forEach(t -> stacks.add(t.getB())));
		return new SimpleContainer(stacks.toArray(new ItemStack[0]));
	}
}
