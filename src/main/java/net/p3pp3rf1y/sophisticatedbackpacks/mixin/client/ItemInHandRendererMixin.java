package net.p3pp3rf1y.sophisticatedbackpacks.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {
	@Shadow
	private ItemStack mainHandItem;

	@Shadow
	private ItemStack offHandItem;

	@Unique
	private int slotMainHand = 0;

	@Unique
	private boolean sophisticatedBackpacks_shouldCauseReequipAnimation(ItemStack from, ItemStack to, int slot) {
		boolean fromEmpty = from.isEmpty();
		boolean toEmpty = to.isEmpty();

		if (fromEmpty && toEmpty) return false;
		if (fromEmpty || toEmpty) return true;

		boolean changed = false;
		if (slot != -1) {
			changed = slot != slotMainHand;
			slotMainHand = slot;
		}
		return from.getItem().sophisticatedLibrary_shouldCauseReequipAnimation(from, to, changed);
	}

	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getAttackStrengthScale(F)F"))
	private void sophisticatedBackpacks$skipReEquipAnimMainHand(CallbackInfo ci, @Local LocalPlayer localPlayer, @Local(ordinal = 0)	ItemStack itemStack,
																@Local(ordinal = 1) ItemStack itemStack1) {
		boolean reequipMain = sophisticatedBackpacks_shouldCauseReequipAnimation(this.mainHandItem, itemStack, localPlayer.getInventory().selected);
		if (!reequipMain && this.mainHandItem != itemStack) {
			this.mainHandItem = itemStack;
		}

		boolean reequipOff = sophisticatedBackpacks_shouldCauseReequipAnimation(this.offHandItem, itemStack1, -1);
		if (!reequipOff && this.offHandItem != itemStack1) {
			this.offHandItem = itemStack1;
		}
	}

}
