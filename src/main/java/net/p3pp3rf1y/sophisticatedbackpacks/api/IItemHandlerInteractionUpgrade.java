package net.p3pp3rf1y.sophisticatedbackpacks.api;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.world.entity.player.Player;

public interface IItemHandlerInteractionUpgrade {
	void onHandlerInteract(Storage<ItemVariant> itemHandler, Player player);
}
