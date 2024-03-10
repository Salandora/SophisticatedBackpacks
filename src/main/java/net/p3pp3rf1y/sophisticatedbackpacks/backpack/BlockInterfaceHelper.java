package net.p3pp3rf1y.sophisticatedbackpacks.backpack;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public interface BlockInterfaceHelper {
	default float getExplosionResistance(BlockState state, BlockGetter level, BlockPos pos, Explosion explosion) {
		return ((Block) this).getExplosionResistance();
	}

	default boolean canEntityDestroy(BlockState state, BlockGetter level, BlockPos pos, Entity entity) {
		if (entity instanceof EnderDragon) {
			return !((Block)this).defaultBlockState().is(BlockTags.DRAGON_IMMUNE);
		} else if ((entity instanceof WitherBoss) ||
				(entity instanceof WitherSkull)) {
			return state.isAir() || WitherBoss.canDestroy(state);
		}

		return true;
	}
}
