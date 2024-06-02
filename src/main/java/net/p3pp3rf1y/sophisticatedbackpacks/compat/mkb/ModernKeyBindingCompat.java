package net.p3pp3rf1y.sophisticatedbackpacks.compat.mkb;

import com.mojang.blaze3d.platform.InputConstants;
import committee.nova.mkb.api.IKeyBinding;
import committee.nova.mkb.keybinding.KeyConflictContext;
import committee.nova.mkb.keybinding.KeyModifier;

import net.minecraft.client.KeyMapping;
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
	}
}
