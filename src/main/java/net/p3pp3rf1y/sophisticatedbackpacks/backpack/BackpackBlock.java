package net.p3pp3rf1y.sophisticatedbackpacks.backpack;

import com.mojang.math.Axis;
import org.joml.Vector3f;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.p3pp3rf1y.sophisticatedbackpacks.common.BackpackWrapperLookup;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.BackpackContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.BackpackContext;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModBlocks;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.everlasting.EverlastingUpgradeItem;
import net.p3pp3rf1y.sophisticatedcore.api.IStorageWrapper;
import net.p3pp3rf1y.sophisticatedcore.api.IUpgradeRenderer;
import net.p3pp3rf1y.sophisticatedcore.client.render.UpgradeRenderRegistry;
import net.p3pp3rf1y.sophisticatedcore.controller.IControllableStorage;
import net.p3pp3rf1y.sophisticatedcore.renderdata.IUpgradeRenderData;
import net.p3pp3rf1y.sophisticatedcore.renderdata.RenderInfo;
import net.p3pp3rf1y.sophisticatedcore.renderdata.UpgradeRenderDataType;
import net.p3pp3rf1y.sophisticatedcore.upgrades.jukebox.ServerStorageSoundHandler;
import net.p3pp3rf1y.sophisticatedcore.util.FluidHelper;
import net.p3pp3rf1y.sophisticatedcore.util.InventoryHelper;
import net.p3pp3rf1y.sophisticatedcore.util.MenuProviderHelper;
import net.p3pp3rf1y.sophisticatedcore.util.WorldHelper;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

public class BackpackBlock extends Block implements EntityBlock, SimpleWaterloggedBlock, BlockInterfaceHelper {
	public static final BooleanProperty LEFT_TANK = BooleanProperty.create("left_tank");
	public static final BooleanProperty RIGHT_TANK = BooleanProperty.create("right_tank");
	public static final BooleanProperty BATTERY = BooleanProperty.create("battery");

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	private static final int BEDROCK_RESISTANCE = 3600000;

	public BackpackBlock() {
		this(0.8F);
	}

	public BackpackBlock(float explosionResistance) {
		super(Properties.of().mapColor(MapColor.WOOL).noOcclusion().strength(0.8F, explosionResistance).sound(SoundType.WOOL).pushReaction(PushReaction.DESTROY));
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false).setValue(LEFT_TANK, false).setValue(RIGHT_TANK, false));
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean hasAnalogOutputSignal(BlockState pState) {
		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	public int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos pos) {
		return WorldHelper.getBlockEntity(level, pos, BackpackBlockEntity.class).map(t -> InventoryHelper.getAnalogOutputSignal(t.getBackpackWrapper().getInventoryForInputOutput())).orElse(0);
	}

	@SuppressWarnings("deprecation")
	@Override
	public FluidState getFluidState(BlockState state) {
		return Boolean.TRUE.equals(state.getValue(WATERLOGGED)) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@SuppressWarnings("deprecation")
	@Override
	public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
		if (Boolean.TRUE.equals(stateIn.getValue(WATERLOGGED))) {
			level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		}

		return super.updateShape(stateIn, facing, facingState, level, currentPos, facingPos);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, WATERLOGGED, LEFT_TANK, RIGHT_TANK, BATTERY);
	}

	@Override
	public float getExplosionResistance(BlockState state, BlockGetter world, BlockPos pos, Explosion explosion) {
		if (hasEverlastingUpgrade(world, pos)) {
			return BEDROCK_RESISTANCE;
		}
		return super.getExplosionResistance();
	}

	private boolean hasEverlastingUpgrade(BlockGetter world, BlockPos pos) {
		return WorldHelper.getBlockEntity(world, pos, BackpackBlockEntity.class).map(te -> !te.getBackpackWrapper().getUpgradeHandler().getTypeWrappers(EverlastingUpgradeItem.TYPE).isEmpty()).orElse(false);
	}

	@SuppressWarnings("deprecation")
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return BackpackShapes.getShape(this, state.getValue(FACING), state.getValue(LEFT_TANK), state.getValue(RIGHT_TANK), state.getValue(BATTERY));
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return new BackpackBlockEntity(pPos, pState);
	}

	@SuppressWarnings("deprecation")
	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (world.isClientSide) {
			return InteractionResult.SUCCESS;
		}

		ItemStack heldItem = player.getItemInHand(hand);
		if (player.isShiftKeyDown() && heldItem.isEmpty()) {
			putInPlayersHandAndRemove(state, world, pos, player, hand);
			return InteractionResult.SUCCESS;
		}

		if (!heldItem.isEmpty() && FluidHelper.isFluidStorage(heldItem)) {
			WorldHelper.getBlockEntity(world, pos, BackpackBlockEntity.class)
				.flatMap(te -> te.getBackpackWrapper().getFluidHandler()).ifPresent(backpackFluidHandler -> FluidStorageUtil.interactWithFluidStorage(backpackFluidHandler, player, hand));

			return InteractionResult.SUCCESS;
		}

		BackpackContext.Block backpackContext = new BackpackContext.Block(pos);
		player.openMenu(MenuProviderHelper.createMenuProvider((w, p, pl) -> new BackpackContainer(w, pl, backpackContext), backpackContext,
				getBackpackDisplayName(world, pos)));
		return InteractionResult.SUCCESS;
	}

	private Component getBackpackDisplayName(Level world, BlockPos pos) {
		Component defaultDisplayName = new ItemStack(ModItems.BACKPACK).getHoverName();
		return WorldHelper.getBlockEntity(world, pos, BackpackBlockEntity.class).map(te -> te.getBackpackWrapper().getBackpack().getHoverName()).orElse(defaultDisplayName);
	}

	private static void putInPlayersHandAndRemove(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand) {
		ItemStack backpack = WorldHelper.getBlockEntity(world, pos, BackpackBlockEntity.class).map(te -> te.getBackpackWrapper().getBackpack()).orElse(ItemStack.EMPTY);
		stopBackpackSounds(backpack, world, pos);

		player.setItemInHand(hand, backpack.copy());
		player.getCooldowns().addCooldown(backpack.getItem(), 5);
		world.removeBlock(pos, false);

		SoundType soundType = state.getSoundType();
		world.playSound(null, pos, soundType.getBreakSound(), SoundSource.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			WorldHelper.getBlockEntity(level, pos, BackpackBlockEntity.class).ifPresent(IControllableStorage::removeFromController);
		}

		super.onRemove(state, level, pos, newState, isMoving);
	}

	@Override
	public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
		super.playerWillDestroy(level, pos, state, player);
		WorldHelper.getBlockEntity(level, pos, BackpackBlockEntity.class).ifPresent(IControllableStorage::removeFromController);
	}

	private static void stopBackpackSounds(ItemStack backpack, Level world, BlockPos pos) {
		BackpackWrapperLookup.get(backpack).flatMap(IStorageWrapper::getContentsUuid).ifPresent(uuid ->
				ServerStorageSoundHandler.stopPlayingDisc((ServerLevel) world, Vec3.atCenterOf(pos), uuid)
		);
	}

	public static InteractionResult playerInteract(Player player, Level level, InteractionHand hand, BlockHitResult hitResult) {
		BlockPos pos = hitResult.getBlockPos();

		if (!player.isShiftKeyDown() || !hasEmptyMainHandAndSomethingInOffhand(player) || didntInteractWithBackpack(player, level, hand, pos)) {
			return InteractionResult.PASS;
		}

		if (level.isClientSide) {
			return InteractionResult.SUCCESS;
		}

		BlockState state = level.getBlockState(pos);
		if (!(state.getBlock() instanceof BackpackBlock)) {
			return InteractionResult.PASS;
		}

		putInPlayersHandAndRemove(state, level, pos, player, player.getMainHandItem().isEmpty() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);

		return InteractionResult.SUCCESS;
	}

	@SuppressWarnings("unused")
	private static boolean didntInteractWithBackpack(Player player, Level world, InteractionHand hand, BlockPos pos) {
		return !(world.getBlockState(pos).getBlock() instanceof BackpackBlock);
	}

	private static boolean hasEmptyMainHandAndSomethingInOffhand(Player player) {
		return player.getMainHandItem().isEmpty() && !player.getOffhandItem().isEmpty();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
		super.entityInside(state, world, pos, entity);
		if (!world.isClientSide && entity instanceof ItemEntity itemEntity) {
			WorldHelper.getBlockEntity(world, pos, BackpackBlockEntity.class).ifPresent(te -> tryToPickup(world, itemEntity, te.getBackpackWrapper()));
		}
	}

	@Override
	public boolean canEntityDestroy(BlockState state, BlockGetter world, BlockPos pos, Entity entity) {
		if (hasEverlastingUpgrade(world, pos)) {
			return false;
		}
		return BlockInterfaceHelper.super.canEntityDestroy(state, world, pos, entity);
	}

	private void tryToPickup(Level world, ItemEntity itemEntity, IStorageWrapper w) {
		ItemStack remainingStack = itemEntity.getItem().copy();
		try (Transaction ctx = Transaction.openOuter()) {
			remainingStack = InventoryHelper.runPickupOnPickupResponseUpgrades(world, w.getUpgradeHandler(), remainingStack, ctx);
			ctx.commit();
		}
		if (remainingStack.getCount() < itemEntity.getItem().getCount()) {
			itemEntity.setItem(remainingStack);
		}
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
		return !pLevel.isClientSide ? createTickerHelper(pBlockEntityType, ModBlocks.BACKPACK_TILE_TYPE, (level, blockPos, blockState, backpackBlockEntity) -> BackpackBlockEntity.serverTick(level, blockPos, backpackBlockEntity)) : null;
	}

	@Nullable
	protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> typePassedIn, BlockEntityType<E> typeExpected, BlockEntityTicker<? super E> blockEntityTicker) {
		//noinspection unchecked
		return typeExpected == typePassedIn ? (BlockEntityTicker<A>) blockEntityTicker : null;
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource rand) {
		WorldHelper.getBlockEntity(level, pos, BackpackBlockEntity.class).ifPresent(te -> {
			RenderInfo renderInfo = te.getBackpackWrapper().getRenderInfo();
			renderUpgrades(level, rand, pos, state.getValue(FACING), renderInfo);
		});

	}

	private static void renderUpgrades(Level level, RandomSource rand, BlockPos pos, Direction facing, RenderInfo renderInfo) {
		if (Minecraft.getInstance().isPaused()) {
			return;
		}

		Map<UpgradeRenderDataType<?>, IUpgradeRenderData> upgradeRenderData = new HashMap<>(renderInfo.getUpgradeRenderData());
		upgradeRenderData.forEach((type, data) -> UpgradeRenderRegistry.getUpgradeRenderer(type).ifPresent(renderer -> renderUpgrade(renderer, level, rand, pos, facing, type, data)));
	}

	private static Vector3f getBackpackMiddleFacePoint(BlockPos pos, Direction facing, Vector3f vector) {
		Vector3f point = new Vector3f(vector);
		point.add(0, 0, 0.41f);
		point.rotate(Axis.YN.rotationDegrees(facing.toYRot()));
		point.add(pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f);
		return point;
	}

	private static <T extends IUpgradeRenderData> void renderUpgrade(IUpgradeRenderer<T> renderer, Level level, RandomSource rand, BlockPos pos, Direction facing, UpgradeRenderDataType<?> type, IUpgradeRenderData data) {
		//noinspection unchecked
		type.cast(data).ifPresent(renderData -> renderer.render(level, rand, vector -> getBackpackMiddleFacePoint(pos, facing, vector), (T) renderData));
	}
}