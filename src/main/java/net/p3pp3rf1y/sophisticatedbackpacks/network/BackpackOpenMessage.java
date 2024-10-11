package net.p3pp3rf1y.sophisticatedbackpacks.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.InventoryMenu;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.BackpackContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.BackpackContext;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.IContextAwareContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.util.PlayerInventoryProvider;
import net.p3pp3rf1y.sophisticatedcore.network.SimplePacketBase;
import net.p3pp3rf1y.sophisticatedcore.util.MenuProviderHelper;

public class BackpackOpenMessage extends SimplePacketBase {
	private static final int CHEST_SLOT = 38;
	private static final int OFFHAND_SLOT = 40;
	private final int slotIndex;
	private final String identifier;
	private final String handlerName;

	public BackpackOpenMessage() {
		this(-1);
	}

	public BackpackOpenMessage(int backpackSlot) {
		this(backpackSlot, "");
	}

	public BackpackOpenMessage(int backpackSlot, String identifier, String handlerName) {
		this.slotIndex = backpackSlot;
		this.identifier = identifier;
		this.handlerName = handlerName;
	}

	public BackpackOpenMessage(int backpackSlot, String identifier) {
		this(backpackSlot, identifier, "");
	}

	public BackpackOpenMessage(FriendlyByteBuf buffer) { this(buffer.readInt(), buffer.readUtf(), buffer.readUtf()); }

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeInt(this.slotIndex);
		buffer.writeUtf(this.identifier);
		buffer.writeUtf(this.handlerName);
	}

	@Override
	public boolean handle(Context context) {
		context.enqueueWork(() -> {
			ServerPlayer player = context.getSender();
			if (player == null) {
				return;
			}

			if (!this.handlerName.isEmpty()) {
				int slotIndex1 = this.slotIndex;
				if (this.slotIndex == CHEST_SLOT) {
					slotIndex1 -= 36;
				} else if (this.slotIndex == OFFHAND_SLOT) {
					slotIndex1 = 0;
				}
				BackpackContext.Item backpackContext = new BackpackContext.Item(this.handlerName, this.identifier, slotIndex1,
						player.containerMenu instanceof InventoryMenu || (player.containerMenu instanceof BackpackContainer backpackContainer && backpackContainer.getBackpackContext().wasOpenFromInventory()));
				openBackpack(player, backpackContext);
			} else if (player.containerMenu instanceof BackpackContainer backpackContainer) {
				BackpackContext backpackContext = backpackContainer.getBackpackContext();
				if (this.slotIndex == -1) {
					openBackpack(player, backpackContext.getParentBackpackContext());
				} else if (backpackContainer.isStorageInventorySlot(this.slotIndex)) {
					openBackpack(player, backpackContext.getSubBackpackContext(this.slotIndex));
				}
			} else if (player.containerMenu instanceof IContextAwareContainer contextAwareContainer) {
				BackpackContext backpackContext = contextAwareContainer.getBackpackContext();
				openBackpack(player, backpackContext);
			} else {
				findAndOpenFirstBackpack(player);
			}
		});
		return true;
	}

	private static void findAndOpenFirstBackpack(ServerPlayer player) {
		PlayerInventoryProvider.get().runOnBackpacks(player, (backpack, inventoryName, identifier, slot) -> {
			BackpackContext.Item backpackContext = new BackpackContext.Item(inventoryName, identifier, slot);
			player.openMenu(MenuProviderHelper.createMenuProvider((w, p, pl) -> new BackpackContainer(w, pl, backpackContext), backpackContext, backpack.getHoverName()));
			return true;
		});
	}

	private static void openBackpack(ServerPlayer player, BackpackContext backpackContext) {
		player.openMenu(MenuProviderHelper.createMenuProvider((w, p, pl) -> new BackpackContainer(w, pl, backpackContext), backpackContext, backpackContext.getDisplayName(player)));
	}
}
