package net.p3pp3rf1y.sophisticatedbackpacks.init;

import com.mojang.serialization.Codec;
import team.reborn.energy.api.EnergyStorage;

import io.github.fabricators_of_create.porting_lib.loot.IGlobalLootModifier;
import io.github.fabricators_of_create.porting_lib.loot.PortingLibLoot;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemItemStorages;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.crafting.SmokingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.p3pp3rf1y.sophisticatedbackpacks.Config;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.IBackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.BackpackContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.BackpackSettingsContainerMenu;
import net.p3pp3rf1y.sophisticatedbackpacks.crafting.BackpackDyeRecipe;
import net.p3pp3rf1y.sophisticatedbackpacks.crafting.BackpackUpgradeRecipe;
import net.p3pp3rf1y.sophisticatedbackpacks.crafting.BasicBackpackRecipe;
import net.p3pp3rf1y.sophisticatedbackpacks.crafting.SmithingBackpackUpgradeRecipe;
import net.p3pp3rf1y.sophisticatedbackpacks.data.CopyBackpackDataFunction;
import net.p3pp3rf1y.sophisticatedbackpacks.data.SBLootEnabledCondition;
import net.p3pp3rf1y.sophisticatedbackpacks.data.SBLootModifierProvider;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.anvil.AnvilUpgradeContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.anvil.AnvilUpgradeItem;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.anvil.AnvilUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.deposit.DepositUpgradeContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.deposit.DepositUpgradeItem;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.deposit.DepositUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.everlasting.EverlastingBackpackItemEntity;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.everlasting.EverlastingUpgradeItem;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.inception.InceptionUpgradeContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.inception.InceptionUpgradeItem;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.inception.InceptionUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.refill.RefillUpgradeContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.refill.RefillUpgradeItem;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.refill.RefillUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.restock.RestockUpgradeItem;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.restock.RestockUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.toolswapper.ToolSwapperUpgradeContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.toolswapper.ToolSwapperUpgradeItem;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.toolswapper.ToolSwapperUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedcore.api.IStorageWrapper;
import net.p3pp3rf1y.sophisticatedcore.common.gui.UpgradeContainerRegistry;
import net.p3pp3rf1y.sophisticatedcore.common.gui.UpgradeContainerType;
import net.p3pp3rf1y.sophisticatedcore.upgrades.ContentsFilteredUpgradeContainer;
import net.p3pp3rf1y.sophisticatedcore.upgrades.battery.BatteryUpgradeContainer;
import net.p3pp3rf1y.sophisticatedcore.upgrades.battery.BatteryUpgradeItem;
import net.p3pp3rf1y.sophisticatedcore.upgrades.battery.BatteryUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedcore.upgrades.compacting.CompactingUpgradeContainer;
import net.p3pp3rf1y.sophisticatedcore.upgrades.compacting.CompactingUpgradeItem;
import net.p3pp3rf1y.sophisticatedcore.upgrades.compacting.CompactingUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedcore.upgrades.cooking.AutoBlastingUpgradeItem;
import net.p3pp3rf1y.sophisticatedcore.upgrades.cooking.AutoCookingUpgradeContainer;
import net.p3pp3rf1y.sophisticatedcore.upgrades.cooking.AutoCookingUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedcore.upgrades.cooking.AutoSmeltingUpgradeItem;
import net.p3pp3rf1y.sophisticatedcore.upgrades.cooking.AutoSmokingUpgradeItem;
import net.p3pp3rf1y.sophisticatedcore.upgrades.cooking.BlastingUpgradeItem;
import net.p3pp3rf1y.sophisticatedcore.upgrades.cooking.CookingUpgradeContainer;
import net.p3pp3rf1y.sophisticatedcore.upgrades.cooking.CookingUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedcore.upgrades.cooking.SmeltingUpgradeItem;
import net.p3pp3rf1y.sophisticatedcore.upgrades.cooking.SmokingUpgradeItem;
import net.p3pp3rf1y.sophisticatedcore.upgrades.crafting.CraftingUpgradeContainer;
import net.p3pp3rf1y.sophisticatedcore.upgrades.crafting.CraftingUpgradeItem;
import net.p3pp3rf1y.sophisticatedcore.upgrades.crafting.CraftingUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedcore.upgrades.feeding.FeedingUpgradeContainer;
import net.p3pp3rf1y.sophisticatedcore.upgrades.feeding.FeedingUpgradeItem;
import net.p3pp3rf1y.sophisticatedcore.upgrades.feeding.FeedingUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedcore.upgrades.filter.FilterUpgradeContainer;
import net.p3pp3rf1y.sophisticatedcore.upgrades.filter.FilterUpgradeItem;
import net.p3pp3rf1y.sophisticatedcore.upgrades.jukebox.JukeboxUpgradeContainer;
import net.p3pp3rf1y.sophisticatedcore.upgrades.jukebox.JukeboxUpgradeItem;
import net.p3pp3rf1y.sophisticatedcore.upgrades.magnet.MagnetUpgradeContainer;
import net.p3pp3rf1y.sophisticatedcore.upgrades.magnet.MagnetUpgradeItem;
import net.p3pp3rf1y.sophisticatedcore.upgrades.magnet.MagnetUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedcore.upgrades.pickup.PickupUpgradeItem;
import net.p3pp3rf1y.sophisticatedcore.upgrades.pickup.PickupUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedcore.upgrades.pump.PumpUpgradeContainer;
import net.p3pp3rf1y.sophisticatedcore.upgrades.pump.PumpUpgradeItem;
import net.p3pp3rf1y.sophisticatedcore.upgrades.pump.PumpUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedcore.upgrades.stack.StackUpgradeItem;
import net.p3pp3rf1y.sophisticatedcore.upgrades.stonecutter.StonecutterUpgradeContainer;
import net.p3pp3rf1y.sophisticatedcore.upgrades.stonecutter.StonecutterUpgradeItem;
import net.p3pp3rf1y.sophisticatedcore.upgrades.stonecutter.StonecutterUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedcore.upgrades.tank.TankUpgradeContainer;
import net.p3pp3rf1y.sophisticatedcore.upgrades.tank.TankUpgradeItem;
import net.p3pp3rf1y.sophisticatedcore.upgrades.tank.TankUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedcore.upgrades.voiding.VoidUpgradeContainer;
import net.p3pp3rf1y.sophisticatedcore.upgrades.voiding.VoidUpgradeItem;
import net.p3pp3rf1y.sophisticatedcore.upgrades.voiding.VoidUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedcore.upgrades.xppump.XpPumpUpgradeContainer;
import net.p3pp3rf1y.sophisticatedcore.upgrades.xppump.XpPumpUpgradeItem;
import net.p3pp3rf1y.sophisticatedcore.upgrades.xppump.XpPumpUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedcore.util.ItemBase;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ModItems {
	static List<Item> ITEMS = new ArrayList<>(); // Must be up here!

	private ModItems() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	// ITEMS
	//
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static final BackpackItem BACKPACK = register("backpack",
			() -> new BackpackItem(Config.SERVER.leatherBackpack.inventorySlotCount::get, Config.SERVER.leatherBackpack.upgradeSlotCount::get, () -> ModBlocks.BACKPACK));
	public static final BackpackItem COPPER_BACKPACK = register("copper_backpack",
			() -> new BackpackItem(Config.SERVER.copperBackpack.inventorySlotCount::get, Config.SERVER.copperBackpack.upgradeSlotCount::get, () -> ModBlocks.COPPER_BACKPACK));
	public static final BackpackItem IRON_BACKPACK = register("iron_backpack",
			() -> new BackpackItem(Config.SERVER.ironBackpack.inventorySlotCount::get, Config.SERVER.ironBackpack.upgradeSlotCount::get, () -> ModBlocks.IRON_BACKPACK));
	public static final BackpackItem GOLD_BACKPACK = register("gold_backpack",
			() -> new BackpackItem(Config.SERVER.goldBackpack.inventorySlotCount::get, Config.SERVER.goldBackpack.upgradeSlotCount::get, () -> ModBlocks.GOLD_BACKPACK));
	public static final BackpackItem DIAMOND_BACKPACK = register("diamond_backpack",
			() -> new BackpackItem(Config.SERVER.diamondBackpack.inventorySlotCount::get, Config.SERVER.diamondBackpack.upgradeSlotCount::get, () -> ModBlocks.DIAMOND_BACKPACK));
	public static final BackpackItem NETHERITE_BACKPACK = register("netherite_backpack",
			() -> new BackpackItem(Config.SERVER.netheriteBackpack.inventorySlotCount::get, Config.SERVER.netheriteBackpack.upgradeSlotCount::get, () -> ModBlocks.NETHERITE_BACKPACK, Item.Properties::fireResistant));

	public static final BackpackItem[] BACKPACKS = new BackpackItem[] { BACKPACK, COPPER_BACKPACK, IRON_BACKPACK, GOLD_BACKPACK, DIAMOND_BACKPACK, NETHERITE_BACKPACK };

	public static final ResourceLocation BACKPACK_UPGRADE_TAG_NAME = new ResourceLocation(SophisticatedBackpacks.MOD_ID, "upgrade");

	public static final TagKey<Item> BACKPACK_UPGRADE_TAG = TagKey.create(Registries.ITEM, BACKPACK_UPGRADE_TAG_NAME);

	public static final PickupUpgradeItem PICKUP_UPGRADE = register("pickup_upgrade",
			() -> new PickupUpgradeItem(Config.SERVER.pickupUpgrade.filterSlots::get, Config.SERVER.maxUpgradesPerStorage));
	public static final PickupUpgradeItem ADVANCED_PICKUP_UPGRADE = register("advanced_pickup_upgrade",
			() -> new PickupUpgradeItem(Config.SERVER.advancedPickupUpgrade.filterSlots::get, Config.SERVER.maxUpgradesPerStorage));
	public static final FilterUpgradeItem FILTER_UPGRADE = register("filter_upgrade",
			() -> new FilterUpgradeItem(Config.SERVER.filterUpgrade.filterSlots::get, Config.SERVER.maxUpgradesPerStorage));
	public static final FilterUpgradeItem ADVANCED_FILTER_UPGRADE = register("advanced_filter_upgrade",
			() -> new FilterUpgradeItem(Config.SERVER.advancedFilterUpgrade.filterSlots::get, Config.SERVER.maxUpgradesPerStorage));
	public static final MagnetUpgradeItem MAGNET_UPGRADE = register("magnet_upgrade",
			() -> new MagnetUpgradeItem(Config.SERVER.magnetUpgrade.magnetRange::get, Config.SERVER.magnetUpgrade.filterSlots::get, Config.SERVER.maxUpgradesPerStorage));
	public static final MagnetUpgradeItem ADVANCED_MAGNET_UPGRADE = register("advanced_magnet_upgrade",
			() -> new MagnetUpgradeItem(Config.SERVER.advancedMagnetUpgrade.magnetRange::get, Config.SERVER.advancedMagnetUpgrade.filterSlots::get, Config.SERVER.maxUpgradesPerStorage));
	public static final FeedingUpgradeItem FEEDING_UPGRADE = register("feeding_upgrade",
			() -> new FeedingUpgradeItem(Config.SERVER.feedingUpgrade.filterSlots::get, Config.SERVER.maxUpgradesPerStorage));
	public static final FeedingUpgradeItem ADVANCED_FEEDING_UPGRADE = register("advanced_feeding_upgrade",
			() -> new FeedingUpgradeItem(Config.SERVER.advancedFeedingUpgrade.filterSlots::get, Config.SERVER.maxUpgradesPerStorage));
	public static final CompactingUpgradeItem COMPACTING_UPGRADE = register("compacting_upgrade",
			() -> new CompactingUpgradeItem(false, Config.SERVER.compactingUpgrade.filterSlots::get, Config.SERVER.maxUpgradesPerStorage));
	public static final CompactingUpgradeItem ADVANCED_COMPACTING_UPGRADE = register("advanced_compacting_upgrade",
			() -> new CompactingUpgradeItem(true, Config.SERVER.advancedCompactingUpgrade.filterSlots::get, Config.SERVER.maxUpgradesPerStorage));
	public static final VoidUpgradeItem VOID_UPGRADE = register("void_upgrade",
			() -> new VoidUpgradeItem(Config.SERVER.voidUpgrade, Config.SERVER.maxUpgradesPerStorage));
	public static final VoidUpgradeItem ADVANCED_VOID_UPGRADE = register("advanced_void_upgrade",
			() -> new VoidUpgradeItem(Config.SERVER.advancedVoidUpgrade, Config.SERVER.maxUpgradesPerStorage));
	public static final RestockUpgradeItem RESTOCK_UPGRADE = register("restock_upgrade",
			() -> new RestockUpgradeItem(Config.SERVER.restockUpgrade.filterSlots::get));
	public static final RestockUpgradeItem ADVANCED_RESTOCK_UPGRADE = register("advanced_restock_upgrade",
			() -> new RestockUpgradeItem(Config.SERVER.advancedRestockUpgrade.filterSlots::get));
	public static final DepositUpgradeItem DEPOSIT_UPGRADE = register("deposit_upgrade",
			() -> new DepositUpgradeItem(Config.SERVER.depositUpgrade.filterSlots::get));
	public static final DepositUpgradeItem ADVANCED_DEPOSIT_UPGRADE = register("advanced_deposit_upgrade",
			() -> new DepositUpgradeItem(Config.SERVER.advancedDepositUpgrade.filterSlots::get));
	public static final RefillUpgradeItem REFILL_UPGRADE = register("refill_upgrade",
			() -> new RefillUpgradeItem(Config.SERVER.refillUpgrade.filterSlots::get, false, false));
	public static final RefillUpgradeItem ADVANCED_REFILL_UPGRADE = register("advanced_refill_upgrade",
			() -> new RefillUpgradeItem(Config.SERVER.advancedRefillUpgrade.filterSlots::get, true, true));
	public static final InceptionUpgradeItem INCEPTION_UPGRADE = register("inception_upgrade",
			InceptionUpgradeItem::new);
	public static final EverlastingUpgradeItem EVERLASTING_UPGRADE = register("everlasting_upgrade",
			EverlastingUpgradeItem::new);
	public static final SmeltingUpgradeItem SMELTING_UPGRADE = register("smelting_upgrade",
			() -> new SmeltingUpgradeItem(Config.SERVER.smeltingUpgrade, Config.SERVER.maxUpgradesPerStorage));
	public static final AutoSmeltingUpgradeItem AUTO_SMELTING_UPGRADE = register("auto_smelting_upgrade",
			() -> new AutoSmeltingUpgradeItem(Config.SERVER.autoSmeltingUpgrade, Config.SERVER.maxUpgradesPerStorage));
	public static final SmokingUpgradeItem SMOKING_UPGRADE = register("smoking_upgrade",
			() -> new SmokingUpgradeItem(Config.SERVER.smokingUpgrade, Config.SERVER.maxUpgradesPerStorage));
	public static final AutoSmokingUpgradeItem AUTO_SMOKING_UPGRADE = register("auto_smoking_upgrade",
			() -> new AutoSmokingUpgradeItem(Config.SERVER.autoSmokingUpgrade, Config.SERVER.maxUpgradesPerStorage));
	public static final BlastingUpgradeItem BLASTING_UPGRADE = register("blasting_upgrade",
			() -> new BlastingUpgradeItem(Config.SERVER.blastingUpgrade, Config.SERVER.maxUpgradesPerStorage));
	public static final AutoBlastingUpgradeItem AUTO_BLASTING_UPGRADE = register("auto_blasting_upgrade",
			() -> new AutoBlastingUpgradeItem(Config.SERVER.autoBlastingUpgrade, Config.SERVER.maxUpgradesPerStorage));
	public static final CraftingUpgradeItem CRAFTING_UPGRADE = register("crafting_upgrade", () -> new CraftingUpgradeItem(Config.SERVER.maxUpgradesPerStorage));
	public static final StonecutterUpgradeItem STONECUTTER_UPGRADE = register("stonecutter_upgrade", () -> new StonecutterUpgradeItem(Config.SERVER.maxUpgradesPerStorage));
	public static final StackUpgradeItem STACK_UPGRADE_STARTER_TIER = register("stack_upgrade_starter_tier", () ->
			new StackUpgradeItem(1.5D, Config.SERVER.maxUpgradesPerStorage));
	public static final StackUpgradeItem STACK_UPGRADE_TIER_1 = register("stack_upgrade_tier_1", () ->
			new StackUpgradeItem(2, Config.SERVER.maxUpgradesPerStorage));
	public static final StackUpgradeItem STACK_UPGRADE_TIER_2 = register("stack_upgrade_tier_2", () ->
			new StackUpgradeItem(4, Config.SERVER.maxUpgradesPerStorage));
	public static final StackUpgradeItem STACK_UPGRADE_TIER_3 = register("stack_upgrade_tier_3", () ->
			new StackUpgradeItem(8, Config.SERVER.maxUpgradesPerStorage));
	public static final StackUpgradeItem STACK_UPGRADE_TIER_4 = register("stack_upgrade_tier_4", () ->
			new StackUpgradeItem(16, Config.SERVER.maxUpgradesPerStorage));
	public static final String JUKEBOX_UPGRADE_NAME = "jukebox_upgrade";
	public static final JukeboxUpgradeItem JUKEBOX_UPGRADE = register(JUKEBOX_UPGRADE_NAME, () -> new JukeboxUpgradeItem(Config.SERVER.maxUpgradesPerStorage));
	public static final ToolSwapperUpgradeItem TOOL_SWAPPER_UPGRADE = register("tool_swapper_upgrade",
			() -> new ToolSwapperUpgradeItem(false, false));
	public static final ToolSwapperUpgradeItem ADVANCED_TOOL_SWAPPER_UPGRADE = register("advanced_tool_swapper_upgrade",
			() -> new ToolSwapperUpgradeItem(true, true));
	public static final TankUpgradeItem TANK_UPGRADE = register("tank_upgrade", () -> new TankUpgradeItem(Config.SERVER.tankUpgrade, Config.SERVER.maxUpgradesPerStorage));
	public static final BatteryUpgradeItem BATTERY_UPGRADE = register("battery_upgrade", () -> new BatteryUpgradeItem(Config.SERVER.batteryUpgrade, Config.SERVER.maxUpgradesPerStorage));
	public static final PumpUpgradeItem PUMP_UPGRADE = register("pump_upgrade", () -> new PumpUpgradeItem(false, false, Config.SERVER.pumpUpgrade, Config.SERVER.maxUpgradesPerStorage));
	public static final PumpUpgradeItem ADVANCED_PUMP_UPGRADE = register("advanced_pump_upgrade", () -> new PumpUpgradeItem(true, true, Config.SERVER.pumpUpgrade, Config.SERVER.maxUpgradesPerStorage));
	public static final XpPumpUpgradeItem XP_PUMP_UPGRADE = register("xp_pump_upgrade", () -> new XpPumpUpgradeItem(Config.SERVER.xpPumpUpgrade, Config.SERVER.maxUpgradesPerStorage));
	public static final AnvilUpgradeItem ANVIL_UPGRADE = register("anvil_upgrade", AnvilUpgradeItem::new);

	public static final ItemBase UPGRADE_BASE = register("upgrade_base", () -> new ItemBase(new Item.Properties().stacksTo(16)));

	@SuppressWarnings("unused")
	public static final CreativeModeTab CREATIVE_TAB = FabricItemGroup.builder()
			.title(Component.translatable("itemGroup.sophisticatedbackpacks"))
			.icon(() -> new ItemStack(ModItems.BACKPACK))
			.displayItems((featureFlags, output) -> ITEMS.stream().filter(i -> i instanceof ItemBase).forEach(i -> ((ItemBase) i).addCreativeTabItems(output::accept)))
			.build();

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	// MENU_TYPES
	//
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static final MenuType<BackpackContainer> BACKPACK_CONTAINER_TYPE = registerMenu("backpack", () -> new ExtendedScreenHandlerType<>(BackpackContainer::fromBuffer));

	public static final MenuType<BackpackSettingsContainerMenu> SETTINGS_CONTAINER_TYPE = registerMenu("settings", () -> new ExtendedScreenHandlerType<>(BackpackSettingsContainerMenu::fromBuffer));

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	// ENTITY_TYPES
	//
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static final EntityType<EverlastingBackpackItemEntity> EVERLASTING_BACKPACK_ITEM_ENTITY = registerEntityType("everlasting_backpack_item", () ->
					EntityType.Builder.of(EverlastingBackpackItemEntity::new, MobCategory.MISC)
							.sized(0.25F, 0.25F)
							.clientTrackingRange(6)
							.updateInterval(20)
							.build("")
	);

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	// RECIPE_SERIALIZERS
	//
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static final SimpleCraftingRecipeSerializer<BackpackDyeRecipe> BACKPACK_DYE_RECIPE_SERIALIZER = registerRecipeSerializer("backpack_dye", () -> new SimpleCraftingRecipeSerializer<>(BackpackDyeRecipe::new));
	public static final RecipeSerializer<BackpackUpgradeRecipe> BACKPACK_UPGRADE_RECIPE_SERIALIZER = registerRecipeSerializer("backpack_upgrade", BackpackUpgradeRecipe.Serializer::new);
	public static final RecipeSerializer<SmithingBackpackUpgradeRecipe> SMITHING_BACKPACK_UPGRADE_RECIPE_SERIALIZER = registerRecipeSerializer("smithing_backpack_upgrade", SmithingBackpackUpgradeRecipe.Serializer::new);
	public static final RecipeSerializer<BasicBackpackRecipe> BASIC_BACKPACK_RECIPE_SERIALIZER = registerRecipeSerializer("basic_backpack", BasicBackpackRecipe.Serializer::new);

	public static final LootItemFunctionType COPY_BACKPACK_DATA = registerLootFunction("copy_backpack_data", () -> new LootItemFunctionType(CopyBackpackDataFunction.CODEC));
	public static final LootItemConditionType LOOT_ENABLED_CONDITION = registerLootCondition("loot_enabled", () -> new LootItemConditionType(SBLootEnabledCondition.CODEC));
	public static final Codec<SBLootModifierProvider.InjectLootModifier> INJECT_LOOT = registerLootModifier("inject_loot", () -> SBLootModifierProvider.InjectLootModifier.CODEC);

	public static AttachmentType<BackpackWrapper> BACKPACK_WRAPPER = AttachmentRegistry.createDefaulted(new ResourceLocation(SophisticatedBackpacks.MOD_ID, "backpack_wrapper"), BackpackWrapper::new);

	// Register
	public static <T extends Item> T register(String id, Supplier<T> supplier) {
		T item = supplier.get();
		ITEMS.add(item);
		return Registry.register(BuiltInRegistries.ITEM, SophisticatedBackpacks.getRL(id), item);
	}
	public static <T extends MenuType<?>> T registerMenu(String id, Supplier<T> supplier) {
		return Registry.register(BuiltInRegistries.MENU, SophisticatedBackpacks.getRL(id), supplier.get());
	}
	public static <T extends EntityType<?>> T registerEntityType(String id, Supplier<T> supplier) {
		return Registry.register(BuiltInRegistries.ENTITY_TYPE, SophisticatedBackpacks.getRL(id), supplier.get());
	}
	public static <T extends RecipeSerializer<?>> T registerRecipeSerializer(String id, Supplier<T> supplier) {
		return Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, SophisticatedBackpacks.getRL(id), supplier.get());
	}

	public static <T extends LootItemFunctionType> T registerLootFunction(String id, Supplier<T> supplier) {
		return Registry.register(BuiltInRegistries.LOOT_FUNCTION_TYPE, SophisticatedBackpacks.getRL(id), supplier.get());
	}
	public static <T extends LootItemConditionType> T registerLootCondition(String id, Supplier<T> supplier) {
		return Registry.register(BuiltInRegistries.LOOT_CONDITION_TYPE, SophisticatedBackpacks.getRL(id), supplier.get());
	}
	public static <T extends Codec<? extends IGlobalLootModifier>> T registerLootModifier(String id, Supplier<T> supplier) {
		return Registry.register(PortingLibLoot.GLOBAL_LOOT_MODIFIER_SERIALIZERS.get(), SophisticatedBackpacks.getRL(id), supplier.get());
	}

	public static void register() {
		Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, SophisticatedBackpacks.getRL("item_group"), CREATIVE_TAB);

		registerDispenseBehavior();
		registerCauldronInteractions();

		registerContainers();
		registerCapabilities();
	}

	public static final UpgradeContainerType<PickupUpgradeWrapper, ContentsFilteredUpgradeContainer<PickupUpgradeWrapper>> PICKUP_BASIC_TYPE = new UpgradeContainerType<>(ContentsFilteredUpgradeContainer::new);
	public static final UpgradeContainerType<PickupUpgradeWrapper, ContentsFilteredUpgradeContainer<PickupUpgradeWrapper>> PICKUP_ADVANCED_TYPE = new UpgradeContainerType<>(ContentsFilteredUpgradeContainer::new);
	public static final UpgradeContainerType<MagnetUpgradeWrapper, MagnetUpgradeContainer> MAGNET_BASIC_TYPE = new UpgradeContainerType<>(MagnetUpgradeContainer::new);
	public static final UpgradeContainerType<MagnetUpgradeWrapper, MagnetUpgradeContainer> MAGNET_ADVANCED_TYPE = new UpgradeContainerType<>(MagnetUpgradeContainer::new);
	public static final UpgradeContainerType<FeedingUpgradeWrapper, FeedingUpgradeContainer> FEEDING_TYPE = new UpgradeContainerType<>(FeedingUpgradeContainer::new);
	public static final UpgradeContainerType<FeedingUpgradeWrapper, FeedingUpgradeContainer> ADVANCED_FEEDING_TYPE = new UpgradeContainerType<>(FeedingUpgradeContainer::new);
	public static final UpgradeContainerType<CompactingUpgradeWrapper, CompactingUpgradeContainer> COMPACTING_TYPE = new UpgradeContainerType<>(CompactingUpgradeContainer::new);
	public static final UpgradeContainerType<CompactingUpgradeWrapper, CompactingUpgradeContainer> ADVANCED_COMPACTING_TYPE = new UpgradeContainerType<>(CompactingUpgradeContainer::new);
	public static final UpgradeContainerType<VoidUpgradeWrapper, VoidUpgradeContainer> VOID_TYPE = new UpgradeContainerType<>(VoidUpgradeContainer::new);
	public static final UpgradeContainerType<VoidUpgradeWrapper, VoidUpgradeContainer> ADVANCED_VOID_TYPE = new UpgradeContainerType<>(VoidUpgradeContainer::new);
	public static final UpgradeContainerType<RestockUpgradeWrapper, ContentsFilteredUpgradeContainer<RestockUpgradeWrapper>> RESTOCK_TYPE = new UpgradeContainerType<>(ContentsFilteredUpgradeContainer::new);
	public static final UpgradeContainerType<RestockUpgradeWrapper, ContentsFilteredUpgradeContainer<RestockUpgradeWrapper>> ADVANCED_RESTOCK_TYPE = new UpgradeContainerType<>(ContentsFilteredUpgradeContainer::new);
	public static final UpgradeContainerType<DepositUpgradeWrapper, DepositUpgradeContainer> DEPOSIT_TYPE = new UpgradeContainerType<>(DepositUpgradeContainer::new);
	public static final UpgradeContainerType<DepositUpgradeWrapper, DepositUpgradeContainer> ADVANCED_DEPOSIT_TYPE = new UpgradeContainerType<>(DepositUpgradeContainer::new);
	public static final UpgradeContainerType<RefillUpgradeWrapper, RefillUpgradeContainer> REFILL_TYPE = new UpgradeContainerType<>(RefillUpgradeContainer::new);
	public static final UpgradeContainerType<RefillUpgradeWrapper, RefillUpgradeContainer> ADVANCED_REFILL_TYPE = new UpgradeContainerType<>(RefillUpgradeContainer::new);
	public static final UpgradeContainerType<CookingUpgradeWrapper.SmeltingUpgradeWrapper, CookingUpgradeContainer<SmeltingRecipe, CookingUpgradeWrapper.SmeltingUpgradeWrapper>> SMELTING_TYPE = new UpgradeContainerType<>(CookingUpgradeContainer::new);
	public static final UpgradeContainerType<AutoCookingUpgradeWrapper.AutoSmeltingUpgradeWrapper, AutoCookingUpgradeContainer<SmeltingRecipe, AutoCookingUpgradeWrapper.AutoSmeltingUpgradeWrapper>> AUTO_SMELTING_TYPE = new UpgradeContainerType<>(AutoCookingUpgradeContainer::new);
	public static final UpgradeContainerType<CookingUpgradeWrapper.SmokingUpgradeWrapper, CookingUpgradeContainer<SmokingRecipe, CookingUpgradeWrapper.SmokingUpgradeWrapper>> SMOKING_TYPE = new UpgradeContainerType<>(CookingUpgradeContainer::new);
	public static final UpgradeContainerType<AutoCookingUpgradeWrapper.AutoSmokingUpgradeWrapper, AutoCookingUpgradeContainer<SmokingRecipe, AutoCookingUpgradeWrapper.AutoSmokingUpgradeWrapper>> AUTO_SMOKING_TYPE = new UpgradeContainerType<>(AutoCookingUpgradeContainer::new);
	public static final UpgradeContainerType<CookingUpgradeWrapper.BlastingUpgradeWrapper, CookingUpgradeContainer<BlastingRecipe, CookingUpgradeWrapper.BlastingUpgradeWrapper>> BLASTING_TYPE = new UpgradeContainerType<>(CookingUpgradeContainer::new);
	public static final UpgradeContainerType<AutoCookingUpgradeWrapper.AutoBlastingUpgradeWrapper, AutoCookingUpgradeContainer<BlastingRecipe, AutoCookingUpgradeWrapper.AutoBlastingUpgradeWrapper>> AUTO_BLASTING_TYPE = new UpgradeContainerType<>(AutoCookingUpgradeContainer::new);
	public static final UpgradeContainerType<CraftingUpgradeWrapper, CraftingUpgradeContainer> CRAFTING_TYPE = new UpgradeContainerType<>(CraftingUpgradeContainer::new);
	public static final UpgradeContainerType<InceptionUpgradeWrapper, InceptionUpgradeContainer> INCEPTION_TYPE = new UpgradeContainerType<>(InceptionUpgradeContainer::new);
	public static final UpgradeContainerType<StonecutterUpgradeWrapper, StonecutterUpgradeContainer> STONECUTTER_TYPE = new UpgradeContainerType<>(StonecutterUpgradeContainer::new);
	public static final UpgradeContainerType<JukeboxUpgradeItem.Wrapper, JukeboxUpgradeContainer> JUKEBOX_TYPE = new UpgradeContainerType<>(JukeboxUpgradeContainer::new);
	public static final UpgradeContainerType<ToolSwapperUpgradeWrapper, ToolSwapperUpgradeContainer> TOOL_SWAPPER_TYPE = new UpgradeContainerType<>(ToolSwapperUpgradeContainer::new);
	public static final UpgradeContainerType<TankUpgradeWrapper, TankUpgradeContainer> TANK_TYPE = new UpgradeContainerType<>(TankUpgradeContainer::new);
	public static final UpgradeContainerType<BatteryUpgradeWrapper, BatteryUpgradeContainer> BATTERY_TYPE = new UpgradeContainerType<>(BatteryUpgradeContainer::new);
	public static final UpgradeContainerType<PumpUpgradeWrapper, PumpUpgradeContainer> PUMP_TYPE = new UpgradeContainerType<>(PumpUpgradeContainer::new);
	public static final UpgradeContainerType<PumpUpgradeWrapper, PumpUpgradeContainer> ADVANCED_PUMP_TYPE = new UpgradeContainerType<>(PumpUpgradeContainer::new);
	public static final UpgradeContainerType<XpPumpUpgradeWrapper, XpPumpUpgradeContainer> XP_PUMP_TYPE = new UpgradeContainerType<>(XpPumpUpgradeContainer::new);
	public static final UpgradeContainerType<AnvilUpgradeWrapper, AnvilUpgradeContainer> ANVIL_TYPE = new UpgradeContainerType<>(AnvilUpgradeContainer::new);

	public static void registerContainers() {
		UpgradeContainerRegistry.register(PICKUP_UPGRADE, PICKUP_BASIC_TYPE);
		UpgradeContainerRegistry.register(ADVANCED_PICKUP_UPGRADE, PICKUP_ADVANCED_TYPE);
		UpgradeContainerRegistry.register(FILTER_UPGRADE, FilterUpgradeContainer.BASIC_TYPE);
		UpgradeContainerRegistry.register(ADVANCED_FILTER_UPGRADE, FilterUpgradeContainer.ADVANCED_TYPE);
		UpgradeContainerRegistry.register(MAGNET_UPGRADE, MAGNET_BASIC_TYPE);
		UpgradeContainerRegistry.register(ADVANCED_MAGNET_UPGRADE, MAGNET_ADVANCED_TYPE);
		UpgradeContainerRegistry.register(FEEDING_UPGRADE, FEEDING_TYPE);
		UpgradeContainerRegistry.register(ADVANCED_FEEDING_UPGRADE, ADVANCED_FEEDING_TYPE);
		UpgradeContainerRegistry.register(COMPACTING_UPGRADE, COMPACTING_TYPE);
		UpgradeContainerRegistry.register(ADVANCED_COMPACTING_UPGRADE, ADVANCED_COMPACTING_TYPE);
		UpgradeContainerRegistry.register(VOID_UPGRADE, VOID_TYPE);
		UpgradeContainerRegistry.register(ADVANCED_VOID_UPGRADE, ADVANCED_VOID_TYPE);
		UpgradeContainerRegistry.register(RESTOCK_UPGRADE, RESTOCK_TYPE);
		UpgradeContainerRegistry.register(ADVANCED_RESTOCK_UPGRADE, ADVANCED_RESTOCK_TYPE);
		UpgradeContainerRegistry.register(DEPOSIT_UPGRADE, DEPOSIT_TYPE);
		UpgradeContainerRegistry.register(ADVANCED_DEPOSIT_UPGRADE, ADVANCED_DEPOSIT_TYPE);
		UpgradeContainerRegistry.register(REFILL_UPGRADE, REFILL_TYPE);
		UpgradeContainerRegistry.register(ADVANCED_REFILL_UPGRADE, ADVANCED_REFILL_TYPE);
		UpgradeContainerRegistry.register(SMELTING_UPGRADE, SMELTING_TYPE);
		UpgradeContainerRegistry.register(AUTO_SMELTING_UPGRADE, AUTO_SMELTING_TYPE);
		UpgradeContainerRegistry.register(SMOKING_UPGRADE, SMOKING_TYPE);
		UpgradeContainerRegistry.register(AUTO_SMOKING_UPGRADE, AUTO_SMOKING_TYPE);
		UpgradeContainerRegistry.register(BLASTING_UPGRADE, BLASTING_TYPE);
		UpgradeContainerRegistry.register(AUTO_BLASTING_UPGRADE, AUTO_BLASTING_TYPE);
		UpgradeContainerRegistry.register(CRAFTING_UPGRADE, CRAFTING_TYPE);
		UpgradeContainerRegistry.register(INCEPTION_UPGRADE, INCEPTION_TYPE);
		UpgradeContainerRegistry.register(STONECUTTER_UPGRADE, STONECUTTER_TYPE);
		UpgradeContainerRegistry.register(JUKEBOX_UPGRADE, JUKEBOX_TYPE);
		UpgradeContainerRegistry.register(ADVANCED_TOOL_SWAPPER_UPGRADE, TOOL_SWAPPER_TYPE);
		UpgradeContainerRegistry.register(TANK_UPGRADE, TANK_TYPE);
		UpgradeContainerRegistry.register(BATTERY_UPGRADE, BATTERY_TYPE);
		UpgradeContainerRegistry.register(PUMP_UPGRADE, PUMP_TYPE);
		UpgradeContainerRegistry.register(ADVANCED_PUMP_UPGRADE, ADVANCED_PUMP_TYPE);
		UpgradeContainerRegistry.register(XP_PUMP_UPGRADE, XP_PUMP_TYPE);
		UpgradeContainerRegistry.register(ANVIL_UPGRADE, ANVIL_TYPE);
	}

	public static void registerDispenseBehavior() {
		DispenserBlock.registerBehavior(BACKPACK, new BackpackDispenseBehavior());
		DispenserBlock.registerBehavior(COPPER_BACKPACK, new BackpackDispenseBehavior());
		DispenserBlock.registerBehavior(IRON_BACKPACK, new BackpackDispenseBehavior());
		DispenserBlock.registerBehavior(GOLD_BACKPACK, new BackpackDispenseBehavior());
		DispenserBlock.registerBehavior(DIAMOND_BACKPACK, new BackpackDispenseBehavior());
		DispenserBlock.registerBehavior(NETHERITE_BACKPACK, new BackpackDispenseBehavior());
	}

	public static void registerCauldronInteractions() {
		CauldronInteraction.WATER.map().put(BACKPACK, new BackpackCauldronInteraction());
		CauldronInteraction.WATER.map().put(COPPER_BACKPACK, new BackpackCauldronInteraction());
		CauldronInteraction.WATER.map().put(IRON_BACKPACK, new BackpackCauldronInteraction());
		CauldronInteraction.WATER.map().put(GOLD_BACKPACK, new BackpackCauldronInteraction());
		CauldronInteraction.WATER.map().put(DIAMOND_BACKPACK, new BackpackCauldronInteraction());
		CauldronInteraction.WATER.map().put(NETHERITE_BACKPACK, new BackpackCauldronInteraction());
	}

	private static void registerCapabilities() {
		ItemItemStorages.ITEM.registerForItems((stack, ctx) -> BackpackWrapper.fromData(stack).getInventoryForInputOutput(), BACKPACKS);

		FluidStorage.ITEM.registerForItems((stack, ctx) -> {
			if (Boolean.FALSE.equals(Config.SERVER.itemFluidHandlerEnabled.get())) {
				return null;
			}
			return BackpackWrapper.fromData(stack).getItemFluidHandler().orElse(null);
		}, BACKPACKS);

		EnergyStorage.ITEM.registerForItems((stack, ctx) -> BackpackWrapper.fromData(stack).getEnergyStorage().orElse(null), BACKPACKS);
	}

	private static class BackpackCauldronInteraction implements CauldronInteraction {
		private static boolean hasDefaultColor(IStorageWrapper wrapper) {
			return wrapper.getAccentColor() == BackpackWrapper.DEFAULT_BORDER_COLOR && wrapper.getMainColor() == BackpackWrapper.DEFAULT_CLOTH_COLOR;
		}

		@Override
		public InteractionResult interact(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack stack) {
			IBackpackWrapper backpackWrapper = BackpackWrapper.fromData(stack);
			if (hasDefaultColor(backpackWrapper)) {
				return InteractionResult.PASS;
			}

			if (!level.isClientSide) {
				backpackWrapper.setColors(BackpackWrapper.DEFAULT_CLOTH_COLOR, BackpackWrapper.DEFAULT_BORDER_COLOR);
			}

			return InteractionResult.sidedSuccess(level.isClientSide);
		}
	}

	private static class BackpackDispenseBehavior extends OptionalDispenseItemBehavior {
		@Override
		protected ItemStack execute(BlockSource source, ItemStack stack) {
			setSuccess(false);
			Item item = stack.getItem();
			if (item instanceof BackpackItem backpackItem) {
				Direction dispenserDirection = source.state().getValue(DispenserBlock.FACING);
				BlockPos blockpos = source.pos().relative(dispenserDirection);
				Direction against = source.level().isEmptyBlock(blockpos.below()) ? dispenserDirection.getOpposite() : Direction.UP;

				setSuccess(backpackItem.tryPlace(null, dispenserDirection.getAxis() == Direction.Axis.Y ? Direction.NORTH : dispenserDirection.getOpposite(), new DirectionalPlaceContext(source.level(), blockpos, dispenserDirection, stack, against)).consumesAction());
			}

			return stack;
		}
	}
}
