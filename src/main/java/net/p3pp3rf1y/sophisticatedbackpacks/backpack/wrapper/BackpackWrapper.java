package net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper;

import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.api.IEnergyStorageUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.api.IFluidHandlerWrapperUpgrade;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackStorage;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModDataComponents;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems;
import net.p3pp3rf1y.sophisticatedcore.SophisticatedCore;
import net.p3pp3rf1y.sophisticatedcore.api.IStorageFluidHandler;
import net.p3pp3rf1y.sophisticatedcore.api.IStorageWrapper;
import net.p3pp3rf1y.sophisticatedcore.common.gui.SortBy;
import net.p3pp3rf1y.sophisticatedcore.init.ModCoreDataComponents;
import net.p3pp3rf1y.sophisticatedcore.inventory.*;
import net.p3pp3rf1y.sophisticatedcore.settings.itemdisplay.ItemDisplaySettingsCategory;
import net.p3pp3rf1y.sophisticatedcore.settings.memory.MemorySettingsCategory;
import net.p3pp3rf1y.sophisticatedcore.settings.nosort.NoSortSettingsCategory;
import net.p3pp3rf1y.sophisticatedcore.upgrades.UpgradeHandler;
import net.p3pp3rf1y.sophisticatedcore.upgrades.stack.StackUpgradeItem;
import net.p3pp3rf1y.sophisticatedcore.upgrades.tank.TankUpgradeItem;
import net.p3pp3rf1y.sophisticatedcore.util.InventoryHelper;
import net.p3pp3rf1y.sophisticatedcore.util.InventorySorter;
import net.p3pp3rf1y.sophisticatedcore.util.LootHelper;
import net.p3pp3rf1y.sophisticatedcore.util.RandHelper;
import team.reborn.energy.api.EnergyStorage;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.IntConsumer;

public class BackpackWrapper implements IBackpackWrapper {
	public static final int DEFAULT_MAIN_COLOR = 0xFF_CC613A;
	public static final int DEFAULT_ACCENT_COLOR = 0xFF_622E1A;

	@Nullable
	private ItemStack backpack;
	private Runnable backpackSaveHandler = () -> {
	};
	private Runnable inventorySlotChangeHandler = () -> {
	};

	@Nullable
	private InventoryHandler handler = null;
	@Nullable
	private UpgradeHandler upgradeHandler = null;
	@Nullable
	private InventoryIOHandler inventoryIOHandler = null;
	@Nullable
	private InventoryModificationHandler inventoryModificationHandler = null;
	@Nullable
	private BackpackSettingsHandler settingsHandler = null;
	private boolean fluidHandlerInitialized = false;
	@Nullable
	private IStorageFluidHandler fluidHandler = null;
	private boolean energyStorageInitialized = false;
	@Nullable
	private EnergyStorage energyStorage = null;

	@Nullable
	private BackpackRenderInfo renderInfo;

	private IntConsumer onSlotsChange = diff -> {
	};

	private Runnable onInventoryHandlerRefresh = () -> {
	};
	private Runnable upgradeCachesInvalidatedHandler = () -> {
	};

	public BackpackWrapper(ItemStack backpackStack) {
		setBackpackStack(backpackStack);
	}

	public static IBackpackWrapper fromStack(ItemStack stack) {
		return StorageWrapperRepository.getStorageWrapper(stack, IBackpackWrapper.class, BackpackWrapper::new);
		/* TODO try to add uuid based caching in the future
		UUID uuid = stack.get(ModCoreDataComponents.STORAGE_UUID);
		if (uuid == null) {
			return StorageWrapperRepository.getStorageWrapper(stack, IBackpackWrapper.class, BackpackWrapper::new);
		} else {
			return StorageWrapperRepository.getStorageWrapper(uuid, IBackpackWrapper.class, BackpackWrapper::new);
		}
*/
	}

	public static Optional<IBackpackWrapper> fromExistingData(ItemStack stack) {
		if (stack.getItem() instanceof BackpackItem) {
			return StorageWrapperRepository.getExistingStorageWrapper(stack, IBackpackWrapper.class);
		}

		return Optional.empty();
	}

	@Override
	public void setContentsChangeHandler(Runnable contentsChangeHandler) {
		backpackSaveHandler = contentsChangeHandler;
		refreshInventoryForUpgradeProcessing();
	}

	@Override
	public void setInventorySlotChangeHandler(Runnable slotChangeHandler) {
		inventorySlotChangeHandler = slotChangeHandler;
	}

	@Override
	public ITrackedContentsItemHandler getInventoryForUpgradeProcessing() {
		if (inventoryModificationHandler == null) {
			inventoryModificationHandler = new InventoryModificationHandler(this);
		}
		return inventoryModificationHandler.getModifiedInventoryHandler();
	}

	@Override
	public InventoryHandler getInventoryHandler() {
		if (handler == null) {
			handler = new BackpackInventoryHandler(getNumberOfInventorySlots() - (getNumberOfSlotRows() * getColumnsTaken()),
					this, getBackpackContentsNbt(), () -> {
				markBackpackContentsDirty();
				inventorySlotChangeHandler.run();
			}, StackUpgradeItem.getInventorySlotLimit(this));
			handler.addListener(getSettingsHandler().getTypeCategory(ItemDisplaySettingsCategory.class)::itemChanged);
		}
		return handler;
	}

	private int getNumberOfInventorySlots() {
		Integer inventorySlots = getBackpackStack().sophisticatedCore_get(ModCoreDataComponents.NUMBER_OF_INVENTORY_SLOTS);

		if (inventorySlots != null) {
			return inventorySlots;
		}

		int itemInventorySlots = ((BackpackItem) getBackpackStack().getItem()).getNumberOfSlots();
		setNumberOfInventorySlots(itemInventorySlots);
		return itemInventorySlots;
	}

	@Override
	public int getNumberOfSlotRows() {
		int itemInventorySlots = getNumberOfInventorySlots();
		return (int) Math.ceil(itemInventorySlots <= 81 ? (double) itemInventorySlots / 9 : (double) itemInventorySlots / 12);
	}

	private void setNumberOfInventorySlots(int itemInventorySlots) {
		getBackpackStack().sophisticatedCore_set(ModCoreDataComponents.NUMBER_OF_INVENTORY_SLOTS, itemInventorySlots);
	}

	private CompoundTag getBackpackContentsNbt() {
		return BackpackStorage.get().getOrCreateBackpackContents(getOrCreateContentsUuid());
	}

	private void markBackpackContentsDirty() {
		BackpackStorage.get().setDirty();
	}

	@Override
	public ITrackedContentsItemHandler getInventoryForInputOutput() {
		if (inventoryIOHandler == null) {
			inventoryIOHandler = new InventoryIOHandler(this);
		}
		return inventoryIOHandler.getFilteredItemHandler();
	}

	@Override
	public Optional<IStorageFluidHandler> getFluidHandler() {
		if (!fluidHandlerInitialized) {
			IStorageFluidHandler wrappedHandler = getUpgradeHandler().getTypeWrappers(TankUpgradeItem.TYPE).isEmpty() ? null : new BackpackFluidHandler(this);
			List<IFluidHandlerWrapperUpgrade> fluidHandlerWrapperUpgrades = getUpgradeHandler().getWrappersThatImplement(IFluidHandlerWrapperUpgrade.class);

			for (IFluidHandlerWrapperUpgrade fluidHandlerWrapperUpgrade : fluidHandlerWrapperUpgrades) {
				wrappedHandler = fluidHandlerWrapperUpgrade.wrapHandler(wrappedHandler, getBackpackStack());
			}

			fluidHandler = wrappedHandler;
		}

		return Optional.ofNullable(fluidHandler);
	}

	@Override
	public Optional<IStorageFluidHandler> getItemFluidHandler() {
		return getFluidHandler().map(fh -> new FluidHandlerItemWrapper(getBackpackStack(), fh));
	}

	@Override
	public Optional<EnergyStorage> getEnergyStorage() {
		if (!energyStorageInitialized) {
			EnergyStorage wrappedStorage = getUpgradeHandler().getWrappersThatImplement(EnergyStorage.class).stream().findFirst().orElse(null);

			for (IEnergyStorageUpgradeWrapper energyStorageWrapperUpgrade : getUpgradeHandler().getWrappersThatImplement(IEnergyStorageUpgradeWrapper.class)) {
				wrappedStorage = energyStorageWrapperUpgrade.wrapStorage(wrappedStorage);
			}

			energyStorage = wrappedStorage;
		}

		return energyStorage == null || energyStorage.getCapacity() == 0 ? Optional.empty() : Optional.of(energyStorage);
	}

	@Override
	public void copyDataTo(IStorageWrapper otherStorageWrapper) {
		getContentsUuid().ifPresent(originalUuid -> {
			getInventoryHandler().copyStacksTo(otherStorageWrapper.getInventoryHandler());
			getUpgradeHandler().copyTo(otherStorageWrapper.getUpgradeHandler());
			getSettingsHandler().copyTo(otherStorageWrapper.getSettingsHandler());
		});
	}

	@Override
	public IBackpackWrapper setBackpackStack(ItemStack backpack) {
		this.backpack = backpack;
		if (renderInfo == null) {
			renderInfo = new BackpackRenderInfo(backpack, () -> backpackSaveHandler);
		}
		return this;
	}

	@Override
	public BackpackSettingsHandler getSettingsHandler() {
		if (settingsHandler == null) {
			if (getContentsUuid().isPresent()) {
				settingsHandler = new BackpackSettingsHandler(this, getBackpackContentsNbt(), this::markBackpackContentsDirty);
			} else {
				settingsHandler = Noop.INSTANCE.getSettingsHandler();
			}
		}
		return settingsHandler;
	}

	@Override
	public UpgradeHandler getUpgradeHandler() {
		if (upgradeHandler == null) {
			if (getContentsUuid().isPresent()) {
				upgradeHandler = new UpgradeHandler(getNumberOfUpgradeSlots(), this, getBackpackContentsNbt(), this::markBackpackContentsDirty, () -> {
					if (handler != null) {
						handler.clearListeners();
						handler.setBaseSlotLimit(StackUpgradeItem.getInventorySlotLimit(this));
					}
					getInventoryHandler().clearListeners();
					handler.addListener(getSettingsHandler().getTypeCategory(ItemDisplaySettingsCategory.class)::itemChanged);
					inventoryIOHandler = null;
					inventoryModificationHandler = null;
					fluidHandlerInitialized = false;
					fluidHandler = null;
					energyStorageInitialized = false;
					energyStorage = null;
					upgradeCachesInvalidatedHandler.run();
				}) {
					@Override
					public boolean isItemValid(int slot, ItemStack stack) {
						return super.isItemValid(slot, stack) && (stack.isEmpty() || stack.is(ModItems.BACKPACK_UPGRADE_TAG));
					}
				};
			} else {
				upgradeHandler = Noop.INSTANCE.getUpgradeHandler();
			}
		}
		return upgradeHandler;
	}

	@Override
	public void setUpgradeCachesInvalidatedHandler(Runnable handler) {
		upgradeCachesInvalidatedHandler = handler;
	}

	private int getNumberOfUpgradeSlots() {
		Integer upgradeSlots = getBackpackStack().sophisticatedCore_get(ModCoreDataComponents.NUMBER_OF_UPGRADE_SLOTS);

		if (upgradeSlots != null) {
			return upgradeSlots;
		}

		int itemUpgradeSlots = ((BackpackItem) getBackpackStack().getItem()).getNumberOfUpgradeSlots();
		setNumberOfUpgradeSlots(itemUpgradeSlots);
		return itemUpgradeSlots;
	}

	@Override
	public Optional<UUID> getContentsUuid() {
		return Optional.ofNullable(getBackpackStack().sophisticatedCore_get(ModCoreDataComponents.STORAGE_UUID));
	}

	private UUID getOrCreateContentsUuid() {
		Optional<UUID> contentsUuid = getContentsUuid();
		if (contentsUuid.isPresent()) {
			return contentsUuid.get();
		}
		clearDummyHandlers();
		UUID newUuid = UUID.randomUUID();
		setContentsUuid(newUuid);
		return newUuid;
	}

	private void clearDummyHandlers() {
		if (upgradeHandler == Noop.INSTANCE.getUpgradeHandler()) {
			upgradeHandler = null;
		}
		if (settingsHandler == Noop.INSTANCE.getSettingsHandler()) {
			settingsHandler = null;
		}
	}

	@Override
	public int getMainColor() {
		return getBackpackStack().sophisticatedCore_getOrDefault(ModCoreDataComponents.MAIN_COLOR, DEFAULT_MAIN_COLOR);
	}

	@Override
	public int getAccentColor() {
		return getBackpackStack().sophisticatedCore_getOrDefault(ModCoreDataComponents.ACCENT_COLOR, DEFAULT_ACCENT_COLOR);
	}

	@Override
	public Optional<Integer> getOpenTabId() {
		return Optional.ofNullable(getBackpackStack().sophisticatedCore_get(ModCoreDataComponents.OPEN_TAB_ID));
	}

	@Override
	public void setOpenTabId(int openTabId) {
		getBackpackStack().sophisticatedCore_set(ModCoreDataComponents.OPEN_TAB_ID, openTabId);
		backpackSaveHandler.run();
	}

	@Override
	public void removeOpenTabId() {
		getBackpackStack().sophisticatedCore_remove(ModCoreDataComponents.OPEN_TAB_ID);
		backpackSaveHandler.run();
	}

	@Override
	public void setColors(int mainColor, int accentColor) {
		ItemStack backpackStack = getBackpackStack();
		BackpackItem.setColors(backpackStack, mainColor, accentColor);
		backpackSaveHandler.run();
	}

	@Override
	public void setSortBy(SortBy sortBy) {
		getBackpackStack().sophisticatedCore_set(ModCoreDataComponents.SORT_BY, sortBy);
		backpackSaveHandler.run();
	}

	@Override
	public SortBy getSortBy() {
		return getBackpackStack().sophisticatedCore_getOrDefault(ModCoreDataComponents.SORT_BY, SortBy.NAME);
	}

	@Override
	public void sort() {
		Set<Integer> slotIndexesExcludedFromSort = new HashSet<>();
		slotIndexesExcludedFromSort.addAll(getSettingsHandler().getTypeCategory(NoSortSettingsCategory.class).getNoSortSlots());
		slotIndexesExcludedFromSort.addAll(getSettingsHandler().getTypeCategory(MemorySettingsCategory.class).getSlotIndexes());
		InventorySorter.sortHandler(getInventoryHandler(), getComparator(), slotIndexesExcludedFromSort);
	}

	private Comparator<Map.Entry<ItemStackKey, Integer>> getComparator() {
		return switch (getSortBy()) {
			case COUNT -> InventorySorter.BY_COUNT;
			case TAGS -> InventorySorter.BY_TAGS;
			case NAME -> InventorySorter.BY_NAME;
			case MOD -> InventorySorter.BY_MOD;
		};
	}

	public ItemStack getBackpack() {
		return getBackpackStack();
	}

	@Override
	public ItemStack cloneBackpack() {
		ItemStack clonedBackpack = cloneBackpack(this);
		cloneSubbackpacks(BackpackWrapper.fromStack(clonedBackpack));
		return clonedBackpack;
	}

	private void cloneSubbackpacks(IStorageWrapper wrapperCloned) {
		InventoryHandler inventoryHandler = wrapperCloned.getInventoryHandler();
		InventoryHelper.iterate(inventoryHandler, (slot, stack) -> {
			if (!(stack.getItem() instanceof BackpackItem)) {
				return;
			}
			inventoryHandler.setStackInSlot(slot, cloneBackpack(BackpackWrapper.fromStack(stack)));
		});
	}

	private ItemStack cloneBackpack(IBackpackWrapper originalWrapper) {
		ItemStack backpackCopy = originalWrapper.getBackpack().copy();
		backpackCopy.sophisticatedCore_remove(ModCoreDataComponents.STORAGE_UUID);
		IBackpackWrapper wrapperCopy = BackpackWrapper.fromStack(backpackCopy);
		originalWrapper.copyDataTo(wrapperCopy);
		return wrapperCopy.getBackpack();
	}

	@Override
	public void refreshInventoryForInputOutput() {
		inventoryIOHandler = null;
		upgradeCachesInvalidatedHandler.run();
	}

	@Override
	public void setPersistent(boolean persistent) {
		getInventoryHandler().setPersistent(persistent);
		getUpgradeHandler().setPersistent(persistent);
	}

	@Override
	public void setSlotNumbers(int numberOfInventorySlots, int numberOfUpgradeSlots) {
		setNumberOfInventorySlots(numberOfInventorySlots);
		setNumberOfUpgradeSlots(numberOfUpgradeSlots);
	}

	@Override
	public void setLoot(ResourceLocation lootTableName, float lootFactor) {
		getBackpackStack().sophisticatedCore_set(ModDataComponents.LOOT_TABLE, lootTableName);
		getBackpackStack().sophisticatedCore_set(ModDataComponents.LOOT_FACTOR, lootFactor);
		backpackSaveHandler.run();
	}

	@Override
	public void fillWithLoot(Player player) {
		Level level = player.level();
		if (level.isClientSide) {
			return;
		}
		BlockPos pos = player.blockPosition();
		fillWithLoot(level, pos);
		fillWithExtraItems(player);
	}

	private void fillWithExtraItems(Player player) {
		ItemStack backpack = getBackpackStack();
		if (!backpack.has(DataComponents.CONTAINER)) {
			return;
		}

		ItemContainerContents containerItems = backpack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
		for (int slot = 0; slot < containerItems.getSlots(); slot++) {
			ItemStack stack = containerItems.getStackInSlot(slot);
			if (stack.isEmpty()) {
				continue;
			}
			InventoryHelper.insertOrDropItem(player, stack, getInventoryHandler());
		}
		backpack.remove(DataComponents.CONTAINER);
	}

	private void fillWithLoot(Level level, BlockPos pos) {
		ResourceLocation lootTable = getBackpackStack().sophisticatedCore_get(ModDataComponents.LOOT_TABLE);
		if (lootTable == null) {
			return;
		}
		fillWithLootFromTable(level, pos, lootTable);
	}

	@Override
	public void setContentsUuid(UUID storageUuid) {
		getBackpackStack().sophisticatedCore_set(ModCoreDataComponents.STORAGE_UUID, storageUuid);
/* TODO add in the future
		StorageWrapperRepository.migrateToUuid(this, backpack, storageUuid);
*/
	}

	@Override
	public void removeContentsUuid() {
		getContentsUuid().ifPresent(BackpackStorage.get()::removeBackpackContents);
		removeContentsUUIDTag();
	}

	@Override
	public void removeContentsUUIDTag() {
		getBackpackStack().sophisticatedCore_remove(ModCoreDataComponents.STORAGE_UUID);
	}

	private ItemStack getBackpackStack() {
		if (backpack == null) {
			throw new IllegalStateException("Backpack stack not set");
		}
		return backpack;
	}

	@Override
	public BackpackRenderInfo getRenderInfo() {
		return renderInfo;
	}

	@Override
	public void setColumnsTaken(int columnsTaken, boolean hasChanged) {
		int originalColumnsTaken = getColumnsTaken();
		getBackpackStack().sophisticatedCore_set(ModDataComponents.COLUMNS_TAKEN, columnsTaken);
		if (hasChanged) {
			int diff = (columnsTaken - originalColumnsTaken) * getNumberOfSlotRows();
			onSlotsChange.accept(diff);
		}
		backpackSaveHandler.run();
	}

	@Override
	public void registerOnSlotsChangeListener(IntConsumer onSlotsChange) {
		this.onSlotsChange = onSlotsChange;
	}

	@Override
	public void unregisterOnSlotsChangeListener() {
		onSlotsChange = diff -> {
		};
	}

	@Override
	public int getColumnsTaken() {
		return getBackpackStack().sophisticatedCore_getOrDefault(ModDataComponents.COLUMNS_TAKEN, 0);
	}

	private void fillWithLootFromTable(Level level, BlockPos pos, ResourceLocation lootTable) {
		MinecraftServer server = level.getServer();
		if (server == null || !(level instanceof ServerLevel serverLevel)) {
			return;
		}

		float lootFactor = getBackpackStack().sophisticatedCore_getOrDefault(ModDataComponents.LOOT_FACTOR, 0f);

		getBackpackStack().sophisticatedCore_remove(ModDataComponents.LOOT_TABLE);
		getBackpackStack().sophisticatedCore_remove(ModDataComponents.LOOT_FACTOR);

		List<ItemStack> loot = LootHelper.getLoot(lootTable, server, serverLevel, pos);
		loot.removeIf(stack -> stack.getItem() instanceof BackpackItem);
		loot = RandHelper.getNRandomElements(loot, (int) (loot.size() * lootFactor));
		LootHelper.fillWithLoot(serverLevel.random, loot, getInventoryHandler());
	}

	private void setNumberOfUpgradeSlots(int numberOfUpgradeSlots) {
		getBackpackStack().sophisticatedCore_set(ModCoreDataComponents.NUMBER_OF_UPGRADE_SLOTS, numberOfUpgradeSlots);
	}

	@Override
	public void refreshInventoryForUpgradeProcessing() {
		inventoryModificationHandler = null;
		fluidHandler = null;
		fluidHandlerInitialized = false;
		energyStorage = null;
		energyStorageInitialized = false;
		refreshInventoryForInputOutput();
	}

	@Override
	public void onContentsNbtUpdated() {
		handler = null;
		upgradeHandler = null;
		refreshInventoryForUpgradeProcessing();
		onInventoryHandlerRefresh.run();
	}

	@Override
	public void registerOnInventoryHandlerRefreshListener(Runnable onInventoryHandlerRefresh) {
		this.onInventoryHandlerRefresh = onInventoryHandlerRefresh;
	}

	@Override
	public void unregisterOnInventoryHandlerRefreshListener() {
		onInventoryHandlerRefresh = () -> {
		};
	}

	@Override
	public ItemStack getWrappedStorageStack() {
		return getBackpack();
	}

	@Override
	public String getStorageType() {
		return "backpack";
	}

	@Override
	public Component getDisplayName() {
		return getBackpack().getHoverName();
	}

	private static class FluidHandlerItemWrapper implements IStorageFluidHandler {
		private final IStorageFluidHandler delegate;
		private final ItemStack container;

		public FluidHandlerItemWrapper(ItemStack container, IStorageFluidHandler delegate) {
			this.container = container;
			this.delegate = delegate;
		}

		public ItemStack getContainer() {
			return container;
		}

		@Override
		public long insert(FluidVariant resource, long maxFill, TransactionContext ctx, boolean ignoreInOutLimit) {
			return delegate.insert(resource, maxFill, ctx, ignoreInOutLimit);
		}

		@Override
		public long extract(FluidVariant resource, long maxDrain, TransactionContext ctx, boolean ignoreInOutLimit) {
			return delegate.extract(resource, maxDrain, ctx, ignoreInOutLimit);
		}

		@Override
		public FluidStack extract(TagKey<Fluid> resourceTag, long maxDrain, TransactionContext ctx, boolean ignoreInOutLimit) {
			return delegate.extract(resourceTag, maxDrain, ctx, ignoreInOutLimit);
		}

		@Override
		public FluidStack extract(int maxDrain, TransactionContext ctx, boolean ignoreInOutLimit) {
			return delegate.extract(maxDrain, ctx, ignoreInOutLimit);
		}

		@Override
		public FluidStack extract(FluidStack resource, TransactionContext ctx, boolean ignoreInOutLimit) {
			return delegate.extract(resource, ctx, ignoreInOutLimit);
		}

		@Override
		public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {
			return delegate.insert(resource, maxAmount, transaction);
		}

		@Override
		public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
			return delegate.extract(resource, maxAmount, transaction);
		}

		@Override
		public Iterator<StorageView<FluidVariant>> iterator() {
			return delegate.iterator();
		}
	}
}
