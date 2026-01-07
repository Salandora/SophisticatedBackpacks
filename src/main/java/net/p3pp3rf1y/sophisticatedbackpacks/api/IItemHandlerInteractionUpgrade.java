package net.p3pp3rf1y.sophisticatedbackpacks.api;

import com.github.salandora.sophisticatedfabriclib.transfer.api.v1.IItemHandler;
import net.minecraft.world.entity.player.Player;

public interface IItemHandlerInteractionUpgrade {
	void onHandlerInteract(IItemHandler itemHandler, Player player);
}
