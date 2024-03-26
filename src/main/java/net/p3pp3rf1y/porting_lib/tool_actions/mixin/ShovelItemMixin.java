package net.p3pp3rf1y.porting_lib.tool_actions.mixin;

import org.spongepowered.asm.mixin.Mixin;

import io.github.fabricators_of_create.porting_lib.extensions.tool.ItemExtensions;
import io.github.fabricators_of_create.porting_lib.util.ToolAction;
import io.github.fabricators_of_create.porting_lib.util.ToolActions;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;

@Mixin(ShovelItem.class)
public abstract class ShovelItemMixin implements ItemExtensions {
	@Override
	public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
		return ToolActions.DEFAULT_SHOVEL_ACTIONS.contains(toolAction);
	}
}
