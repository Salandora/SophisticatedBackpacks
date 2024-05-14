package net.p3pp3rf1y.sophisticatedbackpacks.upgrades.toolswapper;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Multimap;
import com.google.common.util.concurrent.AtomicDouble;

import io.github.fabricators_of_create.porting_lib.extensions.extensions.IShearable;
import io.github.fabricators_of_create.porting_lib.tool.ToolAction;
import io.github.fabricators_of_create.porting_lib.tool.ToolActions;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.p3pp3rf1y.sophisticatedbackpacks.Config;
import net.p3pp3rf1y.sophisticatedbackpacks.api.IAttackEntityResponseUpgrade;
import net.p3pp3rf1y.sophisticatedbackpacks.api.IBlockClickResponseUpgrade;
import net.p3pp3rf1y.sophisticatedbackpacks.api.IBlockToolSwapUpgrade;
import net.p3pp3rf1y.sophisticatedbackpacks.api.IEntityToolSwapUpgrade;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.registry.tool.SwordRegistry;
import net.p3pp3rf1y.sophisticatedbackpacks.registry.tool.ToolRegistry;
import net.p3pp3rf1y.sophisticatedcore.api.IStorageWrapper;
import net.p3pp3rf1y.sophisticatedcore.inventory.IItemHandlerSimpleInserter;
import net.p3pp3rf1y.sophisticatedcore.upgrades.FilterLogic;
import net.p3pp3rf1y.sophisticatedcore.upgrades.UpgradeWrapperBase;
import net.p3pp3rf1y.sophisticatedcore.util.NBTHelper;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;

import static io.github.fabricators_of_create.porting_lib.tool.ToolActions.AXE_SCRAPE;
import static io.github.fabricators_of_create.porting_lib.tool.ToolActions.AXE_STRIP;
import static io.github.fabricators_of_create.porting_lib.tool.ToolActions.AXE_WAX_OFF;
import static io.github.fabricators_of_create.porting_lib.tool.ToolActions.SHEARS_CARVE;
import static io.github.fabricators_of_create.porting_lib.tool.ToolActions.SHEARS_HARVEST;
import static io.github.fabricators_of_create.porting_lib.tool.ToolActions.SHOVEL_FLATTEN;

public class ToolSwapperUpgradeWrapper extends UpgradeWrapperBase<ToolSwapperUpgradeWrapper, ToolSwapperUpgradeItem>
		implements IBlockClickResponseUpgrade, IAttackEntityResponseUpgrade, IBlockToolSwapUpgrade, IEntityToolSwapUpgrade {

	private static final LoadingCache<ItemStack, Boolean> isToolCache = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.MINUTES).build(
			new CacheLoader<>() {
				@Override
				public Boolean load(ItemStack key) {
					return canPerformToolAction(key);
				}
			}
	);

	private final FilterLogic filterLogic;
	@Nullable
	private ResourceLocation toolCacheFor = null;
	private final Queue<ItemStack> toolCache = new LinkedList<>();

	private Block lastMinedBlock = Blocks.AIR;

	protected ToolSwapperUpgradeWrapper(IStorageWrapper backpackWrapper, ItemStack upgrade, Consumer<ItemStack> upgradeSaveHandler) {
		super(backpackWrapper, upgrade, upgradeSaveHandler);
		filterLogic = new FilterLogic(upgrade, upgradeSaveHandler, Config.SERVER.toolSwapperUpgrade.filterSlots.get());
	}

	@Override
	public boolean onBlockClick(Player player, BlockPos pos) {
		ToolSwapMode toolSwapMode = getToolSwapMode();
		if (player.isCreative() || player.isSpectator() || toolSwapMode == ToolSwapMode.NO_SWAP) {
			return false;
		}

		ItemStack mainHandItem = player.getMainHandItem();
		if (mainHandItem.getItem() instanceof BackpackItem || (toolSwapMode == ToolSwapMode.ONLY_TOOLS && isSword(mainHandItem, player)) || (!isSword(mainHandItem, player) && isNotTool(mainHandItem)) || !filterLogic.matchesFilter(mainHandItem)) {
			return false;
		}

		BlockState state = player.getLevel().getBlockState(pos);
		Block block = state.getBlock();

		double mainToolSpeed = 0;
		if (isGoodAtBreakingBlock(state, mainHandItem)) {
			if (lastMinedBlock == block) {
				return true;
			}
			mainToolSpeed = mainHandItem.getDestroySpeed(state);
		}

		lastMinedBlock = block;

		return tryToSwapTool(player, state, mainToolSpeed, mainHandItem);
	}

	private boolean tryToSwapTool(Player player, BlockState state, double mainHandItemSpeed, ItemStack mainHandItem) {
		ItemStack selectedTool = ItemStack.EMPTY;
		int selectedSlot = -1;
		double bestSpeed = mainHandItemSpeed;
		IItemHandlerSimpleInserter backpackInventory = storageWrapper.getInventoryHandler();
		for (int slot = 0; slot < backpackInventory.getSlotCount(); slot++) {
			ItemStack stack = backpackInventory.getStackInSlot(slot);
			if (stack.isEmpty()) {
				continue;
			}

			if (isAllowedAndGoodAtBreakingBlock(state, stack)) {
				float destroySpeed = stack.getDestroySpeed(state);
				if (bestSpeed < destroySpeed) {
					bestSpeed = destroySpeed;
					selectedSlot = slot;
					selectedTool = stack;
				}
			}
		}

		ItemVariant mainHandItemResource = ItemVariant.of(mainHandItem);
		if (!selectedTool.isEmpty() && hasSpaceInBackpackOrCanPlaceInTheSlotOfSwappedTool(backpackInventory, mainHandItemResource, mainHandItem.getCount(), selectedTool, selectedSlot)) {
			try (Transaction ctx = Transaction.openOuter()) {
				ItemVariant resource = ItemVariant.of(selectedTool);
				player.setItemInHand(InteractionHand.MAIN_HAND, resource.toStack((int) backpackInventory.extractSlot(selectedSlot, resource, 1, ctx)));
				ctx.commit();
			}
			try (Transaction ctx = Transaction.openOuter()) {
				backpackInventory.insert(ItemVariant.of(mainHandItem), mainHandItem.getCount(), ctx);
				ctx.commit();
			}
			return true;
		}

		return false;
	}

	private boolean hasSpaceInBackpackOrCanPlaceInTheSlotOfSwappedTool(IItemHandlerSimpleInserter backpackInventory, ItemVariant mainHandItem, int mainHandItemCount, ItemStack tool, int selectedSlot) {
		return (StorageUtil.simulateInsert(backpackInventory, mainHandItem, mainHandItemCount, null) == mainHandItemCount)
				|| (tool.getCount() == 1 && backpackInventory.isItemValid(selectedSlot, mainHandItem));
	}

	private boolean isAllowedAndGoodAtBreakingBlock(BlockState state, ItemStack stack) {
		return filterLogic.matchesFilter(stack) && isGoodAtBreakingBlock(state, stack);
	}

	private boolean isGoodAtBreakingBlock(BlockState state, ItemStack stack) {
		return stack.isCorrectToolForDrops(state) && stack.getDestroySpeed(state) > 1.5;
	}

	@Override
	public boolean onAttackEntity(Player player) {
		if (!shouldSwapWeapon()) {
			return false;
		}

		ItemStack mainHandItem = player.getMainHandItem();

		if (isSword(mainHandItem, player)) {
			return true;
		}

		if (mainHandItem.getItem() instanceof BackpackItem || isNotTool(mainHandItem) || !filterLogic.matchesFilter(mainHandItem)) {
			return false;
		}

		return tryToSwapInWeapon(player, mainHandItem);
	}

	private boolean isNotTool(ItemStack stack) {
		return !isToolCache.getUnchecked(stack);
	}

	private static boolean canPerformToolAction(ItemStack stack) {
		return canPerformAnyAction(stack, ToolActions.DEFAULT_AXE_ACTIONS) || canPerformAnyAction(stack, ToolActions.DEFAULT_HOE_ACTIONS)
				|| canPerformAnyAction(stack, ToolActions.DEFAULT_PICKAXE_ACTIONS) || canPerformAnyAction(stack, ToolActions.DEFAULT_SHOVEL_ACTIONS)
				|| canPerformAnyAction(stack, ToolActions.DEFAULT_SHEARS_ACTIONS);
	}

	private static boolean canPerformAnyAction(ItemStack stack, Set<ToolAction> toolActions) {
		for (ToolAction toolAction : toolActions) {
			if (stack.canPerformAction(toolAction)) {
				return true;
			}
		}
		return false;
	}

	private boolean isSword(ItemStack stack, Player player) {
		if (SwordRegistry.isSword(stack)) {
			return true;
		}

		AttributeInstance attackDamage = player.getAttribute(Attributes.ATTACK_DAMAGE);
		if (!stack.isEmpty() && stack.canPerformAction(ToolActions.SWORD_SWEEP)) {
			return attackDamage != null && attackDamage.getModifier(Item.BASE_ATTACK_DAMAGE_UUID) != null;
		}
		return false;
	}

	private boolean tryToSwapInWeapon(Player player, ItemStack mainHandItem) {
		AtomicReference<ItemStack> bestAxe = new AtomicReference<>(ItemStack.EMPTY);
		AtomicDouble bestAxeDamage = new AtomicDouble(0);
		AtomicReference<ItemStack> bestSword = new AtomicReference<>(ItemStack.EMPTY);
		AtomicDouble bestSwordDamage = new AtomicDouble(0);

		updateBestWeapons(bestAxe, bestAxeDamage, bestSword, bestSwordDamage, mainHandItem);

		IItemHandlerSimpleInserter backpackInventory = storageWrapper.getInventoryForUpgradeProcessing();
		for (int slot = 0; slot < backpackInventory.getSlotCount(); slot++) {
			ItemStack stack = backpackInventory.getStackInSlot(slot);
			if (filterLogic.matchesFilter(stack)) {
				updateBestWeapons(bestAxe, bestAxeDamage, bestSword, bestSwordDamage, stack);
			}
		}

		if (!bestSword.get().isEmpty()) {
			return swapWeapon(player, mainHandItem, backpackInventory, bestSword.get());
		} else if (!bestAxe.get().isEmpty()) {
			return swapWeapon(player, mainHandItem, backpackInventory, bestAxe.get());
		}
		return false;
	}

	private void updateBestWeapons(AtomicReference<ItemStack> bestAxe, AtomicDouble bestAxeDamage, AtomicReference<ItemStack> bestSword, AtomicDouble bestSwordDamage, ItemStack stack) {
		AttributeInstance attribute = new AttributeInstance(Attributes.ATTACK_DAMAGE, a -> {});
		Multimap<Attribute, AttributeModifier> attributeModifiers = stack.getAttributeModifiers(EquipmentSlot.MAINHAND);
		if (!attributeModifiers.containsKey(Attributes.ATTACK_DAMAGE)) {
			return;
		}
		attributeModifiers.get(Attributes.ATTACK_DAMAGE).forEach(m -> {
			attribute.removeModifier(m);
			attribute.addTransientModifier(m);
		});
		double damageValue = attribute.getValue();
		if (stack.canPerformAction(ToolActions.AXE_DIG)) {
			if (damageValue > bestAxeDamage.get()) {
				bestAxe.set(stack);
				bestAxeDamage.set(damageValue);
			}
		} else if ((SwordRegistry.isSword(stack) || stack.canPerformAction(ToolActions.SWORD_SWEEP)) && damageValue > bestSwordDamage.get()) {
			bestSword.set(stack);
			bestSwordDamage.set(damageValue);
		}
	}

	private boolean swapWeapon(Player player, ItemStack mainHandItem, IItemHandlerSimpleInserter backpackInventory, ItemStack sword) {
		if (sword == mainHandItem) {
			return true;
		}

		try(Transaction ctx = Transaction.openOuter()) {
			backpackInventory.extract(ItemVariant.of(sword), sword.getCount(), ctx);
			long inserted = backpackInventory.insert(ItemVariant.of(mainHandItem), mainHandItem.getCount(), ctx);
			if (inserted == mainHandItem.getCount()) {
				player.setItemInHand(InteractionHand.MAIN_HAND, sword);
				ctx.commit();
				return true;
			}

			return false;
		}
	}

	@Override
	public boolean hideSettingsTab() {
		return !upgradeItem.hasSettingsTab();
	}

	public FilterLogic getFilterLogic() {
		return filterLogic;
	}

	public boolean shouldSwapWeapon() {
		return NBTHelper.getBoolean(upgrade, "shouldSwapWeapon").orElse(true);
	}

	public void setSwapWeapon(boolean shouldSwapWeapon) {
		NBTHelper.setBoolean(upgrade, "shouldSwapWeapon", shouldSwapWeapon);
		save();
	}

	public ToolSwapMode getToolSwapMode() {
		return NBTHelper.getEnumConstant(upgrade, "toolSwapMode", ToolSwapMode::fromName).orElse(ToolSwapMode.ANY);
	}

	public void setToolSwapMode(ToolSwapMode toolSwapMode) {
		NBTHelper.setEnumConstant(upgrade, "toolSwapMode", toolSwapMode);
		save();
	}

	@Override
	public boolean onEntityInteract(Level world, Entity entity, Player player) {
		if (!upgradeItem.shouldSwapToolOnKeyPress()) {
			return false;
		}

		return tryToSwapTool(player, stack -> itemWorksOnEntity(stack, entity), BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()));
	}

	private boolean itemWorksOnEntity(ItemStack stack, Entity entity) {
		if (isShearableEntity(entity, stack) && isShearsItem(stack)) {
			return true;
		}
		return ToolRegistry.isToolForEntity(stack, entity);
	}

	@Override
	public boolean onBlockInteract(Level world, BlockPos pos, BlockState blockState, Player player) {
		if (!upgradeItem.shouldSwapToolOnKeyPress()) {
			return false;
		}

		return tryToSwapTool(player, stack -> itemWorksOnBlock(world, pos, blockState, player, stack), BuiltInRegistries.BLOCK.getKey(blockState.getBlock()));
	}

	private boolean tryToSwapTool(Player player, Predicate<ItemStack> isToolValid, @Nullable ResourceLocation targetRegistryName) {
		ItemStack mainHandStack = player.getMainHandItem();
		if (mainHandStack.getItem() instanceof BackpackItem) {
			return false;
		}
		if (toolCacheFor == null || !toolCacheFor.equals(targetRegistryName)) {
			toolCache.clear();
			toolCacheFor = targetRegistryName;
		}

		boolean itemInHandIsValid = isToolValid.test(mainHandStack);

		IItemHandlerSimpleInserter backpackInventory = storageWrapper.getInventoryForUpgradeProcessing();
		if (itemInHandIsValid && toolCache.stream().noneMatch(st -> ItemStack.isSame(st, mainHandStack))) {
			toolCache.offer(mainHandStack);
		}
		ItemStack tool = findToolToSwap(backpackInventory, isToolValid);
		if (tool.isEmpty()) {
			return false;
		}

		tool = tool.copy().split(1);
		try (Transaction ctx = Transaction.openOuter()) {
			long inserted = backpackInventory.insert(ItemVariant.of(mainHandStack), mainHandStack.getCount(), ctx);
			if (tool.getCount() == 1 || inserted == 0) {
				ItemVariant resource = ItemVariant.of(tool);
				long extracted = backpackInventory.extract(resource, tool.getCount(), ctx);
				player.setItemInHand(InteractionHand.MAIN_HAND, resource.toStack((int) extracted));
				toolCache.offer(tool);
				ctx.commit();
			}
		}
		return true;
	}

	private ItemStack findToolToSwap(IItemHandlerSimpleInserter backpackInventory, Predicate<ItemStack> isValidTool) {
		Set<ItemStack> alreadyGivenBefore = new HashSet<>();
		for (var view : backpackInventory.nonEmptyViews()) {
			if (view.isResourceBlank()) {
				continue;
			}

			ItemStack stack = view.getResource().toStack((int) view.getAmount());
			if (!hasEquivalentItem(toolCache, stack)) {
				if (isValidTool.test(stack)) {
					return stack;
				}
			} else {
				alreadyGivenBefore.add(stack);
			}
		}

		if (!alreadyGivenBefore.isEmpty()) {
			while (toolCache.peek() != null) {
				ItemStack itemStack = toolCache.poll();
				if (hasEquivalentItem(alreadyGivenBefore, itemStack)) {
					return itemStack;
				}
			}
		}

		return ItemStack.EMPTY;
	}

	private boolean hasEquivalentItem(Collection<ItemStack> alreadyGivenBefore, ItemStack stack) {
		for (ItemStack givenTool : alreadyGivenBefore) {
			if (ItemStack.isSame(givenTool, stack)) {
				return true;
			}
		}

		return false;
	}

	private static final Set<ToolAction> BLOCK_MODIFICATION_ACTIONS = Set.of(AXE_STRIP, AXE_SCRAPE, AXE_WAX_OFF, SHOVEL_FLATTEN, SHEARS_CARVE, SHEARS_HARVEST);

	private boolean itemWorksOnBlock(Level level, BlockPos pos, BlockState blockState, Player player, ItemStack stack) {
		for (ToolAction action : BLOCK_MODIFICATION_ACTIONS) {
			if (stack.canPerformAction(action) && blockState.getToolModifiedState(
					new UseOnContext(level, player, InteractionHand.MAIN_HAND, stack, new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, true)), action, true) != null) {
				return true;
			}
		}
		Block block = blockState.getBlock();
		if (isShearInteractionBlock(level, pos, stack, block) && isShearsItem(stack)) {
			return true;
		}

		return ToolRegistry.isToolForBlock(stack, block, level, blockState, pos);
	}

	private boolean isShearsItem(ItemStack stack) {
		return stack.getItem() instanceof ShearsItem || stack.is(ConventionalItemTags.SHEARS);
	}

	private boolean isShearInteractionBlock(Level world, BlockPos pos, ItemStack stack, Block block) {
		return (block instanceof IShearable shearable && shearable.isShearable(stack, world, pos)) || block instanceof BeehiveBlock;
	}

	private boolean isShearableEntity(Entity entity, ItemStack stack) {
		return entity instanceof IShearable shearable && shearable.isShearable(stack, entity.getLevel(), entity.blockPosition());
	}

	@Override
	public boolean canProcessBlockInteract() {
		return upgradeItem.shouldSwapToolOnKeyPress();
	}

	@Override
	public boolean canProcessEntityInteract() {
		return upgradeItem.shouldSwapToolOnKeyPress();
	}
}
