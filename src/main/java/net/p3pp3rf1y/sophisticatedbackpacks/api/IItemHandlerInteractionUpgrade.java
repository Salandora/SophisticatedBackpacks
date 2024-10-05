package net.p3pp3rf1y.sophisticatedbackpacks.api;

import net.minecraft.world.entity.player.Player;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;

public interface IItemHandlerInteractionUpgrade {
	void onHandlerInteract(SlottedStorage<ItemVariant> itemHandler, Player player);
}
