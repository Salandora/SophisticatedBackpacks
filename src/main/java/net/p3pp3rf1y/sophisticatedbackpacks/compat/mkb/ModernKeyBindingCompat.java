package net.p3pp3rf1y.sophisticatedbackpacks.compat.mkb;

import com.mojang.blaze3d.platform.InputConstants;
import committee.nova.mkb.api.IKeyBinding;
import committee.nova.mkb.keybinding.KeyConflictContext;
import committee.nova.mkb.keybinding.KeyModifier;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.Screen;
import net.p3pp3rf1y.sophisticatedcore.compat.ICompat;

import static com.mojang.blaze3d.platform.InputConstants.KEY_X;
import static com.mojang.blaze3d.platform.InputConstants.KEY_Z;
import static net.p3pp3rf1y.sophisticatedbackpacks.client.KeybindHandler.BACKPACK_OPEN_KEYBIND;
import static net.p3pp3rf1y.sophisticatedbackpacks.client.KeybindHandler.BACKPACK_TOGGLE_UPGRADE_1;
import static net.p3pp3rf1y.sophisticatedbackpacks.client.KeybindHandler.BACKPACK_TOGGLE_UPGRADE_2;
import static net.p3pp3rf1y.sophisticatedbackpacks.client.KeybindHandler.INVENTORY_INTERACTION_KEYBIND;
import static net.p3pp3rf1y.sophisticatedbackpacks.client.KeybindHandler.SORT_KEYBIND;
import static net.p3pp3rf1y.sophisticatedbackpacks.client.KeybindHandler.TOOL_SWAP_KEYBIND;
import static net.p3pp3rf1y.sophisticatedbackpacks.client.KeybindHandler.UPGRADE_SLOT_TOGGLE_KEYBINDS;
import static net.p3pp3rf1y.sophisticatedbackpacks.client.KeybindHandler.sendBackpackOpenOrCloseMessage;
import static net.p3pp3rf1y.sophisticatedbackpacks.client.KeybindHandler.tryCallSort;

public class ModernKeyBindingCompat implements ICompat {
	@Override
	public void setup() {
		if (!IKeyBinding.class.isAssignableFrom(KeyMapping.class)) {
			return;
		}

		((IKeyBinding) TOOL_SWAP_KEYBIND).setKeyConflictContext(KeyConflictContext.IN_GAME);
		((IKeyBinding) INVENTORY_INTERACTION_KEYBIND).setKeyConflictContext(KeyConflictContext.IN_GAME);
		((IKeyBinding) BACKPACK_OPEN_KEYBIND).setKeyConflictContext(BackpackKeyConflictContext.INSTANCE);
		((IKeyBinding) SORT_KEYBIND).setKeyConflictContext(BackpackGuiKeyConflictContext.INSTANCE);

		((IKeyBinding) BACKPACK_TOGGLE_UPGRADE_1).setKeyModifierAndCode(KeyModifier.ALT, InputConstants.Type.KEYSYM.getOrCreate(KEY_Z));
		((IKeyBinding) BACKPACK_TOGGLE_UPGRADE_2).setKeyModifierAndCode(KeyModifier.ALT, InputConstants.Type.KEYSYM.getOrCreate(KEY_X));

		UPGRADE_SLOT_TOGGLE_KEYBINDS.forEach((slot, keybind) -> ((IKeyBinding)keybind).setKeyConflictContext(KeyConflictContext.UNIVERSAL));

		ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
			ScreenKeyboardEvents.allowKeyPress(screen).register(ModernKeyBindingCompat::handleGuiKeyPress);
			ScreenMouseEvents.allowMouseClick(screen).register(ModernKeyBindingCompat::handleGuiMouseKeyPress);
		});
	}

	public static boolean handleGuiKeyPress(Screen screen, int keycode, int scancode, int modifiers) {
		InputConstants.Key key = InputConstants.getKey(keycode, scancode);
		if (((IKeyBinding) SORT_KEYBIND).isActiveAndMatches(key) && tryCallSort(screen)) {
			return false;
		} else if (((IKeyBinding) BACKPACK_OPEN_KEYBIND).isActiveAndMatches(key) && sendBackpackOpenOrCloseMessage()) {
			return false;
		}

		return true;
	}

	public static boolean handleGuiMouseKeyPress(Screen screen, double mouseX, double mouseY, int button) {
		InputConstants.Key key = InputConstants.Type.MOUSE.getOrCreate(button);
		if (((IKeyBinding) SORT_KEYBIND).isActiveAndMatches(key) && tryCallSort(screen)) {
			return false;
		} else if (((IKeyBinding) BACKPACK_OPEN_KEYBIND).isActiveAndMatches(key) && sendBackpackOpenOrCloseMessage()) {
			return false;
		}

		return true;
	}
}
