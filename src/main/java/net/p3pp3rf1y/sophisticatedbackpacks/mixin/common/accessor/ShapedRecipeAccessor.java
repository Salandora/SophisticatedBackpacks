package net.p3pp3rf1y.sophisticatedbackpacks.mixin.common.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.ShapedRecipe;

@Mixin(ShapedRecipe.class)
public interface ShapedRecipeAccessor {
	@Accessor
	ItemStack getResult();
}
