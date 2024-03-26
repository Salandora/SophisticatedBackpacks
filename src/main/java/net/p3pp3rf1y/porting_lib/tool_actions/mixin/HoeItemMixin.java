package net.p3pp3rf1y.porting_lib.tool_actions.mixin;

import org.spongepowered.asm.mixin.Mixin;

import io.github.fabricators_of_create.porting_lib.extensions.tool.ItemExtensions;
import io.github.fabricators_of_create.porting_lib.util.ToolAction;
import io.github.fabricators_of_create.porting_lib.util.ToolActions;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;

@Mixin(HoeItem.class)
public abstract class HoeItemMixin implements ItemExtensions {
	@Override
	public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
		return ToolActions.DEFAULT_HOE_ACTIONS.contains(toolAction);
	}
}
