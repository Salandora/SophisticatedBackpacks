package net.p3pp3rf1y.sophisticatedbackpacks.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BlockInterfaceHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(WitherSkull.class)
public class WitherSkullMixin {
	@ModifyExpressionValue(method = "getBlockExplosionResistance", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/WitherSkull;isDangerous()Z"))
	public boolean sophisticatedBackpacks$canDestroy(boolean original, @Local(argsOnly = true) BlockGetter blockGetter, @Local(argsOnly = true) BlockPos blockPos, @Local(argsOnly = true) BlockState blockState) {
		if (blockState.getBlock() instanceof BlockInterfaceHelper bih) {
			return original && bih.canEntityDestroy(blockState, blockGetter, blockPos, (Entity)(Object) this);
		}

		return original;
	}
}
