package net.p3pp3rf1y.sophisticatedbackpacks.common;

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.p3pp3rf1y.sophisticatedbackpacks.Config;
import net.p3pp3rf1y.sophisticatedbackpacks.api.IAttackEntityResponseUpgrade;
import net.p3pp3rf1y.sophisticatedbackpacks.api.IBlockClickResponseUpgrade;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.IBackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModBlocks;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems;
import net.p3pp3rf1y.sophisticatedbackpacks.network.AnotherPlayerBackpackOpenMessage;
import net.p3pp3rf1y.sophisticatedbackpacks.network.SBPPacketHandler;
import net.p3pp3rf1y.sophisticatedbackpacks.settings.BackpackMainSettingsCategory;
import net.p3pp3rf1y.sophisticatedbackpacks.util.PlayerInventoryProvider;
import net.p3pp3rf1y.sophisticatedcore.event.common.EntityEvents;
import net.p3pp3rf1y.sophisticatedcore.event.common.ItemEntityEvents;
import net.p3pp3rf1y.sophisticatedcore.event.common.LivingEntityEvents;
import net.p3pp3rf1y.sophisticatedcore.event.common.MobSpawnEvents;
import net.p3pp3rf1y.sophisticatedcore.network.PacketHandler;
import net.p3pp3rf1y.sophisticatedcore.network.SyncPlayerSettingsMessage;
import net.p3pp3rf1y.sophisticatedcore.settings.SettingsManager;
import net.p3pp3rf1y.sophisticatedcore.upgrades.jukebox.ServerStorageSoundHandler;
import net.p3pp3rf1y.sophisticatedcore.util.InventoryHelper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nullable;

public class CommonEventHandler {
	public void registerHandlers() {
		ModBlocks.registerEvents();
		ModItems.register();

		ItemEntityEvents.CAN_PICKUP.register(this::onItemPickup);

		MobSpawnEvents.AFTER_FINALIZE_SPAWN.register(this::onLivingSpecialSpawn);
		LivingEntityEvents.DROPS.register(EntityBackpackAdditionHandler::handleBackpackDrop);

		EntityTrackingEvents.STOP_TRACKING.register(this::onEntityLeaveWorld);
		ServerTickEvents.END_WORLD_TICK.register(ServerStorageSoundHandler::tick);
		AttackBlockCallback.EVENT.register(this::onBlockClick);
		AttackEntityCallback.EVENT.register(this::onAttackEntity);
		LivingEntityEvents.TICK.register(EntityBackpackAdditionHandler::onLivingUpdate);
		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(this::onPlayerLoggedIn);
		ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register(this::onPlayerChangedDimension);
		ServerPlayerEvents.AFTER_RESPAWN.register(this::onPlayerRespawn);
		ServerTickEvents.END_WORLD_TICK.register(this::onWorldTick);
		UseEntityCallback.EVENT.register(this::interactWithEntity);

		EntityEvents.ON_JOIN_WORLD.register((entity, world, loadedFromDisk) -> {
			if (entity.getClass().equals(ItemEntity.class) && ((ItemEntity)entity).getItem().getItem() instanceof BackpackItem backpack) {
				Entity newEntity = backpack.createCustomEntity(world, entity, ((ItemEntity)entity).getItem());
				if (newEntity != null) {
					entity.discard();
					world.addFreshEntity(newEntity);
					return false;
				}
			}
			return true;
		});
	}

	private static final int BACKPACK_CHECK_COOLDOWN = 40;
	private final Map<ResourceLocation, Long> nextBackpackCheckTime = new HashMap<>();

	InteractionResult interactWithEntity(Player player, Level world, InteractionHand hand, Entity entity, @Nullable EntityHitResult hitResult) {
		if (!(entity instanceof Player targetPlayer) || hitResult == null || Boolean.FALSE.equals(Config.COMMON.allowOpeningOtherPlayerBackpacks.get())) {
			return InteractionResult.PASS;
		}

		Vec3 targetPlayerViewVector = Vec3.directionFromRotation(new Vec2(targetPlayer.getXRot(), targetPlayer.yBodyRot));

		Vec3 hitVector = hitResult.getLocation();
		Vec3 vec31 = player.position().vectorTo(targetPlayer.position()).normalize();
		vec31 = new Vec3(vec31.x, 0.0D, vec31.z);
		boolean isPointingAtBody = hitVector.y >= 0.9D && hitVector.y < 1.6D;
		boolean isPointingAtBack = vec31.dot(targetPlayerViewVector) > 0.0D;
		if (!isPointingAtBody || !isPointingAtBack) {
			return InteractionResult.PASS;
		}

		if (targetPlayer.getLevel().isClientSide) {
			SBPPacketHandler.sendToServer(new AnotherPlayerBackpackOpenMessage(targetPlayer.getId()));
			return InteractionResult.SUCCESS;
		}

		return InteractionResult.PASS;
	}

	private void onWorldTick(ServerLevel level) {
		ResourceLocation dimensionKey = level.dimension().location();
		boolean runSlownessLogic = Boolean.TRUE.equals(Config.COMMON.nerfsConfig.tooManyBackpacksSlowness.get());
		boolean runDedupeLogic = Boolean.FALSE.equals(Config.COMMON.tickDedupeLogicDisabled.get());
		if ((!runSlownessLogic && !runDedupeLogic)
				|| nextBackpackCheckTime.getOrDefault(dimensionKey, 0L) > level.getGameTime()) {
			return;
		}
		nextBackpackCheckTime.put(dimensionKey, level.getGameTime() + BACKPACK_CHECK_COOLDOWN);

		Set<UUID> backpackIds = new HashSet<>();

		level.players().forEach(player -> {
			AtomicInteger numberOfBackpacks = new AtomicInteger(0);
			PlayerInventoryProvider.get().runOnBackpacks(player, (backpack, handlerName, identifier, slot) -> {
				if (runSlownessLogic) {
					numberOfBackpacks.incrementAndGet();
				}
				if (runDedupeLogic) {
					BackpackWrapperLookup.get(backpack).ifPresent(backpackWrapper ->
							addBackpackIdIfUniqueOrDedupe(backpackIds, backpackWrapper));
				}
				return false;
			});
			if (runSlownessLogic) {
				int maxNumberOfBackpacks = Config.COMMON.nerfsConfig.maxNumberOfBackpacks.get();
				if (numberOfBackpacks.get() > maxNumberOfBackpacks) {
					int numberOfSlownessLevels = Math.min(10, (int) Math.ceil((numberOfBackpacks.get() - maxNumberOfBackpacks) * Config.COMMON.nerfsConfig.slownessLevelsPerAdditionalBackpack.get()));
					player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, BACKPACK_CHECK_COOLDOWN * 2, numberOfSlownessLevels - 1, false, false));
				}
			}
		});
	}

	private static void addBackpackIdIfUniqueOrDedupe(Set<UUID> backpackIds, IBackpackWrapper backpackWrapper) {
		backpackWrapper.getContentsUuid().ifPresent(backpackId -> {
			if (backpackIds.contains(backpackId)) {
				backpackWrapper.removeContentsUUIDTag();
				backpackWrapper.onContentsNbtUpdated();
			} else {
				backpackIds.add(backpackId);
			}
		});
	}

	private void onPlayerChangedDimension(ServerPlayer player, ServerLevel origin, ServerLevel destination) {
		sendPlayerSettingsToClient(player);
	}

	private void onPlayerLoggedIn(Player player, boolean joined) {
		if (joined) {
			sendPlayerSettingsToClient(player);
		}
	}

	private void sendPlayerSettingsToClient(Player player) {
		String playerTagName = BackpackMainSettingsCategory.SOPHISTICATED_BACKPACK_SETTINGS_PLAYER_TAG;
		PacketHandler.sendToClient((ServerPlayer) player, new SyncPlayerSettingsMessage(playerTagName, SettingsManager.getPlayerSettingsTag(player, playerTagName)));
	}

	private void onPlayerRespawn(ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean alive) {
		sendPlayerSettingsToClient(newPlayer);
	}

	private InteractionResult onBlockClick(Player player, Level world, InteractionHand hand, BlockPos pos, Direction direction) {
		if (world.isClientSide) {
			return InteractionResult.PASS;
		}

		PlayerInventoryProvider.get().runOnBackpacks(player, (backpack, inventoryHandlerName, identifier, slot) -> BackpackWrapperLookup.get(backpack)
				.map(wrapper -> {
					for (IBlockClickResponseUpgrade upgrade : wrapper.getUpgradeHandler().getWrappersThatImplement(IBlockClickResponseUpgrade.class)) {
						if (upgrade.onBlockClick(player, pos)) {
							return true;
						}
					}
					return false;
				}).orElse(false));

		return InteractionResult.PASS;
	}

	private InteractionResult onAttackEntity(Player player, Level level, InteractionHand hand, Entity entity, @Nullable EntityHitResult hitResult) {
		if (level.isClientSide) {
			return InteractionResult.PASS;
		}

		PlayerInventoryProvider.get().runOnBackpacks(player, (backpack, inventoryHandlerName, identifier, slot) -> BackpackWrapperLookup.get(backpack)
				.map(wrapper -> {
					for (IAttackEntityResponseUpgrade upgrade : wrapper.getUpgradeHandler().getWrappersThatImplement(IAttackEntityResponseUpgrade.class)) {
						if (upgrade.onAttackEntity(player)) {
							return true;
						}
					}
					return false;
				}).orElse(false));

		return InteractionResult.PASS;
	}

	private void onLivingSpecialSpawn(MobSpawnEvents.FinalizeSpawn event) {
		Entity entity = event.getEntity();
		if (entity instanceof Monster monster && monster.getItemBySlot(EquipmentSlot.CHEST).isEmpty()) {
			EntityBackpackAdditionHandler.addBackpack(monster, event.getLevel(), event.getDifficulty());
		}
	}

	private void onEntityLeaveWorld(Entity trackedEntity, ServerPlayer player) {
		if (!(trackedEntity instanceof Monster monster)) {
			return;
		}
		EntityBackpackAdditionHandler.removeBackpackUuid(monster, player.getLevel());
	}

	private InteractionResult onItemPickup(Player player, ItemEntity itemEntity, ItemStack stack) {
		if (itemEntity.pickupDelay != 0 || (itemEntity.getThrower() != null && !itemEntity.getThrower().equals(player.getUUID())) || stack.isEmpty()) {
			return InteractionResult.PASS;
		}

		Level world = player.getCommandSenderWorld();

		AtomicReference<ItemStack> remainingStack = new AtomicReference<>(stack.copy());
		try(Transaction ctx = Transaction.openOuter()) {
			PlayerInventoryProvider.get().runOnBackpacks(player, (backpack, inventoryHandlerName, identifier, slot) -> BackpackWrapperLookup.get(backpack)
					.map(wrapper -> {
						remainingStack.set(InventoryHelper.runPickupOnPickupResponseUpgrades(world, player, wrapper.getUpgradeHandler(), remainingStack.get(), ctx));
						return remainingStack.get().isEmpty();
					}).orElse(false), Config.COMMON.nerfsConfig.onlyWornBackpackTriggersUpgrades.get()
			);

			if (remainingStack.get().getCount() != stack.getCount()) {
				itemEntity.setItem(remainingStack.get());
				ctx.commit();
				return InteractionResult.SUCCESS;
			}
		}

		return InteractionResult.PASS;
	}
}
