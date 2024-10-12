package net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import team.reborn.energy.api.EnergyStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.api.IEnergyStorageUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.api.IFluidHandlerWrapperUpgrade;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackStorage;
import net.p3pp3rf1y.sophisticatedbackpacks.common.BackpackWrapperLookup;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems;
import net.p3pp3rf1y.sophisticatedcore.api.IStorageFluidHandler;
import net.p3pp3rf1y.sophisticatedcore.api.IStorageWrapper;
import net.p3pp3rf1y.sophisticatedcore.common.gui.SortBy;
import net.p3pp3rf1y.sophisticatedcore.inventory.ITrackedContentsItemHandler;
import net.p3pp3rf1y.sophisticatedcore.inventory.InventoryHandler;
import net.p3pp3rf1y.sophisticatedcore.inventory.InventoryIOHandler;
import net.p3pp3rf1y.sophisticatedcore.inventory.ItemStackKey;
import net.p3pp3rf1y.sophisticatedcore.settings.itemdisplay.ItemDisplaySettingsCategory;
import net.p3pp3rf1y.sophisticatedcore.settings.memory.MemorySettingsCategory;
import net.p3pp3rf1y.sophisticatedcore.settings.nosort.NoSortSettingsCategory;
import net.p3pp3rf1y.sophisticatedcore.upgrades.UpgradeHandler;
import net.p3pp3rf1y.sophisticatedcore.upgrades.stack.StackUpgradeItem;
import net.p3pp3rf1y.sophisticatedcore.upgrades.tank.TankUpgradeItem;
import net.p3pp3rf1y.sophisticatedcore.util.*;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.IntConsumer;

public class BackpackWrapper implements IBackpackWrapper {
	public static final int DEFAULT_CLOTH_COLOR = 13394234;
	public static final int DEFAULT_BORDER_COLOR = 6434330;
	private static final String CLOTH_COLOR_TAG = "clothColor";
	private static final String BORDER_COLOR_TAG = "borderColor";
	private static final String OPEN_TAB_ID_TAG = "openTabId";
	private static final String SORT_BY_TAG = "sortBy";
	private static final String CONTENTS_UUID_TAG = "contentsUuid";
	private static final String INVENTORY_SLOTS_TAG = "inventorySlots";
	private static final String UPGRADE_SLOTS_TAG = "upgradeSlots";
	private static final String LOOT_TABLE_NAME_TAG = "lootTableName";
	private static final String LOOT_PERCENTAGE_TAG = "lootPercentage";
	private static final String COLUMNS_TAKEN_TAG = "columnsTaken";

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

	public static IBackpackWrapper fromData(ItemStack stack) {
		return BackpackWrapperLookup.getOrCreate(stack).setBackpackStack(stack);
		// TODO: Switch to this if fabric adds ItemStack attachments
		// return stack.getAttachedOrCreate(ModItems.BACKPACK_WRAPPER).setBackpackStack(stack);
	}

	public static Optional<IBackpackWrapper> fromExistingData(ItemStack stack) {
		return Optional.ofNullable(BackpackWrapperLookup.get(stack)).map(wrapper -> wrapper.setBackpackStack(stack));
		// TODO: Switch to this if fabric adds ItemStack attachments
		// return Optional.ofNullable(stack.getAttached(ModItems.BACKPACK_WRAPPER)).map(wrapper -> wrapper.setBackpackStack(stack));
	}

	@Override
	public void setSaveHandler(Runnable saveHandler) {
		backpackSaveHandler = saveHandler;
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
		Optional<Integer> inventorySlots = NBTHelper.getInt(getBackpackStack(), INVENTORY_SLOTS_TAG);

		if (inventorySlots.isPresent()) {
			return inventorySlots.get();
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
		NBTHelper.setInteger(getBackpackStack(), INVENTORY_SLOTS_TAG, itemInventorySlots);
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
					public boolean isItemValid(int slot, ItemVariant resource, int count) {
						return super.isItemValid(slot, resource, count) && (resource.isBlank() || SophisticatedBackpacks.MOD_ID.equals(BuiltInRegistries.ITEM.getKey(resource.getItem()).getNamespace()) || resource.toStack(count).is(ModItems.BACKPACK_UPGRADE_TAG));
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
		Optional<Integer> upgradeSlots = NBTHelper.getInt(getBackpackStack(), UPGRADE_SLOTS_TAG);

		if (upgradeSlots.isPresent()) {
			return upgradeSlots.get();
		}

		int itemUpgradeSlots = ((BackpackItem) getBackpackStack().getItem()).getNumberOfUpgradeSlots();
		setNumberOfUpgradeSlots(itemUpgradeSlots);
		return itemUpgradeSlots;
	}

	@Override
	public Optional<UUID> getContentsUuid() {
		return NBTHelper.getUniqueId(getBackpackStack(), CONTENTS_UUID_TAG);
	}

	private UUID getOrCreateContentsUuid() {
		Optional<UUID> contentsUuid = getContentsUuid();
		if (contentsUuid.isPresent()) {
			return contentsUuid.get();
		}
		clearDummyHandlers();
		UUID newUuid = UUID.randomUUID();
		setContentsUuid(newUuid);
		migrateBackpackContents(newUuid);
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

	private void migrateBackpackContents(UUID newUuid) {
		migrateNbtTag(newUuid, InventoryHandler.INVENTORY_TAG);
		migrateNbtTag(newUuid, UpgradeHandler.UPGRADE_INVENTORY_TAG);
	}

	private void migrateNbtTag(UUID newUuid, String key) {
		NBTHelper.getCompound(getBackpackStack(), key)
				.ifPresent(nbt -> {
					BackpackStorage.get().getOrCreateBackpackContents(newUuid).put(key, nbt);
					markBackpackContentsDirty();
					NBTHelper.removeTag(getBackpackStack(), key);
				});
	}

	@Override
	public int getMainColor() {
		return NBTHelper.getInt(getBackpackStack(), CLOTH_COLOR_TAG).orElse(DEFAULT_CLOTH_COLOR);
	}

	@Override
	public int getAccentColor() {
		return NBTHelper.getInt(getBackpackStack(), BORDER_COLOR_TAG).orElse(DEFAULT_BORDER_COLOR);
	}

	@Override
	public Optional<Integer> getOpenTabId() {
		return NBTHelper.getInt(getBackpackStack(), OPEN_TAB_ID_TAG);
	}

	@Override
	public void setOpenTabId(int openTabId) {
		NBTHelper.setInteger(getBackpackStack(), OPEN_TAB_ID_TAG, openTabId);
		backpackSaveHandler.run();
	}

	@Override
	public void removeOpenTabId() {
		getBackpackStack().getOrCreateTag().remove(OPEN_TAB_ID_TAG);
		backpackSaveHandler.run();
	}

	@Override
	public void setColors(int mainColor, int accentColor) {
		getBackpackStack().addTagElement(CLOTH_COLOR_TAG, IntTag.valueOf(mainColor));
		getBackpackStack().addTagElement(BORDER_COLOR_TAG, IntTag.valueOf(accentColor));
		backpackSaveHandler.run();
	}

	@Override
	public void setSortBy(SortBy sortBy) {
		getBackpackStack().addTagElement(SORT_BY_TAG, StringTag.valueOf(sortBy.getSerializedName()));
		backpackSaveHandler.run();
	}

	@Override
	public SortBy getSortBy() {
		return NBTHelper.getEnumConstant(getBackpackStack(), SORT_BY_TAG, SortBy::fromName).orElse(SortBy.NAME);
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
		cloneSubbackpacks(BackpackWrapper.fromData(clonedBackpack));
		return clonedBackpack;
	}

	private void cloneSubbackpacks(IStorageWrapper wrapperCloned) {
		InventoryHandler inventoryHandler = wrapperCloned.getInventoryHandler();
		InventoryHelper.iterate(inventoryHandler, (slot, stack) -> {
			if (!(stack.getItem() instanceof BackpackItem)) {
				return;
			}
			inventoryHandler.setStackInSlot(slot, cloneBackpack(BackpackWrapper.fromData(stack)));
		});
	}

	private ItemStack cloneBackpack(IBackpackWrapper originalWrapper) {
		ItemStack backpackCopy = originalWrapper.getBackpack().copy();
		backpackCopy.removeTagKey(CONTENTS_UUID_TAG);
		IBackpackWrapper wrapperCopy = BackpackWrapper.fromData(backpackCopy);
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
	public void setLoot(ResourceLocation lootTableName, float lootPercentage) {
		getBackpackStack().addTagElement(LOOT_TABLE_NAME_TAG, StringTag.valueOf(lootTableName.toString()));
		getBackpackStack().addTagElement(LOOT_PERCENTAGE_TAG, FloatTag.valueOf(lootPercentage));
		backpackSaveHandler.run();
	}

	@Override
	public void fillWithLoot(Player playerEntity) {
		if (playerEntity.level().isClientSide) {
			return;
		}
		NBTHelper.getString(getBackpackStack(), LOOT_TABLE_NAME_TAG).ifPresent(ltName -> fillWithLootFromTable(playerEntity, ltName));
	}

	@Override
	public void setContentsUuid(UUID storageUuid) {
		NBTHelper.setUniqueId(getBackpackStack(), CONTENTS_UUID_TAG, storageUuid);
	}

	@Override
	public void removeContentsUuid() {
		getContentsUuid().ifPresent(BackpackStorage.get()::removeBackpackContents);
		removeContentsUUIDTag();
	}

	@Override
	public void removeContentsUUIDTag() {
		NBTHelper.removeTag(getBackpackStack(), CONTENTS_UUID_TAG);
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
		NBTHelper.setInteger(getBackpackStack(), COLUMNS_TAKEN_TAG, columnsTaken);
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
		return NBTHelper.getInt(getBackpackStack(), COLUMNS_TAKEN_TAG).orElse(0);
	}

	private void fillWithLootFromTable(Player playerEntity, String lootName) {
		MinecraftServer server = playerEntity.level().getServer();
		if (server == null || !(playerEntity.level() instanceof ServerLevel serverLevel)) {
			return;
		}

		ResourceLocation lootTableName = new ResourceLocation(lootName);
		float lootPercentage = NBTHelper.getFloat(getBackpackStack(), LOOT_PERCENTAGE_TAG).orElse(0f);

		getBackpackStack().removeTagKey(LOOT_TABLE_NAME_TAG);
		getBackpackStack().removeTagKey(LOOT_PERCENTAGE_TAG);

		List<ItemStack> loot = LootHelper.getLoot(lootTableName, server, serverLevel, playerEntity);
		loot.removeIf(stack -> stack.getItem() instanceof BackpackItem);
		loot = RandHelper.getNRandomElements(loot, (int) (loot.size() * lootPercentage));
		LootHelper.fillWithLoot(serverLevel.random, loot, getInventoryHandler());
	}

	private void setNumberOfUpgradeSlots(int numberOfUpgradeSlots) {
		NBTHelper.setInteger(getBackpackStack(), UPGRADE_SLOTS_TAG, numberOfUpgradeSlots);
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
