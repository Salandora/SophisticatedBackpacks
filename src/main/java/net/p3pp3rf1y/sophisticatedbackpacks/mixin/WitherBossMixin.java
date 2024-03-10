package net.p3pp3rf1y.sophisticatedbackpacks.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BlockInterfaceHelper;

@Mixin(WitherBoss.class)
public abstract class WitherBossMixin extends Entity {
	public WitherBossMixin(EntityType<?> entityType, Level level) {
		super(entityType, level);
	}

	@ModifyExpressionValue(method = "customServerAiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/boss/wither/WitherBoss;canDestroy(Lnet/minecraft/world/level/block/state/BlockState;)Z"))
	public boolean sophisticatedBackpacks$checkWalls(boolean original, @Local BlockPos blockPos, @Local BlockState blockState) {
		if (blockState.getBlock() instanceof BlockInterfaceHelper bih) {
			return !bih.canEntityDestroy(blockState, this.level, blockPos, this);
		}

		return original;
	}
}
