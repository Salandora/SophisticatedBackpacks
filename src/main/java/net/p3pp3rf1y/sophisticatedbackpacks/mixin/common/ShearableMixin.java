package net.p3pp3rf1y.sophisticatedbackpacks.mixin.common;

import net.minecraft.world.entity.Shearable;
import net.p3pp3rf1y.sophisticatedbackpacks.api.SophisticatedShearable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Shearable.class)
public interface ShearableMixin extends SophisticatedShearable {
}
