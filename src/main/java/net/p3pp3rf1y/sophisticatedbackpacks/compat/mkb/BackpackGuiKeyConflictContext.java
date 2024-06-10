package net.p3pp3rf1y.sophisticatedbackpacks.compat.mkb;

import committee.nova.mkb.api.IKeyConflictContext;

import net.minecraft.client.Minecraft;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.BackpackScreen;

import static committee.nova.mkb.keybinding.KeyConflictContext.GUI;

public class BackpackGuiKeyConflictContext implements IKeyConflictContext {
	public static final BackpackGuiKeyConflictContext INSTANCE = new BackpackGuiKeyConflictContext();

	@Override
	public boolean isActive() {
		return GUI.isActive() && Minecraft.getInstance().screen instanceof BackpackScreen;
	}

	@Override
	public boolean conflicts(IKeyConflictContext other) {
		return this == other;
	}
}
