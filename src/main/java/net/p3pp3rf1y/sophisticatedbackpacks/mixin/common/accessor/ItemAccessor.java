package net.p3pp3rf1y.sophisticatedbackpacks.mixin.common.accessor;

import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.UUID;

@Mixin(Item.class)
public interface ItemAccessor {
	@Accessor("BASE_ATTACK_DAMAGE_UUID")
	static UUID getBaseAttackDamageUUID() {
		return null;
	}
}
