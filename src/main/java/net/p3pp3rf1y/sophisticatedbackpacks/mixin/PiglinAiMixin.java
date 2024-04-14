package net.p3pp3rf1y.sophisticatedbackpacks.mixin;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedbackpacks.compat.CompatModIds;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems;
import net.p3pp3rf1y.sophisticatedbackpacks.util.PlayerInventoryProvider;

import java.util.Iterator;
import java.util.List;

@Mixin(PiglinAi.class)
public class PiglinAiMixin {
	@Redirect(method = "isWearingGold", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getArmorSlots()Ljava/lang/Iterable;"))
	private static Iterable<ItemStack> sophisticatedbackpacks$isWearingGold$getArmorSlots(LivingEntity instance) {
		if (instance instanceof Player player) {
			List<ItemStack> trinkets = Lists.newArrayList();
			PlayerInventoryProvider.get().runOnBackpacks(player, CompatModIds.TRINKETS, (backpack, inventoryHandlerName, identifier, slot) -> trinkets.add(backpack));
			return Iterables.concat(instance.getArmorSlots(), trinkets);
		}

		return instance.getArmorSlots();
	}

    @Inject(method = "isWearingGold(Lnet/minecraft/world/entity/LivingEntity;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;"), locals = LocalCapture.CAPTURE_FAILEXCEPTION, cancellable = true)
    private static void sophisticatedbackpacks$isWearingGold(LivingEntity livingEntity, CallbackInfoReturnable<Boolean> cir, Iterable<ItemStack> iterable, Iterator<ItemStack> var2, ItemStack itemStack) {
        if (itemStack.getItem() == ModItems.GOLD_BACKPACK) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }
}
