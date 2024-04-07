package net.p3pp3rf1y.sophisticatedbackpacks.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BlockInterfaceHelper;

import java.util.Optional;

@Mixin(ExplosionDamageCalculator.class)
public class ExplosionDamageCalculatorMixin {
	@Inject(method = "getBlockExplosionResistance", at = @At(value = "INVOKE", target = "Ljava/lang/Math;max(FF)F"), cancellable = true)
	public void sophisticatedBackpacks$getBlockExplosionResistance(Explosion explosion, BlockGetter reader, BlockPos pos, BlockState state, FluidState fluid, CallbackInfoReturnable<Optional<Float>> cir) {
		if (state.getBlock() instanceof BlockInterfaceHelper) {
			cir.setReturnValue(Optional.of(Math.max(((BlockInterfaceHelper) state.getBlock()).getExplosionResistance(state, reader, pos, explosion), fluid.getExplosionResistance())));
		}
	}
}
