package net.p3pp3rf1y.sophisticatedbackpacks.mixin;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedbackpacks.compat.CompatModIds;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems;
import net.p3pp3rf1y.sophisticatedbackpacks.util.PlayerInventoryProvider;

import java.util.List;

@Mixin(PiglinAi.class)
public class PiglinAiMixin {
	@Inject(method = "isWearingGold", at = @At(value = "HEAD"), cancellable = true)
	private static void sophisticatedBackpacks$isWearingGold(LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
		for (ItemStack itemStack : sophisticatedBackpacks$getArmorSlots(entity)) {
			if (itemStack.getItem() == ModItems.GOLD_BACKPACK) {
				cir.setReturnValue(true);
				return;
			}
		}
	}

	@Unique
	private static Iterable<ItemStack> sophisticatedBackpacks$getArmorSlots(LivingEntity entity) {
		if (entity instanceof Player player) {
			List<ItemStack> trinkets = Lists.newArrayList();
			PlayerInventoryProvider.get().runOnBackpacks(player, CompatModIds.TRINKETS, (backpack, inventoryHandlerName, identifier, slot) -> trinkets.add(backpack));
			return Iterables.concat(entity.getArmorSlots(), trinkets);
		}

		return entity.getArmorSlots();
	}
}
