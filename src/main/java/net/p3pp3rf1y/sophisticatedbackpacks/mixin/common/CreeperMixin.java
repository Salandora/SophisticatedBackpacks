package net.p3pp3rf1y.sophisticatedbackpacks.mixin.common;

import net.minecraft.world.entity.monster.Creeper;
import net.p3pp3rf1y.sophisticatedbackpacks.common.EntityBackpackAdditionHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Creeper.class)
public class CreeperMixin {
    @Inject(
            method = "explodeCreeper",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;explode(Lnet/minecraft/world/entity/Entity;DDDFLnet/minecraft/world/level/Level$ExplosionInteraction;)Lnet/minecraft/world/level/Explosion;"
            )
    )
    private void sophisticatedBackpacks$explodeCreeper(CallbackInfo ci) {
        EntityBackpackAdditionHandler.removeBeneficialEffects((Creeper) (Object) this);
    }
}
