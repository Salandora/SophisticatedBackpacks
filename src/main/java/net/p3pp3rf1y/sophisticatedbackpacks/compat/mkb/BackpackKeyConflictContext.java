package net.p3pp3rf1y.sophisticatedbackpacks.compat.mkb;

import committee.nova.mkb.api.IKeyConflictContext;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

import static committee.nova.mkb.keybinding.KeyConflictContext.GUI;

public class BackpackKeyConflictContext  implements IKeyConflictContext {
	public static final BackpackKeyConflictContext INSTANCE = new BackpackKeyConflictContext();

	@Override
	public boolean isActive() {
		return !GUI.isActive() || Minecraft.getInstance().screen instanceof AbstractContainerScreen<?>;
	}

	@Override
	public boolean conflicts(IKeyConflictContext other) {
		return this == other;
	}
}
