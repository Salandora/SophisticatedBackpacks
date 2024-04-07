package net.p3pp3rf1y.sophisticatedbackpacks.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedcore.util.ItemBase;

@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {
	@Shadow
	private ItemStack mainHandItem;

	@Shadow
	private ItemStack offHandItem;

	@Unique
	private int slotMainHand = 0;

	@Unique
	private boolean shouldCauseReequipAnimation(ItemStack from, ItemStack to, int slot) {
		if (!(from.getItem() instanceof ItemBase) || !(to.getItem() instanceof ItemBase)) {
			return true;
		}

		boolean changed = false;
		if (slot != -1) {
			changed = slot != slotMainHand;
			slotMainHand = slot;
		}
		return changed;
	}

	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getAttackStrengthScale(F)F"), locals = LocalCapture.CAPTURE_FAILHARD)
	private void sophisticatedbackpacks$skipRequipAnimMainHand(CallbackInfo ci, LocalPlayer localPlayer, ItemStack itemStack, ItemStack itemStack1) {
		boolean reequipMain = shouldCauseReequipAnimation(this.mainHandItem, itemStack, localPlayer.getInventory().selected);
		if (!reequipMain && this.mainHandItem != itemStack) {
			this.mainHandItem = itemStack;
		}

		boolean reequipOff = shouldCauseReequipAnimation(this.offHandItem, itemStack1, -1);
		if (!reequipOff && this.offHandItem != itemStack1) {
			this.offHandItem = itemStack1;
		}
	}

}
