package net.p3pp3rf1y.sophisticatedbackpacks.backpack;

import team.reborn.energy.api.EnergyStorage;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerBlockEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.p3pp3rf1y.porting_lib.transfer.items.SlottedStackStorage;
import net.p3pp3rf1y.sophisticatedbackpacks.Config;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.IBackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.common.BackpackWrapperLookup;
import net.p3pp3rf1y.sophisticatedcore.api.IStorageFluidHandler;
import net.p3pp3rf1y.sophisticatedcore.api.IStorageWrapper;
import net.p3pp3rf1y.sophisticatedcore.controller.ControllerBlockEntityBase;
import net.p3pp3rf1y.sophisticatedcore.controller.IControllableStorage;
import net.p3pp3rf1y.sophisticatedcore.inventory.CachedFailedInsertInventoryHandler;
import net.p3pp3rf1y.sophisticatedcore.renderdata.RenderInfo;
import net.p3pp3rf1y.sophisticatedcore.renderdata.TankPosition;
import net.p3pp3rf1y.sophisticatedcore.upgrades.ITickableUpgrade;
import net.p3pp3rf1y.sophisticatedcore.util.WorldHelper;

import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;

import static net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackBlock.BATTERY;
import static net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackBlock.LEFT_TANK;
import static net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackBlock.RIGHT_TANK;
import static net.p3pp3rf1y.sophisticatedbackpacks.init.ModBlocks.BACKPACK_TILE_TYPE;

public class BackpackBlockEntity extends BlockEntity implements IControllableStorage {
	@Nullable
	private BlockPos controllerPos = null;
	private IBackpackWrapper backpackWrapper = IBackpackWrapper.Noop.INSTANCE;
	private boolean updateBlockRender = true;
	private boolean chunkBeingUnloaded = false;

	@Nullable
	private SlottedStackStorage itemHandlerCap;
	@Nullable
	private IStorageFluidHandler fluidHandlerCap;
	@Nullable
	private EnergyStorage energyStorageCap;

	public BackpackBlockEntity(BlockPos pos, BlockState state) {
		super(BACKPACK_TILE_TYPE, pos, state);

		ServerChunkEvents.CHUNK_UNLOAD.register((level, chunk) -> onChunkUnloaded());
		ServerBlockEntityEvents.BLOCK_ENTITY_UNLOAD.register((be, world) -> {
			if (be == this) {
				invalidateCaps();
			}
		});
	}

	public void setBackpack(ItemStack backpack) {
		backpackWrapper = BackpackWrapperLookup.get(backpack).orElse(IBackpackWrapper.Noop.INSTANCE);
		backpackWrapper.setSaveHandler(() -> {
			setChanged();
			updateBlockRender = false;
			WorldHelper.notifyBlockUpdate(this);
		});
		backpackWrapper.setInventorySlotChangeHandler(this::setChanged);
		backpackWrapper.setUpgradeCachesInvalidatedHandler(this::invalidateBackpackCaps);
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		setBackpackFromNbt(tag);

		// If updateBlockRender exists we are in a update packet load
		if (tag.contains("updateBlockRender")) {
			if (tag.getBoolean("updateBlockRender")) {
				WorldHelper.notifyBlockUpdate(this);
			}
		} else {
			loadControllerPos(tag);

			if (level != null && !level.isClientSide()) {
				removeControllerPos();
				tryToAddToController();
			}

			WorldHelper.notifyBlockUpdate(this);
		}
	}

	@Override
	public void onLoad() {
		registerWithControllerOnLoad();
	}

	private void setBackpackFromNbt(CompoundTag nbt) {
		setBackpack(ItemStack.of(nbt.getCompound("backpackData")));
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		writeBackpack(tag);
		saveControllerPos(tag);
	}

	private void writeBackpack(CompoundTag ret) {
		ItemStack backpackCopy = backpackWrapper.getBackpack().copy();
		backpackCopy.setTag(backpackCopy.getTag());
		ret.put("backpackData", backpackCopy.save(new CompoundTag()));
	}

	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag ret = super.getUpdateTag();
		writeBackpack(ret);
		ret.putBoolean("updateBlockRender", updateBlockRender);
		updateBlockRender = true;
		return ret;
	}

	@Nullable
	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	public IBackpackWrapper getBackpackWrapper() {
		return backpackWrapper;
	}

	@Nullable
	public <T> T getCapability(BlockApiLookup<T, Direction> cap, @Nullable Direction opt) {
		if (opt != null && level != null && Config.COMMON.noConnectionBlocks.isBlockConnectionDisallowed(level.getBlockState(getBlockPos().relative(opt)).getBlock())) {
			return null;
		}

		if (cap == ItemStorage.SIDED) {
			if (itemHandlerCap == null) {
				itemHandlerCap = new CachedFailedInsertInventoryHandler(getBackpackWrapper().getInventoryForInputOutput(), () -> level != null ? level.getGameTime() : 0);
			}
			//noinspection unchecked
			return (T) itemHandlerCap;
		} else if (cap == FluidStorage.SIDED) {
			if (fluidHandlerCap == null) {
				fluidHandlerCap = getBackpackWrapper().getFluidHandler().orElse(null);
			}
			//noinspection unchecked
			return (T) fluidHandlerCap;
		} else if (cap == EnergyStorage.SIDED) {
			if (energyStorageCap == null) {
				energyStorageCap = getBackpackWrapper().getEnergyStorage().orElse(null);
			}
			//noinspection unchecked
			return (T) energyStorageCap;
		}
		return null;
	}

	public void invalidateCaps() {
		invalidateBackpackCaps();
	}

	private void invalidateBackpackCaps() {
		if (itemHandlerCap != null) {
			itemHandlerCap = null;
		}
		if (fluidHandlerCap != null) {
			fluidHandlerCap = null;
		}
		if (energyStorageCap != null) {
			energyStorageCap = null;
		}
	}

	public void refreshRenderState() {
		BlockState state = getBlockState();
		state = state.setValue(LEFT_TANK, false);
		state = state.setValue(RIGHT_TANK, false);
		RenderInfo renderInfo = backpackWrapper.getRenderInfo();
		for (TankPosition pos : renderInfo.getTankRenderInfos().keySet()) {
			if (pos == TankPosition.LEFT) {
				state = state.setValue(LEFT_TANK, true);
			} else if (pos == TankPosition.RIGHT) {
				state = state.setValue(RIGHT_TANK, true);
			}
		}
		state = state.setValue(BATTERY, renderInfo.getBatteryRenderInfo().isPresent());
		Level l = Objects.requireNonNull(level);
		l.setBlockAndUpdate(worldPosition, state);
		l.updateNeighborsAt(worldPosition, state.getBlock());
		WorldHelper.notifyBlockUpdate(this);
	}

	public static void serverTick(Level level, BlockPos blockPos, BackpackBlockEntity backpackBlockEntity) {
		backpackBlockEntity.backpackWrapper.getUpgradeHandler().getWrappersThatImplement(ITickableUpgrade.class).forEach(upgrade -> upgrade.tick(null, level, blockPos));
	}

	@Override
	public IStorageWrapper getStorageWrapper() {
		return backpackWrapper;
	}

	@Override
	public void setControllerPos(BlockPos controllerPos) {
		this.controllerPos = controllerPos;
		setChanged();
	}

	@Override
	public Optional<BlockPos> getControllerPos() {
		return Optional.ofNullable(controllerPos);
	}

	@Override
	public void removeControllerPos() {
		controllerPos = null;
	}

	@Override
	public BlockPos getStorageBlockPos() {
		return getBlockPos();
	}

	@Override
	public Level getStorageBlockLevel() {
		return Objects.requireNonNull(getLevel());
	}

	@Override
	public boolean canConnectStorages() {
		return false;
	}

	@Override
	public void unregisterController() {
		IControllableStorage.super.unregisterController();
		backpackWrapper.unregisterOnSlotsChangeListener();
		backpackWrapper.unregisterOnInventoryHandlerRefreshListener();
	}

	@Override
	public void registerController(ControllerBlockEntityBase controllerBlockEntity) {
		IControllableStorage.super.registerController(controllerBlockEntity);
		if (level != null && !level.isClientSide) {
			backpackWrapper.registerOnSlotsChangeListener(this::changeSlots);
			backpackWrapper.registerOnInventoryHandlerRefreshListener(this::registerInventoryStackListeners);
		}
	}

	public void onChunkUnloaded() {
		chunkBeingUnloaded = true;
	}

	@Override
	public void setRemoved() {
		if (!chunkBeingUnloaded && level != null) {
			removeFromController();
		}
		super.setRemoved();
	}
}
