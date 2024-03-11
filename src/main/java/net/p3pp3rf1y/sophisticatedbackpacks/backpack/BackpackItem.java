package net.p3pp3rf1y.sophisticatedbackpacks.backpack;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.p3pp3rf1y.sophisticatedbackpacks.Config;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.IBackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.common.BackpackWrapperLookup;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.BackpackContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.BackpackContext;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.everlasting.EverlastingBackpackItemEntity;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.everlasting.EverlastingUpgradeItem;
import net.p3pp3rf1y.sophisticatedbackpacks.util.InventoryInteractionHelper;
import net.p3pp3rf1y.sophisticatedbackpacks.util.PlayerInventoryProvider;
import net.p3pp3rf1y.sophisticatedcore.api.IStashStorageItem;
import net.p3pp3rf1y.sophisticatedcore.api.IStorageWrapper;
import net.p3pp3rf1y.sophisticatedcore.client.gui.utils.TranslationHelper;
import net.p3pp3rf1y.sophisticatedcore.settings.memory.MemorySettingsCategory;
import net.p3pp3rf1y.sophisticatedcore.upgrades.ITickableUpgrade;
import net.p3pp3rf1y.sophisticatedcore.upgrades.jukebox.ServerStorageSoundHandler;
import net.p3pp3rf1y.sophisticatedcore.util.ColorHelper;
import net.p3pp3rf1y.sophisticatedcore.util.ItemBase;
import net.p3pp3rf1y.sophisticatedcore.util.MenuProviderHelper;
import net.p3pp3rf1y.sophisticatedcore.util.WorldHelper;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

public class BackpackItem extends ItemBase implements IStashStorageItem, Equipable {
	private final IntSupplier numberOfSlots;
	private final IntSupplier numberOfUpgradeSlots;
	private final Supplier<BackpackBlock> blockSupplier;

	public BackpackItem(IntSupplier numberOfSlots, IntSupplier numberOfUpgradeSlots, Supplier<BackpackBlock> blockSupplier) {
		this(numberOfSlots, numberOfUpgradeSlots, blockSupplier, p -> p);
	}

	public BackpackItem(IntSupplier numberOfSlots, IntSupplier numberOfUpgradeSlots, Supplier<BackpackBlock> blockSupplier, UnaryOperator<Properties> updateProperties) {
		super(updateProperties.apply(new Properties().stacksTo(1)));
		this.numberOfSlots = numberOfSlots;
		this.numberOfUpgradeSlots = numberOfUpgradeSlots;
		this.blockSupplier = blockSupplier;
	}

	@Override
	public void addCreativeTabItems(Consumer<ItemStack> itemConsumer) {
		super.addCreativeTabItems(itemConsumer);

		if (this != ModItems.BACKPACK || !net.p3pp3rf1y.sophisticatedcore.Config.COMMON.enabledItems.isItemEnabled(this)) {
			return;
		}

		for (DyeColor color : DyeColor.values()) {
			ItemStack stack = new ItemStack(this);
			new BackpackWrapper(stack).setColors(ColorHelper.getColor(color.getTextureDiffuseColors()), ColorHelper.getColor(color.getTextureDiffuseColors()));
			itemConsumer.accept(stack);
		}

		int clothColor = ColorHelper.calculateColor(BackpackWrapper.DEFAULT_CLOTH_COLOR, BackpackWrapper.DEFAULT_CLOTH_COLOR, List.of(
				DyeColor.BLUE, DyeColor.YELLOW, DyeColor.LIME
		));
		int trimColor = ColorHelper.calculateColor(BackpackWrapper.DEFAULT_BORDER_COLOR, BackpackWrapper.DEFAULT_BORDER_COLOR, List.of(
				DyeColor.BLUE, DyeColor.BLACK
		));

		ItemStack stack = new ItemStack(this);
		new BackpackWrapper(stack).setColors(clothColor, trimColor);
		itemConsumer.accept(stack);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
		if (flagIn == TooltipFlag.Default.ADVANCED) {
			BackpackWrapperLookup.get(stack)
					.flatMap(IStorageWrapper::getContentsUuid)
					.ifPresent(uuid -> tooltip.add(Component.literal("UUID: " + uuid).withStyle(ChatFormatting.DARK_GRAY)));
		}
		if (!Screen.hasShiftDown()) {
			tooltip.add(Component.translatable(
					TranslationHelper.INSTANCE.translItemTooltip("storage") + ".press_for_contents",
					Component.translatable(TranslationHelper.INSTANCE.translItemTooltip("storage") + ".shift").withStyle(ChatFormatting.AQUA)
			).withStyle(ChatFormatting.GRAY));
		}
	}

	@Override
	public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
		TooltipComponent ret = null;
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			Minecraft mc = Minecraft.getInstance();
			if (Screen.hasShiftDown() || (mc.player != null && !mc.player.containerMenu.getCarried().isEmpty())) {
				ret = new BackpackContentsTooltip(stack);
			}
		}
		return Optional.ofNullable(ret);
	}

	private boolean hasEverlastingUpgrade(ItemStack stack) {
		return BackpackWrapperLookup.get(stack).map(w -> !w.getUpgradeHandler().getTypeWrappers(EverlastingUpgradeItem.TYPE).isEmpty()).orElse(false);
	}

	@Nullable
	public Entity createCustomEntity(Level world, Entity entity, ItemStack itemstack) {
		if (!(entity instanceof ItemEntity itemEntity)) {
			return null;
		}

		UUIDDeduplicator.dedupeBackpackItemEntityInArea(itemEntity);
		return hasEverlastingUpgrade(itemstack) ? createEverlastingBackpack(world, (ItemEntity) entity, itemstack) : null;
	}

	@Nullable
	private EverlastingBackpackItemEntity createEverlastingBackpack(Level world, ItemEntity itemEntity, ItemStack itemstack) {
		EverlastingBackpackItemEntity backpackItemEntity = ModItems.EVERLASTING_BACKPACK_ITEM_ENTITY.create(world);
		if (backpackItemEntity != null) {
			backpackItemEntity.setPos(itemEntity.getX(), itemEntity.getY(), itemEntity.getZ());
			backpackItemEntity.setItem(itemstack);
			backpackItemEntity.setPickUpDelay(itemEntity.pickupDelay);
			backpackItemEntity.setThrower(itemEntity.getOwner() != null ? itemEntity.getOwner().getUUID() : null);
			backpackItemEntity.setDeltaMovement(itemEntity.getDeltaMovement());
		}
		return backpackItemEntity;
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Player player = context.getPlayer();
		if (player == null || !player.isShiftKeyDown()) {
			return InteractionResult.PASS;
		}

		if (InventoryInteractionHelper.tryInventoryInteraction(context)) {
			return InteractionResult.SUCCESS;
		}

		Direction direction = player.getDirection().getOpposite();

		BlockPlaceContext blockItemUseContext = new BlockPlaceContext(context);
		InteractionResult result = tryPlace(player, direction, blockItemUseContext);
		return result == InteractionResult.PASS ? super.useOn(context) : result;
	}

	public InteractionResult tryPlace(@Nullable Player player, Direction direction, BlockPlaceContext blockItemUseContext) {
		if (!blockItemUseContext.canPlace()) {
			return InteractionResult.FAIL;
		}
		Level world = blockItemUseContext.getLevel();
		BlockPos pos = blockItemUseContext.getClickedPos();

		FluidState fluidstate = blockItemUseContext.getLevel().getFluidState(pos);
		BlockState placementState = blockSupplier.get().defaultBlockState().setValue(BackpackBlock.FACING, direction)
				.setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
		if (!canPlace(blockItemUseContext, placementState)) {
			return InteractionResult.FAIL;
		}

		if (world.setBlockAndUpdate(pos, placementState)) {
			ItemStack backpack = blockItemUseContext.getItemInHand();
			WorldHelper.getBlockEntity(world, pos, BackpackBlockEntity.class).ifPresent(te -> {
				te.setBackpack(getBackpackCopy(player, backpack));
				te.refreshRenderState();

				te.tryToAddToController();
			});

			if (!world.isClientSide) {
				stopBackpackSounds(backpack, world, pos);
			}

			SoundType soundtype = placementState.getSoundType();
			world.playSound(player, pos, soundtype.getPlaceSound(), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
			if (player == null || !player.isCreative()) {
				backpack.shrink(1);
			}

			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	private static void stopBackpackSounds(ItemStack backpack, Level world, BlockPos pos) {
		BackpackWrapperLookup.get(backpack).flatMap(IStorageWrapper::getContentsUuid).ifPresent(uuid ->
				ServerStorageSoundHandler.stopPlayingDisc((ServerLevel) world, Vec3.atCenterOf(pos), uuid));
	}

	private ItemStack getBackpackCopy(@Nullable Player player, ItemStack backpack) {
		if (player == null || !player.isCreative()) {
			return backpack.copy();
		}
		return BackpackWrapperLookup.get(backpack)
				.map(IBackpackWrapper::cloneBackpack).orElse(new ItemStack(ModItems.BACKPACK));
	}

	protected boolean canPlace(BlockPlaceContext context, BlockState state) {
		Player playerentity = context.getPlayer();
		CollisionContext iselectioncontext = playerentity == null ? CollisionContext.empty() : CollisionContext.of(playerentity);
		return state.canSurvive(context.getLevel(), context.getClickedPos()) && context.getLevel().isUnobstructed(state, context.getClickedPos(), iselectioncontext);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if (!world.isClientSide && player instanceof ServerPlayer) {
			String handlerName = hand == InteractionHand.MAIN_HAND ? PlayerInventoryProvider.MAIN_INVENTORY : PlayerInventoryProvider.OFFHAND_INVENTORY;
			int slot = hand == InteractionHand.MAIN_HAND ? player.getInventory().selected : 0;
			BackpackContext.Item context = new BackpackContext.Item(handlerName, slot);

			player.openMenu(MenuProviderHelper.createMenuProvider((w, bpc, pl) -> new BackpackContainer(w, pl, context), context, stack.getHoverName()));
		}
		return InteractionResultHolder.success(stack);
	}

	@Override
	public void onArmorTick(ItemStack stack, Level level, Player player) {
		if (level.isClientSide || player.isSpectator() || player.isDeadOrDying() || Boolean.FALSE.equals(Config.SERVER.nerfsConfig.onlyWornBackpackTriggersUpgrades.get())) {
			return;
		}
		BackpackWrapperLookup.get(stack).ifPresent(
				wrapper -> wrapper.getUpgradeHandler().getWrappersThatImplement(ITickableUpgrade.class)
						.forEach(upgrade -> upgrade.tick(player, player.getLevel(), player.blockPosition()))
		);
		super.onArmorTick(stack, level, player);
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entityIn, int itemSlot, boolean isSelected) {
		if (level.isClientSide || !(entityIn instanceof Player player) || player.isSpectator() || player.isDeadOrDying() || (Config.SERVER.nerfsConfig.onlyWornBackpackTriggersUpgrades.get() && itemSlot > -1)) {
			return;
		}
		BackpackWrapperLookup.get(stack).ifPresent(
				wrapper -> wrapper.getUpgradeHandler().getWrappersThatImplement(ITickableUpgrade.class)
						.forEach(upgrade -> upgrade.tick(player, player.getLevel(), player.blockPosition()))
		);
		super.inventoryTick(stack, level, entityIn, itemSlot, isSelected);
	}

	public int getNumberOfSlots() {
		return numberOfSlots.getAsInt();
	}

	public int getNumberOfUpgradeSlots() {
		return numberOfUpgradeSlots.getAsInt();
	}

	@Override
	public EquipmentSlot getEquipmentSlot() {
		return EquipmentSlot.CHEST;
	}

	@Override
	public Optional<TooltipComponent> getInventoryTooltip(ItemStack stack) {
		return Optional.of(new BackpackItem.BackpackContentsTooltip(stack));
	}

	@Override
	public ItemStack stash(ItemStack storageStack, ItemStack stack) {
		return BackpackWrapperLookup.get(storageStack)
				.map(wrapper -> {
					try (Transaction ctx = Transaction.openOuter()) {
						long inserted = wrapper.getInventoryForUpgradeProcessing().insert(ItemVariant.of(stack), stack.getCount(), ctx);
						ctx.commit();
						return stack.copyWithCount(stack.getCount() - (int) inserted);
					}
				}).orElse(stack);
	}

	@Override
	public StashResult getItemStashable(ItemStack storageStack, ItemStack stack) {
		return BackpackWrapperLookup.get(storageStack).map(wrapper -> {
			if (StorageUtil.simulateInsert(wrapper.getInventoryForUpgradeProcessing(), ItemVariant.of(stack), stack.getCount(), null) == 0) {
				return StashResult.NO_SPACE;
			}
			if (wrapper.getInventoryHandler().getSlotTracker().getItems().contains(stack.getItem()) || wrapper.getSettingsHandler().getTypeCategory(MemorySettingsCategory.class).matchesFilter(stack)) {
				return StashResult.MATCH_AND_SPACE;
			}

			return StashResult.SPACE;
		}).orElse(StashResult.NO_SPACE);
	}

	public record BackpackContentsTooltip(ItemStack backpack) implements TooltipComponent {
		public ItemStack getBackpack() {
			return backpack;
		}
	}

	@Override
	public boolean overrideStackedOnOther(ItemStack storageStack, Slot slot, ClickAction action, Player player) {
		if (storageStack.getCount() > 1 || !slot.mayPickup(player) || action != ClickAction.SECONDARY) {
			return super.overrideStackedOnOther(storageStack, slot, action, player);
		}

		ItemStack stackToStash = slot.getItem();
		ItemStack stashResult = stash(storageStack, stackToStash);
		if (stashResult.getCount() != stackToStash.getCount()) {
			slot.set(stashResult);
			slot.onTake(player, stashResult);
			return true;
		}

		return super.overrideStackedOnOther(storageStack, slot, action, player);
	}

	@Override
	public boolean overrideOtherStackedOnMe(ItemStack storageStack, ItemStack otherStack, Slot slot, ClickAction action, Player player, SlotAccess carriedAccess) {
		if (storageStack.getCount() > 1 || !slot.mayPlace(storageStack) || action != ClickAction.SECONDARY) {
			return super.overrideOtherStackedOnMe(storageStack, otherStack, slot, action, player, carriedAccess);
		}

		ItemStack result = stash(storageStack, otherStack);
		if (result.getCount() != otherStack.getCount()) {
			carriedAccess.set(result);
			slot.set(storageStack);
			return true;
		}

		return super.overrideOtherStackedOnMe(storageStack, otherStack, slot, action, player, carriedAccess);
	}

	@Override
	public boolean canFitInsideContainerItems() {
		return Config.SERVER.canBePlacedInContainerItems.get();
	}
}