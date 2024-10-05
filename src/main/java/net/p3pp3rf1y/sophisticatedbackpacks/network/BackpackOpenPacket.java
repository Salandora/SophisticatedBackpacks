package net.p3pp3rf1y.sophisticatedbackpacks.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.BackpackContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.BackpackContext;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.IContextAwareContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.util.PlayerInventoryProvider;
import net.p3pp3rf1y.sophisticatedcore.util.MenuProviderHelper;

public class BackpackOpenPacket implements FabricPacket {
	public static final PacketType<BackpackOpenPacket> TYPE = PacketType.create(new ResourceLocation(SophisticatedBackpacks.MOD_ID, "backpack_open"), BackpackOpenPacket::new);
	private static final int CHEST_SLOT = 38;
	private static final int OFFHAND_SLOT = 40;
	private final int slotIndex;
	private final String identifier;
	private final String handlerName;

	public BackpackOpenPacket() {
		this(-1);
	}

	public BackpackOpenPacket(int backpackSlot) {
		this(backpackSlot, "");
	}

	public BackpackOpenPacket(int backpackSlot, String identifier, String handlerName) {
		slotIndex = backpackSlot;
		this.identifier = identifier;
		this.handlerName = handlerName;
	}

	public BackpackOpenPacket(int backpackSlot, String identifier) {
		this(backpackSlot, identifier, "");
	}

	public BackpackOpenPacket(FriendlyByteBuf buffer) {
		this(buffer.readInt(), buffer.readUtf(), buffer.readUtf());
	}

	public void handle(ServerPlayer player, PacketSender responseSender) {
		if (!handlerName.isEmpty()) {
			int adjustedSlotIndex = slotIndex;
			if (adjustedSlotIndex == CHEST_SLOT) {
				adjustedSlotIndex -= 36;
			} else if (adjustedSlotIndex == OFFHAND_SLOT) {
				adjustedSlotIndex = 0;
			}
			BackpackContext.Item backpackContext = new BackpackContext.Item(handlerName, identifier, adjustedSlotIndex,
					player.containerMenu instanceof InventoryMenu || (player.containerMenu instanceof BackpackContainer backpackContainer && backpackContainer.getBackpackContext().wasOpenFromInventory()));
			openBackpack(player, backpackContext);
		} else if (player.containerMenu instanceof BackpackContainer backpackContainer) {
			BackpackContext backpackContext = backpackContainer.getBackpackContext();
			if (slotIndex == -1) {
				openBackpack(player, backpackContext.getParentBackpackContext());
			} else if (backpackContainer.isStorageInventorySlot(slotIndex)) {
				openBackpack(player, backpackContext.getSubBackpackContext(slotIndex));
			}
		} else if (player.containerMenu instanceof IContextAwareContainer contextAwareContainer) {
			BackpackContext backpackContext = contextAwareContainer.getBackpackContext();
			openBackpack(player, backpackContext);
		} else {
			findAndOpenFirstBackpack(player);
		}
	}

	private void findAndOpenFirstBackpack(Player player) {
		PlayerInventoryProvider.get().runOnBackpacks(player, (backpack, inventoryName, identifier, slot) -> {
			BackpackContext.Item backpackContext = new BackpackContext.Item(inventoryName, identifier, slot);
			player.openMenu(MenuProviderHelper.createMenuProvider((w, p, pl) -> new BackpackContainer(w, pl, backpackContext), backpackContext::toBuffer, backpack.getHoverName()));
			return true;
		});
	}

	private void openBackpack(Player player, BackpackContext backpackContext) {
		player.openMenu(MenuProviderHelper.createMenuProvider((w, p, pl) -> new BackpackContainer(w, pl, backpackContext), backpackContext::toBuffer, backpackContext.getDisplayName(player)));
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeInt(slotIndex);
		buffer.writeUtf(identifier);
		buffer.writeUtf(handlerName);
	}

	@Override
	public PacketType<?> getType() {
		return TYPE;
	}
}
