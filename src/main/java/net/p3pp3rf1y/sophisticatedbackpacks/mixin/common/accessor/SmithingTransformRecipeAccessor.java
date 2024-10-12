package net.p3pp3rf1y.sophisticatedbackpacks.mixin.common.accessor;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SmithingTransformRecipe.class)
public interface SmithingTransformRecipeAccessor {
	@Accessor("addition")
	Ingredient getAddition();
	
	@Accessor("base")
	Ingredient getBase();
	
	@Accessor("template")
	Ingredient getTemplate();

	@Accessor
	ItemStack getResult();
}
