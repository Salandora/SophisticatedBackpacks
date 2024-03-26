package net.p3pp3rf1y.porting_lib.shearable.mixin.common;

import org.spongepowered.asm.mixin.Mixin;

import io.github.fabricators_of_create.porting_lib.extensions.IShearable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.VineBlock;

@Mixin(VineBlock.class)
public abstract class VineBlockMixin implements IShearable {
	@Override
	public boolean isShearable(ItemStack item, Level world, BlockPos pos) {
		return true;
	}
}