package net.p3pp3rf1y.sophisticatedbackpacks.mixin.common.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.inventory.AnvilMenu;

@Mixin(AnvilMenu.class)
public interface AnvilMenuAccessor {
	@Accessor
	String getItemName();
}
