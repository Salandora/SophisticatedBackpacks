package net.p3pp3rf1y.sophisticatedbackpacks.mixin.common.accessor;

import net.minecraft.world.inventory.AnvilMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AnvilMenu.class)
public interface AnvilMenuAccessor {
	@Accessor("itemName")
	String itemName();
}
