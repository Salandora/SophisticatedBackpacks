package net.p3pp3rf1y.sophisticatedbackpacks.mixin.common;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedbackpacks.compat.CompatModIds;
import net.p3pp3rf1y.sophisticatedbackpacks.util.PlayerInventoryProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(PiglinAi.class)
public class PiglinAiMixin {
	@ModifyExpressionValue(
			method = "isWearingGold",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/LivingEntity;getArmorAndBodyArmorSlots()Ljava/lang/Iterable;"
			)
	)
	private static Iterable<ItemStack> sophisticatedBackpacks$injectTrinketBackpacks(Iterable<ItemStack> original, @Local(argsOnly = true) LivingEntity livingEntity) {
		if (livingEntity instanceof Player player) {
			List<ItemStack> trinkets = Lists.newArrayList();
			PlayerInventoryProvider.get().runOnBackpacks(player, CompatModIds.TRINKETS, (backpack, inventoryHandlerName, identifier, slot) -> trinkets.add(backpack));
			return Iterables.concat(original, trinkets);
		}

		return original;
	}
}
