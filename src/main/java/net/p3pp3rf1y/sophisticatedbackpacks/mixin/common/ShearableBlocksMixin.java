package net.p3pp3rf1y.sophisticatedbackpacks.mixin.common;

import net.minecraft.world.level.block.*;
import net.p3pp3rf1y.sophisticatedbackpacks.api.SophisticatedShearable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ DeadBushBlock.class, LeavesBlock.class, SeagrassBlock.class, TallGrassBlock.class, VineBlock.class, WebBlock.class })
public class ShearableBlocksMixin implements SophisticatedShearable {
}
