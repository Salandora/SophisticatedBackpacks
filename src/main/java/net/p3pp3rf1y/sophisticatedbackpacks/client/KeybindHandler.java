package net.p3pp3rf1y.sophisticatedbackpacks.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.BackpackScreen;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.SBPTranslationHelper;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.BackpackContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.compat.CompatModIds;
import net.p3pp3rf1y.sophisticatedbackpacks.compat.trinkets.TrinketsCompat;
import net.p3pp3rf1y.sophisticatedbackpacks.network.*;
import net.p3pp3rf1y.sophisticatedbackpacks.util.PlayerInventoryProvider;
import net.p3pp3rf1y.sophisticatedcore.mixin.client.accessor.AbstractContainerScreenAccessor;
import net.p3pp3rf1y.sophisticatedcore.network.PacketHelper;
import net.p3pp3rf1y.sophisticatedcore.util.CapabilityHelper;

import java.util.Map;
import java.util.Optional;

public class KeybindHandler {
	private KeybindHandler() {
	}

	private static final int KEY_B = 66;
	private static final int KEY_C = 67;
	private static final int KEY_Z = 90;
	private static final int KEY_X = 88;
	private static final int KEY_UNKNOWN = -1;
	private static final int MIDDLE_BUTTON = 2;
	private static final int CHEST_SLOT_INDEX = 38;
	private static final int OFFHAND_SLOT_INDEX = 40;
	private static final String KEYBIND_SOPHISTICATEDBACKPACKS_CATEGORY = "keybind.sophisticatedbackpacks.category";
	public static final KeyMapping BACKPACK_TOGGLE_UPGRADE_5 = new KeyMapping(SBPTranslationHelper.INSTANCE.translKeybind("toggle_upgrade_5"),
			InputConstants.Type.KEYSYM.getOrCreate(KEY_UNKNOWN).getValue(), KEYBIND_SOPHISTICATEDBACKPACKS_CATEGORY);
	public static final KeyMapping BACKPACK_TOGGLE_UPGRADE_4 = new KeyMapping(SBPTranslationHelper.INSTANCE.translKeybind("toggle_upgrade_4"),
		    InputConstants.Type.KEYSYM.getOrCreate(KEY_UNKNOWN).getValue(), KEYBIND_SOPHISTICATEDBACKPACKS_CATEGORY);
	public static final KeyMapping BACKPACK_TOGGLE_UPGRADE_3 = new KeyMapping(SBPTranslationHelper.INSTANCE.translKeybind("toggle_upgrade_3"),
		    InputConstants.Type.KEYSYM.getOrCreate(KEY_UNKNOWN).getValue(), KEYBIND_SOPHISTICATEDBACKPACKS_CATEGORY);
	public static final KeyMapping BACKPACK_TOGGLE_UPGRADE_2 = new KeyMapping(SBPTranslationHelper.INSTANCE.translKeybind("toggle_upgrade_2"),
		    InputConstants.Type.KEYSYM.getOrCreate(KEY_X).getValue(), KEYBIND_SOPHISTICATEDBACKPACKS_CATEGORY);
	public static final KeyMapping BACKPACK_TOGGLE_UPGRADE_1 = new KeyMapping(SBPTranslationHelper.INSTANCE.translKeybind("toggle_upgrade_1"),
		    InputConstants.Type.KEYSYM.getOrCreate(KEY_Z).getValue(), KEYBIND_SOPHISTICATEDBACKPACKS_CATEGORY);

	public static final Map<Integer, KeyMapping> UPGRADE_SLOT_TOGGLE_KEYBINDS = Map.of(
			0, BACKPACK_TOGGLE_UPGRADE_1,
			1, BACKPACK_TOGGLE_UPGRADE_2,
			2, BACKPACK_TOGGLE_UPGRADE_3,
			3, BACKPACK_TOGGLE_UPGRADE_4,
			4, BACKPACK_TOGGLE_UPGRADE_5
	);
	public static final KeyMapping SORT_KEYBIND = new KeyMapping(SBPTranslationHelper.INSTANCE.translKeybind("sort"),
            InputConstants.Type.MOUSE, MIDDLE_BUTTON, KEYBIND_SOPHISTICATEDBACKPACKS_CATEGORY); // BackpackGuiKeyConflictContext.INSTANCE
	public static final KeyMapping TOOL_SWAP_KEYBIND = new KeyMapping(SBPTranslationHelper.INSTANCE.translKeybind("tool_swap"),
		    InputConstants.Type.KEYSYM.getOrCreate(KEY_UNKNOWN).getValue(), KEYBIND_SOPHISTICATEDBACKPACKS_CATEGORY);
	public static final KeyMapping INVENTORY_INTERACTION_KEYBIND = new KeyMapping(SBPTranslationHelper.INSTANCE.translKeybind("inventory_interaction"),
			InputConstants.Type.KEYSYM.getOrCreate(KEY_C).getValue(), KEYBIND_SOPHISTICATEDBACKPACKS_CATEGORY);
	public static final KeyMapping BACKPACK_OPEN_KEYBIND = new KeyMapping(SBPTranslationHelper.INSTANCE.translKeybind("open_backpack"),
			InputConstants.Type.KEYSYM.getOrCreate(KEY_B).getValue(), KEYBIND_SOPHISTICATEDBACKPACKS_CATEGORY); // BackpackKeyConflictContext.INSTANCE

	public static void register() {
		if (!FabricLoader.getInstance().isModLoaded(CompatModIds.MKB)) {
			ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
				ScreenKeyboardEvents.allowKeyPress(screen).register(KeybindHandler::handleGuiKeyPress);
				ScreenMouseEvents.allowMouseClick(screen).register(KeybindHandler::handleGuiMouseKeyPress);
			});
		}

		ClientTickEvents.END_CLIENT_TICK.register(KeybindHandler::handleKeyInputEvent);
	}

	public static void registerKeyMappings() {
		KeyBindingHelper.registerKeyBinding(BACKPACK_OPEN_KEYBIND);
		KeyBindingHelper.registerKeyBinding(INVENTORY_INTERACTION_KEYBIND);
		KeyBindingHelper.registerKeyBinding(TOOL_SWAP_KEYBIND);
		KeyBindingHelper.registerKeyBinding(SORT_KEYBIND);
		UPGRADE_SLOT_TOGGLE_KEYBINDS.forEach((slot, keybind) -> KeyBindingHelper.registerKeyBinding(keybind));
	}

	public static boolean handleGuiKeyPress(Screen screen, int key, int scancode, int modifiers) {
		if (SORT_KEYBIND.matches(key, scancode) && tryCallSort(screen)) {
			return false;
		} else if (BACKPACK_OPEN_KEYBIND.matches(key, scancode) && sendBackpackOpenOrCloseMessage()) {
			return false;
		}
		return true;
	}

	public static boolean handleGuiMouseKeyPress(Screen screen, double mouseX, double mouseY, int button) {
		if (SORT_KEYBIND.matchesMouse(button) && tryCallSort(screen)) {
			return false;
		} else if (BACKPACK_OPEN_KEYBIND.matchesMouse(button) && sendBackpackOpenOrCloseMessage()) {
			return false;
		}
        return true;
	}

	private static void handleKeyInputEvent(Minecraft minecraft) {
		if (BACKPACK_OPEN_KEYBIND.consumeClick()) {
			sendBackpackOpenOrCloseMessage();
		} else if (INVENTORY_INTERACTION_KEYBIND.consumeClick()) {
			sendInteractWithInventoryMessage();
		} else if (TOOL_SWAP_KEYBIND.consumeClick()) {
			sendToolSwapMessage();
		} else {
			for (Map.Entry<Integer, KeyMapping> slotKeybind : UPGRADE_SLOT_TOGGLE_KEYBINDS.entrySet()) {
				if (slotKeybind.getValue().consumeClick()) {
					PacketHelper.sendToServer(new UpgradeTogglePacket(slotKeybind.getKey()));
				}
			}
		}
	}

	public static boolean tryCallSort(Screen gui) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player != null && mc.player.containerMenu instanceof BackpackContainer container && gui instanceof BackpackScreen screen) {
			MouseHandler mh = mc.mouseHandler;
			double mouseX = mh.xpos() * mc.getWindow().getGuiScaledWidth() / mc.getWindow().getScreenWidth();
			double mouseY = mh.ypos() * mc.getWindow().getGuiScaledHeight() / mc.getWindow().getScreenHeight();
			Slot selectedSlot = screen.findSlot(mouseX, mouseY);
			if (selectedSlot != null && container.isNotPlayersInventorySlot(selectedSlot.index)) {
				container.sort();
				return true;
			}
		}
		return false;
	}

	private static void sendToolSwapMessage() {
		Minecraft mc = Minecraft.getInstance();
		LocalPlayer player = mc.player;
		if (player == null || mc.hitResult == null) {
			return;
		}
		if (player.getMainHandItem().getItem() instanceof BackpackItem) {
			player.displayClientMessage(Component.translatable("gui.sophisticatedbackpacks.status.unable_to_swap_tool_for_backpack"), true);
			return;
		}
		HitResult rayTrace = mc.hitResult;
		if (rayTrace.getType() == HitResult.Type.BLOCK) {
			BlockHitResult blockRayTraceResult = (BlockHitResult) rayTrace;
			BlockPos pos = blockRayTraceResult.getBlockPos();
			PacketHelper.sendToServer(new BlockToolSwapPacket(pos));
		} else if (rayTrace.getType() == HitResult.Type.ENTITY) {
			EntityHitResult entityRayTraceResult = (EntityHitResult) rayTrace;
			PacketHelper.sendToServer(new EntityToolSwapPacket(entityRayTraceResult.getEntity().getId()));
		}
	}

	private static void sendInteractWithInventoryMessage() {
		Minecraft mc = Minecraft.getInstance();
		HitResult rayTrace = mc.hitResult;
		if (rayTrace == null || rayTrace.getType() != HitResult.Type.BLOCK) {
			return;
		}
		BlockHitResult blockraytraceresult = (BlockHitResult) rayTrace;
		BlockPos pos = blockraytraceresult.getBlockPos();

		if (Boolean.FALSE.equals(CapabilityHelper.getFromItemHandler(mc.level, pos, itemHandler -> true, false))) {
			return;
		}

		PacketHelper.sendToServer(new InventoryInteractionPacket(pos, blockraytraceresult.getDirection()));
	}

	public static boolean sendBackpackOpenOrCloseMessage() {
		if (Minecraft.getInstance().screen == null) {
			PacketHelper.sendToServer(new BackpackOpenPacket());
			return false;
		}

		Screen screen = Minecraft.getInstance().screen;
		if (screen instanceof AbstractContainerScreen<?> containerScreen) {
			Slot slot = ((AbstractContainerScreenAccessor) containerScreen).getHoveredSlot();

			if (slot != null && (slot.container instanceof Inventory || isTrinket(slot.container))) {
				Optional<PlayerInventoryReturn> handler = getPlayerInventory(slot);

				if (handler.isPresent() && slot.getItem().getItem() instanceof BackpackItem) {
					PacketHelper.sendToServer(new BackpackOpenPacket(slot.getContainerSlot(), handler.get().identifier(), handler.get().handlerName()));
					return true;
				}
			}
			if (screen instanceof BackpackScreen && slot != null && slot.getItem().getItem() instanceof BackpackItem && slot.getItem().getCount() == 1) {
				PacketHelper.sendToServer(new BackpackOpenPacket(slot.getContainerSlot()));
				return true;
			}
		}
		return false;
	}

	private static boolean isTrinket(Container container) {
		return FabricLoader.getInstance().isModLoaded(CompatModIds.TRINKETS) && TrinketsCompat.isTrinketContainer(container);
	}

	record PlayerInventoryReturn(String identifier, String handlerName) {}

	private static Optional<PlayerInventoryReturn> getPlayerInventory(Slot slot) {
		int slotIndex = slot.getContainerSlot();
		if (slotIndex == CHEST_SLOT_INDEX) {
			return Optional.of(new PlayerInventoryReturn("", PlayerInventoryProvider.ARMOR_INVENTORY));
		} else if (slotIndex == OFFHAND_SLOT_INDEX) {
			return Optional.of(new PlayerInventoryReturn("", PlayerInventoryProvider.OFFHAND_INVENTORY));
		} else if (isTrinket(slot.container)) {
			return Optional.of(new PlayerInventoryReturn(TrinketsCompat.getIdentifierForSlot(slot.container), CompatModIds.TRINKETS));
		} else if (slotIndex >= 0 && slotIndex < 36) {
			return Optional.of(new PlayerInventoryReturn("", PlayerInventoryProvider.MAIN_INVENTORY));
		}

		return Optional.empty();
	}
}
