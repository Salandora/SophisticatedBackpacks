package net.p3pp3rf1y.sophisticatedbackpacks.client;

import com.mojang.blaze3d.platform.InputConstants;
import committee.nova.mkb.api.IKeyBinding;
import committee.nova.mkb.api.IKeyConflictContext;
import committee.nova.mkb.keybinding.KeyConflictContext;
import committee.nova.mkb.keybinding.KeyModifier;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.BackpackScreen;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.SBPTranslationHelper;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.BackpackContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.compat.CompatModIds;
import net.p3pp3rf1y.sophisticatedbackpacks.compat.trinkets.TrinketsCompat;
import net.p3pp3rf1y.sophisticatedbackpacks.network.BackpackOpenMessage;
import net.p3pp3rf1y.sophisticatedbackpacks.network.BlockToolSwapMessage;
import net.p3pp3rf1y.sophisticatedbackpacks.network.EntityToolSwapMessage;
import net.p3pp3rf1y.sophisticatedbackpacks.network.InventoryInteractionMessage;
import net.p3pp3rf1y.sophisticatedbackpacks.network.SBPPacketHandler;
import net.p3pp3rf1y.sophisticatedbackpacks.network.UpgradeToggleMessage;
import net.p3pp3rf1y.sophisticatedbackpacks.util.PlayerInventoryProvider;
import net.p3pp3rf1y.sophisticatedcore.event.client.ClientRawInputEvent;
import net.p3pp3rf1y.sophisticatedcore.mixin.client.accessor.AbstractContainerScreenAccessor;

import java.util.Map;
import java.util.Optional;

import static committee.nova.mkb.keybinding.KeyConflictContext.GUI;

public class KeybindHandler {
	private KeybindHandler() {}

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
		ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
			ScreenKeyboardEvents.allowKeyPress(screen).register(KeybindHandler::handleGuiKeyPress);
			ScreenMouseEvents.allowMouseClick(screen).register(KeybindHandler::handleGuiMouseKeyPress);
		});

		ClientRawInputEvent.KEY_PRESSED.register(KeybindHandler::handleKeyInputEvent);
	}

	public static void registerKeyMappings() {
		((IKeyBinding)TOOL_SWAP_KEYBIND).setKeyConflictContext(KeyConflictContext.IN_GAME);
		((IKeyBinding)INVENTORY_INTERACTION_KEYBIND).setKeyConflictContext(KeyConflictContext.IN_GAME);
		((IKeyBinding)BACKPACK_OPEN_KEYBIND).setKeyConflictContext(BackpackKeyConflictContext.INSTANCE);
		((IKeyBinding)SORT_KEYBIND).setKeyConflictContext(BackpackGuiKeyConflictContext.INSTANCE);

		KeyBindingHelper.registerKeyBinding(BACKPACK_OPEN_KEYBIND);
		KeyBindingHelper.registerKeyBinding(INVENTORY_INTERACTION_KEYBIND);
		KeyBindingHelper.registerKeyBinding(TOOL_SWAP_KEYBIND);
		KeyBindingHelper.registerKeyBinding(SORT_KEYBIND);

		((IKeyBinding)BACKPACK_TOGGLE_UPGRADE_1).setKeyModifierAndCode(KeyModifier.ALT, InputConstants.Type.KEYSYM.getOrCreate(KEY_Z));
		((IKeyBinding)BACKPACK_TOGGLE_UPGRADE_2).setKeyModifierAndCode(KeyModifier.ALT, InputConstants.Type.KEYSYM.getOrCreate(KEY_Z));

		UPGRADE_SLOT_TOGGLE_KEYBINDS.forEach((slot, keybind) -> {
			((IKeyBinding)keybind).setKeyConflictContext(KeyConflictContext.UNIVERSAL);
			KeyBindingHelper.registerKeyBinding(keybind);
		});
	}

	public static boolean handleGuiKeyPress(Screen screen, int key, int scancode, int modifiers) {
		InputConstants.Key input = InputConstants.getKey(key, scancode);
		if (((IKeyBinding) SORT_KEYBIND).isActiveAndMatches(input) && tryCallSort(screen) || ((IKeyBinding) BACKPACK_OPEN_KEYBIND).isActiveAndMatches(input) && sendBackpackOpenOrCloseMessage()) {
			return false;
		}
		return true;
	}

	public static boolean handleGuiMouseKeyPress(Screen screen, double mouseX, double mouseY, int button) {
		InputConstants.Key input = InputConstants.Type.MOUSE.getOrCreate(button);
		if (((IKeyBinding)SORT_KEYBIND).isActiveAndMatches(input) && tryCallSort(screen) || ((IKeyBinding)BACKPACK_OPEN_KEYBIND).isActiveAndMatches(input) && sendBackpackOpenOrCloseMessage()) {
			return false;
		}

        return true;
	}

	public static InteractionResult handleKeyInputEvent(Minecraft minecraft, int key, int scancode, int action, int mods) {
		if (BACKPACK_OPEN_KEYBIND.consumeClick()) {
			sendBackpackOpenOrCloseMessage();
			return InteractionResult.SUCCESS;
		} else if (INVENTORY_INTERACTION_KEYBIND.consumeClick()) {
			sendInteractWithInventoryMessage();
			return InteractionResult.SUCCESS;
		} else if (TOOL_SWAP_KEYBIND.consumeClick()) {
			sendToolSwapMessage();
			return InteractionResult.SUCCESS;
		} else {
			boolean success = false;
			for (Map.Entry<Integer, KeyMapping> slotKeybind : UPGRADE_SLOT_TOGGLE_KEYBINDS.entrySet()) {
				if (slotKeybind.getValue().consumeClick()) {
					SBPPacketHandler.sendToServer(new UpgradeToggleMessage(slotKeybind.getKey()));
					success = true;
				}
			}
			if (success) {
				return InteractionResult.SUCCESS;
			}
		}

		return InteractionResult.PASS;
	}

	private static boolean tryCallSort(Screen gui) {
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
			SBPPacketHandler.sendToServer(new BlockToolSwapMessage(pos));
		} else if (rayTrace.getType() == HitResult.Type.ENTITY) {
			EntityHitResult entityRayTraceResult = (EntityHitResult) rayTrace;
			SBPPacketHandler.sendToServer(new EntityToolSwapMessage(entityRayTraceResult.getEntity().getId()));
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

		if (ItemStorage.SIDED.find(mc.level, pos, null) == null) {
			return;
		}

		SBPPacketHandler.sendToServer(new InventoryInteractionMessage(pos, blockraytraceresult.getDirection()));
	}

	private static boolean sendBackpackOpenOrCloseMessage() {
		if (!GUI.isActive()) {
			SBPPacketHandler.sendToServer(new BackpackOpenMessage());
			return false;
		}

		Screen screen = Minecraft.getInstance().screen;
		if (screen instanceof AbstractContainerScreen<?> containerScreen) {
			Slot slot = ((AbstractContainerScreenAccessor) containerScreen).getHoveredSlot();
			if (slot != null && (slot.container instanceof Inventory || isTrinket(slot.container))) {
				Optional<PlayerInventoryReturn> ret = getPlayerInventory(slot);

				if (ret.isPresent() && slot.getItem().getItem() instanceof BackpackItem) {
					SBPPacketHandler.sendToServer(new BackpackOpenMessage(slot.getContainerSlot(), ret.get().identifier(), ret.get().handlerName()));
					return true;
				}
			}
			if (screen instanceof BackpackScreen && slot != null && slot.getItem().getItem() instanceof BackpackItem && slot.getItem().getCount() == 1) {
				SBPPacketHandler.sendToServer(new BackpackOpenMessage(slot.getContainerSlot()));
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

	private static class BackpackKeyConflictContext implements IKeyConflictContext {
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

	private static class BackpackGuiKeyConflictContext implements IKeyConflictContext {
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
}
