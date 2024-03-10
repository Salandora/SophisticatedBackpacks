package net.p3pp3rf1y.sophisticatedbackpacks.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BlockInterfaceHelper;

@Mixin(EnderDragon.class)
public class EnderDragonMixin extends Mob {
	protected EnderDragonMixin(EntityType<? extends Mob> entityType, Level level) {
		super(entityType, level);
	}

	@Redirect(method = "checkWalls", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/tags/TagKey;)Z"))
	public boolean sophisticatedBackpacks$checkWalls(BlockState blockState, TagKey<Block> tagKey, @Local BlockPos blockPos) {
		if (blockState.getBlock() instanceof BlockInterfaceHelper bih) {
			return !bih.canEntityDestroy(blockState, this.level, blockPos, this);
		}

		return blockState.is(tagKey);
	}
}
