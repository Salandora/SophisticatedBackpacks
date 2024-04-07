package net.p3pp3rf1y.sophisticatedbackpacks.mixin.common.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.RecordItem;

import java.util.Map;

@Mixin(RecordItem.class)
public interface RecordItemAccessor {
	@Accessor("BY_NAME")
	static Map<SoundEvent, RecordItem> getByName() {
		return null;
	}
}
