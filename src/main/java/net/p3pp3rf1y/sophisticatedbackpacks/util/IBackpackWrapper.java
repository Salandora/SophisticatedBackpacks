package net.p3pp3rf1y.sophisticatedbackpacks.util;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.p3pp3rf1y.sophisticatedbackpacks.blocks.tile.BackpackTileEntity;
import net.p3pp3rf1y.sophisticatedbackpacks.items.ScreenProperties;

import java.util.Optional;

public interface IBackpackWrapper {
	void linkToTileEntity(BackpackTileEntity te);

	BackpackInventoryHandler getInventoryHandler();

	IItemHandler getFilteredHandler();

	ScreenProperties getScreenProperties();

	void copyDataTo(IBackpackWrapper otherBackpackWrapper);

	BackpackUpgradeHandler getUpgradeHandler();

	int getClothColor();

	int getBorderColor();

	Optional<Integer> getOpenTabId();

	void setOpenTabId(int openTabId);

	void removeOpenTabId();

	void setColors(int clothColor, int borderColor);

	ItemStack getBackpack();
}
