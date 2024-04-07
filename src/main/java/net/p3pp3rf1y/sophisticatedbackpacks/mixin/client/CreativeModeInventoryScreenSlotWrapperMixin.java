package net.p3pp3rf1y.sophisticatedbackpacks.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;

@Mixin(targets = { "net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen$SlotWrapper" })
public class CreativeModeInventoryScreenSlotWrapperMixin extends Slot {

	@Shadow
	@Final
	Slot target;

	public CreativeModeInventoryScreenSlotWrapperMixin(Container container, int slot, int x, int y) {
		super(container, slot, x, y);
	}

	@Override
	public int getContainerSlot() {
		return this.target.getContainerSlot();
	}
}
