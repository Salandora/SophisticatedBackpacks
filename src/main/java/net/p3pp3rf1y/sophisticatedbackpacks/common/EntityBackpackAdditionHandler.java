package net.p3pp3rf1y.sophisticatedbackpacks.common;

import com.google.common.primitives.Ints;

import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.p3pp3rf1y.sophisticatedbackpacks.Config;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackStorage;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.IBackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems;
import net.p3pp3rf1y.sophisticatedbackpacks.mixin.common.accessor.RecordItemAccessor;
import net.p3pp3rf1y.sophisticatedcore.api.IStorageWrapper;
import net.p3pp3rf1y.sophisticatedcore.upgrades.jukebox.JukeboxUpgradeItem;
import net.p3pp3rf1y.sophisticatedcore.util.RandHelper;
import net.p3pp3rf1y.sophisticatedcore.util.WeightedElement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class EntityBackpackAdditionHandler {
	private static final int MAX_DIFFICULTY = 3;
	private static final float MAX_LOCAL_DIFFICULTY = 6.75f;

	private EntityBackpackAdditionHandler() {}

	private static final String SPAWNED_WITH_BACKPACK = "spawnedWithBackpack";
	private static final String SPAWNED_WITH_JUKEBOX_UPGRADE = SophisticatedBackpacks.ID + ":jukebox";

	private static final List<WeightedElement<Item>> HELMET_CHANCES = List.of(
			new WeightedElement<>(1, Items.NETHERITE_HELMET),
			new WeightedElement<>(3, Items.DIAMOND_HELMET),
			new WeightedElement<>(9, Items.GOLDEN_HELMET),
			new WeightedElement<>(27, Items.IRON_HELMET),
			new WeightedElement<>(81, Items.LEATHER_HELMET)
	);
	private static final List<WeightedElement<Item>> LEGGINGS_CHANCES = List.of(
			new WeightedElement<>(1, Items.NETHERITE_LEGGINGS),
			new WeightedElement<>(3, Items.DIAMOND_LEGGINGS),
			new WeightedElement<>(9, Items.GOLDEN_LEGGINGS),
			new WeightedElement<>(27, Items.IRON_LEGGINGS),
			new WeightedElement<>(81, Items.LEATHER_LEGGINGS)
	);
	private static final List<WeightedElement<Item>> BOOTS_CHANCES = List.of(
			new WeightedElement<>(1, Items.NETHERITE_BOOTS),
			new WeightedElement<>(3, Items.DIAMOND_BOOTS),
			new WeightedElement<>(9, Items.GOLDEN_BOOTS),
			new WeightedElement<>(27, Items.IRON_BOOTS),
			new WeightedElement<>(81, Items.LEATHER_BOOTS)
	);

	private static final Map<Item, Float> dropChanceMultiplier = Map.of(
			ModItems.BACKPACK, 1F,
			ModItems.IRON_BACKPACK, 1.5F,
			ModItems.GOLD_BACKPACK, 3F,
			ModItems.DIAMOND_BACKPACK, 4.5F,
			ModItems.NETHERITE_BACKPACK, 6F
	);

	private static final List<WeightedElement<BackpackAddition>> BACKPACK_CHANCES = List.of(
			new WeightedElement<>(1, new BackpackAddition(ModItems.NETHERITE_BACKPACK, 4,
					HELMET_CHANCES.subList(0, 1), LEGGINGS_CHANCES.subList(0, 1), BOOTS_CHANCES.subList(0, 1))),
			new WeightedElement<>(5, new BackpackAddition(ModItems.DIAMOND_BACKPACK, 3,
					HELMET_CHANCES.subList(0, 2), LEGGINGS_CHANCES.subList(0, 2), BOOTS_CHANCES.subList(0, 2))),
			new WeightedElement<>(25, new BackpackAddition(ModItems.GOLD_BACKPACK, 2,
					HELMET_CHANCES.subList(1, 3), LEGGINGS_CHANCES.subList(1, 3), BOOTS_CHANCES.subList(1, 3))),
			new WeightedElement<>(125, new BackpackAddition(ModItems.IRON_BACKPACK, 1,
					HELMET_CHANCES.subList(2, 4), LEGGINGS_CHANCES.subList(2, 4), BOOTS_CHANCES.subList(2, 4))),
			new WeightedElement<>(625, new BackpackAddition(ModItems.BACKPACK, 0,
					HELMET_CHANCES.subList(3, 5), LEGGINGS_CHANCES.subList(3, 5), BOOTS_CHANCES.subList(3, 5)))
	);

	private static final Map<Integer, List<WeightedElement<BackpackAddition>>> DIFFICULTY_BACKPACK_CHANCES = Map.of(
			0, BACKPACK_CHANCES,
			1, BACKPACK_CHANCES.subList(1, 5),
			2, BACKPACK_CHANCES.subList(2, 5)
	);

	static void addBackpack(Monster monster, LevelAccessor level, DifficultyInstance difficultyInstance) {
		RandomSource rnd = level.getRandom();
		if (!Config.SERVER.entityBackpackAdditions.canWearBackpack(monster.getType())
				|| rnd.nextInt((int) (1 / Config.SERVER.entityBackpackAdditions.chance.get())) != 0 || (monster instanceof Raider raider && raider.getCurrentRaid() != null)) {
			return;
		}

		//noinspection UnstableApiUsage
		int index = Ints.constrainToRange((int) Math.floor(DIFFICULTY_BACKPACK_CHANCES.size() / MAX_LOCAL_DIFFICULTY * difficultyInstance.getEffectiveDifficulty() - 0.1f), 0, DIFFICULTY_BACKPACK_CHANCES.size());

		RandHelper.getRandomWeightedElement(rnd, DIFFICULTY_BACKPACK_CHANCES.get(index)).ifPresent(backpackAddition -> {
			ItemStack backpack = new ItemStack(backpackAddition.getBackpackItem());
			int minDifficulty = backpackAddition.getMinDifficulty();
			int difficulty = Math.max(minDifficulty, rnd.nextInt(MAX_DIFFICULTY + 1));
			equipBackpack(monster, backpack, difficulty, Boolean.TRUE.equals(Config.SERVER.entityBackpackAdditions.playJukebox.get()) && rnd.nextInt(4) == 0, level, rnd);
			applyPotions(monster, difficulty, minDifficulty, rnd);
			raiseHealth(monster, minDifficulty);
			if (Boolean.TRUE.equals(Config.SERVER.entityBackpackAdditions.equipWithArmor.get())) {
				equipArmorPiece(monster, rnd, minDifficulty, backpackAddition.getHelmetChances(), EquipmentSlot.HEAD, level, difficultyInstance);
				equipArmorPiece(monster, rnd, minDifficulty, backpackAddition.getLeggingsChances(), EquipmentSlot.LEGS, level, difficultyInstance);
				equipArmorPiece(monster, rnd, minDifficulty, backpackAddition.getBootsChances(), EquipmentSlot.FEET, level, difficultyInstance);
			}
			monster.addTag(SPAWNED_WITH_BACKPACK);
		});
	}

	private static void equipArmorPiece(Monster monster, RandomSource rnd, int minDifficulty, List<WeightedElement<Item>> armorChances, EquipmentSlot slot, LevelAccessor level, DifficultyInstance difficultyInstance) {
		RandHelper.getRandomWeightedElement(rnd, armorChances).ifPresent(armorPiece -> {
			if (armorPiece != Items.AIR) {
				ItemStack armorStack = new ItemStack(armorPiece);
				if (rnd.nextInt(6 - minDifficulty) == 0) {
					float additionalDifficulty = difficultyInstance.getSpecialMultiplier();
					int enchantmentLevel = (int) (5F + additionalDifficulty * 18F + minDifficulty * 6);
					EnchantmentHelper.enchantItem(rnd, armorStack, enchantmentLevel, true);
				}
				monster.setItemSlot(slot, armorStack);
			}
		});
	}

	private static void equipBackpack(Monster monster, ItemStack backpack, int difficulty, boolean playMusicDisc, LevelAccessor level, RandomSource rnd) {
		getSpawnEgg(monster.getType()).ifPresent(egg -> BackpackWrapperLookup.get(backpack)
				.ifPresent(w -> {
					w.setColors(getPrimaryColor(egg), getSecondaryColor(egg));
					setLoot(monster, w, difficulty, level);
					if (playMusicDisc) {
						w.getInventoryHandler(); //just to assign uuid and real upgrade handler
						if (w.getUpgradeHandler().getSlotCount() > 0) {
							monster.addTag(SPAWNED_WITH_JUKEBOX_UPGRADE);
							addJukeboxUpgradeAndRandomDisc(w, rnd);
						}
					}
				}));
		monster.setItemSlot(EquipmentSlot.CHEST, backpack);
		monster.setDropChance(EquipmentSlot.CHEST, 0);
	}

	private static void addJukeboxUpgradeAndRandomDisc(IStorageWrapper w, RandomSource rnd) {
		w.getUpgradeHandler().setStackInSlot(0, new ItemStack(ModItems.JUKEBOX_UPGRADE));
		Iterator<JukeboxUpgradeItem.Wrapper> it = w.getUpgradeHandler().getTypeWrappers(JukeboxUpgradeItem.TYPE).iterator();
		if (it.hasNext()) {
			JukeboxUpgradeItem.Wrapper wrapper = it.next();
			List<RecordItem> musicDiscs = getMusicDiscs();
			wrapper.setDisc(new ItemStack(musicDiscs.get(rnd.nextInt(musicDiscs.size()))));
		}
	}

	private static List<RecordItem> musicDiscs = null;

	private static List<RecordItem> getMusicDiscs() {
		if (musicDiscs == null) {
			Map<SoundEvent, RecordItem> records = RecordItemAccessor.getByName();
			if (records == null) {
				musicDiscs = new ArrayList<>();
			} else {
				Set<String> blockedDiscs = new HashSet<>(Config.SERVER.entityBackpackAdditions.discBlockList.get());
				musicDiscs = new ArrayList<>();
				records.forEach((sound, musicDisc) -> {
					//noinspection ConstantConditions - by this point the disc has registry name
					if (!blockedDiscs.contains(BuiltInRegistries.ITEM.getKey(musicDisc).toString())) {
						musicDiscs.add(musicDisc);
					}
				});
			}
		}

		return musicDiscs;
	}

	private static void raiseHealth(Monster monster, int minDifficulty) {
		if (Boolean.FALSE.equals(Config.SERVER.entityBackpackAdditions.buffHealth.get())) {
			return;
		}
		AttributeInstance maxHealth = monster.getAttribute(Attributes.MAX_HEALTH);
		if (maxHealth != null) {
			double healthAddition = maxHealth.getBaseValue() * minDifficulty;
			if (healthAddition > 0.1D) {
				maxHealth.addPermanentModifier(new AttributeModifier("Backpack bearer health bonus", healthAddition, AttributeModifier.Operation.ADDITION));
			}
			monster.setHealth(monster.getMaxHealth());
		}
	}

	private static Optional<SpawnEggItem> getSpawnEgg(EntityType<?> entityType) {
		return Optional.ofNullable(SpawnEggItem.byId(entityType));
	}

	private static int getPrimaryColor(SpawnEggItem egg) {
		Integer primaryColor = egg.getColor(0);
		return primaryColor == null ? -1 : primaryColor;
	}

	private static int getSecondaryColor(SpawnEggItem egg) {
		Integer secondaryColor = egg.getColor(1);
		return secondaryColor == null ? -1 : secondaryColor;
	}

	private static final List<ApplicableEffect> APPLICABLE_EFFECTS = List.of(
			new ApplicableEffect(List.of(MobEffects.DAMAGE_RESISTANCE, MobEffects.REGENERATION), 1),
			new ApplicableEffect(MobEffects.FIRE_RESISTANCE),
			new ApplicableEffect(MobEffects.ABSORPTION),
			new ApplicableEffect(MobEffects.HEALTH_BOOST),
			new ApplicableEffect(MobEffects.MOVEMENT_SPEED),
			new ApplicableEffect(MobEffects.DAMAGE_BOOST));

	private static void setLoot(Monster monster, IBackpackWrapper backpackWrapper, int difficulty, LevelAccessor level) {
		MinecraftServer server = level.getServer();
		if (server == null) {
			return;
		}

		if (Boolean.TRUE.equals(Config.SERVER.entityBackpackAdditions.addLoot.get())) {
			addLoot(monster, backpackWrapper, difficulty);
		}
	}

	private static void applyPotions(Monster monster, int difficulty, int minDifficulty, RandomSource rnd) {
		if (Boolean.TRUE.equals(Config.SERVER.entityBackpackAdditions.buffWithPotionEffects.get())) {
			RandHelper.getNRandomElements(APPLICABLE_EFFECTS, difficulty + 2)
					.forEach(applicableEffect -> {
						int amplifier = Math.min(Math.max(minDifficulty, rnd.nextInt(difficulty + 1)), applicableEffect.getMaxAmplifier());
						monster.addEffect(new MobEffectInstance(applicableEffect.getRandomEffect(rnd), 30 * 60 * 20, amplifier));
					});
		}
	}

	private static void addLoot(Monster monster, IBackpackWrapper backpackWrapper, int difficulty) {
		if (difficulty != 0) {
			Config.SERVER.entityBackpackAdditions.getLootTableName(monster.getType()).ifPresent(lootTableName -> {
				float lootPercentage = (float) difficulty / MAX_DIFFICULTY;
				backpackWrapper.setLoot(lootTableName, lootPercentage);
			});
		}
	}

	@SuppressWarnings("unused")
	static boolean handleBackpackDrop(LivingEntity target, DamageSource source, Collection<ItemEntity> drops, int lootingLevel, boolean recentlyHit) {
		if (target.getTags().contains(SPAWNED_WITH_BACKPACK)) {
			ItemStack backpack = target.getItemBySlot(EquipmentSlot.CHEST);
			Config.Server.EntityBackpackAdditionsConfig additionsConfig = Config.SERVER.entityBackpackAdditions;
			if (shouldDropBackpack(source, additionsConfig, target, backpack, lootingLevel)) {
				ItemEntity backpackEntity = new ItemEntity(target.level(), target.getX(), target.getY(), target.getZ(), backpack);
				drops.add(backpackEntity);
				target.setItemSlot(EquipmentSlot.CHEST, ItemStack.EMPTY);
				target.getTags().remove(SPAWNED_WITH_BACKPACK);
			} else {
				removeContentsUuid(backpack);
			}
		}
		return false;
	}

	private static boolean shouldDropBackpack(DamageSource source, Config.Server.EntityBackpackAdditionsConfig additionsConfig, LivingEntity mob, ItemStack backpack, int lootingLevel) {
		if (!(source.getEntity() instanceof Player)) {
			return false;
		}
		if (!Boolean.TRUE.equals(additionsConfig.dropToFakePlayers.get()) && source.getEntity() instanceof FakePlayer) {
			return false;
		}
		float lootingChanceMultiplier = dropChanceMultiplier.getOrDefault(backpack.getItem(), 1F);
		return mob.level().random.nextFloat() < (additionsConfig.backpackDropChance.get() + lootingLevel * additionsConfig.lootingChanceIncreasePerLevel.get()) * lootingChanceMultiplier;
	}

	public static void removeBeneficialEffects(Creeper creeper) {
		if (creeper.getTags().contains(SPAWNED_WITH_BACKPACK)) {
			creeper.getActiveEffects().removeIf(e -> e.getEffect().isBeneficial());
		}
	}

	public static void removeBackpackUuid(Monster entity, Level level) {
		if (level.isClientSide() || (entity.getRemovalReason() != Entity.RemovalReason.KILLED && entity.getRemovalReason() != Entity.RemovalReason.DISCARDED) || !entity.getTags().contains(SPAWNED_WITH_BACKPACK)) {
			return;
		}

		ItemStack stack = entity.getItemBySlot(EquipmentSlot.CHEST);
		removeContentsUuid(stack);
	}

	private static void removeContentsUuid(ItemStack stack) {
		BackpackWrapperLookup.get(stack).flatMap(IStorageWrapper::getContentsUuid).ifPresent(uuid -> BackpackStorage.get().removeBackpackContents(uuid));
	}

	public static void onLivingUpdate(LivingEntity entity) {
		if (!entity.getTags().contains(SPAWNED_WITH_JUKEBOX_UPGRADE)) {
			return;
		}
		BackpackWrapperLookup.get(entity.getItemBySlot(EquipmentSlot.CHEST))
				.ifPresent(backpackWrapper -> backpackWrapper.getUpgradeHandler().getTypeWrappers(JukeboxUpgradeItem.TYPE).forEach(wrapper -> {
					if (wrapper.isPlaying()) {
						wrapper.tick(entity, entity.level(), entity.blockPosition());
					} else {
						wrapper.play(entity);
					}
				}));
	}

	private record BackpackAddition(Item backpackItem, int minDifficulty,
									List<WeightedElement<Item>> helmetChances,
									List<WeightedElement<Item>> leggingsChances,
									List<WeightedElement<Item>> bootsChances) {
		public List<WeightedElement<Item>> getHelmetChances() {
			return helmetChances;
		}

		public List<WeightedElement<Item>> getLeggingsChances() {
			return leggingsChances;
		}

		public List<WeightedElement<Item>> getBootsChances() {
			return bootsChances;
		}

		public Item getBackpackItem() {
			return backpackItem;
		}

		public int getMinDifficulty() {
			return minDifficulty;
		}
	}

	private static class ApplicableEffect {
		private final List<MobEffect> effects;

		private final int maxAmplifier;

		private ApplicableEffect(MobEffect effect) {
			this(List.of(effect), Integer.MAX_VALUE);
		}

		private ApplicableEffect(List<MobEffect> effects, int maxAmplifier) {
			this.effects = effects;
			this.maxAmplifier = maxAmplifier;
		}

		public MobEffect getRandomEffect(RandomSource random) {
			return effects.get(random.nextInt(effects.size()));
		}

		public int getMaxAmplifier() {
			return maxAmplifier;
		}
	}
}
