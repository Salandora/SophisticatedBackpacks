package net.p3pp3rf1y.sophisticatedbackpacks.settings;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.p3pp3rf1y.sophisticatedcore.settings.MainSetting;
import net.p3pp3rf1y.sophisticatedcore.settings.SettingsManager;
import net.p3pp3rf1y.sophisticatedcore.settings.main.MainSettingsCategory;
import net.p3pp3rf1y.sophisticatedcore.util.NBTHelper;

import java.util.function.Consumer;

public class BackpackMainSettingsCategory extends MainSettingsCategory<BackpackMainSettingsCategory> {
	public static final String SOPHISTICATED_BACKPACK_SETTINGS_PLAYER_TAG = "sophisticatedBackpackSettings";
	public static final MainSetting<Boolean> ANOTHER_PLAYER_CAN_OPEN =
			new MainSetting<>("anotherPlayerCanOpen", NBTHelper::getBoolean, CompoundTag::putBoolean, true);

	public static final String NAME = "backpackGlobal";

	static {
		SettingsManager.addSetting(ANOTHER_PLAYER_CAN_OPEN);

		ServerPlayerEvents.AFTER_RESPAWN.register(BackpackMainSettingsCategory::onPlayerClone);
	}

	public BackpackMainSettingsCategory(CompoundTag categoryNbt, Consumer<CompoundTag> saveNbt) {
		super(categoryNbt, saveNbt, SOPHISTICATED_BACKPACK_SETTINGS_PLAYER_TAG);
	}

	private static void onPlayerClone(ServerPlayer newPlayer, ServerPlayer oldPlayer, boolean wasDeath) {
		CompoundTag oldData = oldPlayer.getSophisticatedCustomData();
		CompoundTag newData = newPlayer.getSophisticatedCustomData();

		if (oldData.contains(SOPHISTICATED_BACKPACK_SETTINGS_PLAYER_TAG)) {
			//noinspection ConstantConditions
			newData.put(SOPHISTICATED_BACKPACK_SETTINGS_PLAYER_TAG, oldData.get(SOPHISTICATED_BACKPACK_SETTINGS_PLAYER_TAG));
		}
	}
}
