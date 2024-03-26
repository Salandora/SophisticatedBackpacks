package net.p3pp3rf1y.porting_lib.tool_actions.mixin;

import org.spongepowered.asm.mixin.Mixin;

import io.github.fabricators_of_create.porting_lib.extensions.tool.ItemExtensions;
import io.github.fabricators_of_create.porting_lib.util.ToolAction;
import io.github.fabricators_of_create.porting_lib.util.ToolActions;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;

@Mixin(ShearsItem.class)
public abstract class ShearsItemMixin implements ItemExtensions {
	@Override
	public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
		return ToolActions.DEFAULT_SHEARS_ACTIONS.contains(toolAction);
	}
}
