package net.p3pp3rf1y.sophisticatedbackpacks.client.init;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.crafting.SmokingRecipe;
import net.p3pp3rf1y.sophisticatedbackpacks.Config;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.BackpackScreen;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.BackpackSettingsScreen;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.SBPButtonDefinitions;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.anvil.AnvilUpgradeTab;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.deposit.DepositUpgradeTab;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.inception.InceptionUpgradeTab;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.refill.RefillUpgradeContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.refill.RefillUpgradeTab;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.restock.RestockUpgradeTab;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.restock.RestockUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.toolswapper.ToolSwapperUpgradeTab;
import net.p3pp3rf1y.sophisticatedcore.client.gui.StorageScreenBase;
import net.p3pp3rf1y.sophisticatedcore.client.gui.UpgradeGuiManager;
import net.p3pp3rf1y.sophisticatedcore.client.gui.utils.Position;
import net.p3pp3rf1y.sophisticatedcore.upgrades.ContentsFilteredUpgradeContainer;
import net.p3pp3rf1y.sophisticatedcore.upgrades.battery.BatteryInventoryPart;
import net.p3pp3rf1y.sophisticatedcore.upgrades.battery.BatteryUpgradeTab;
import net.p3pp3rf1y.sophisticatedcore.upgrades.compacting.CompactingUpgradeContainer;
import net.p3pp3rf1y.sophisticatedcore.upgrades.compacting.CompactingUpgradeTab;
import net.p3pp3rf1y.sophisticatedcore.upgrades.cooking.AutoCookingUpgradeContainer;
import net.p3pp3rf1y.sophisticatedcore.upgrades.cooking.AutoCookingUpgradeTab;
import net.p3pp3rf1y.sophisticatedcore.upgrades.cooking.AutoCookingUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedcore.upgrades.cooking.CookingUpgradeTab;
import net.p3pp3rf1y.sophisticatedcore.upgrades.crafting.CraftingUpgradeContainer;
import net.p3pp3rf1y.sophisticatedcore.upgrades.crafting.CraftingUpgradeTab;
import net.p3pp3rf1y.sophisticatedcore.upgrades.feeding.FeedingUpgradeContainer;
import net.p3pp3rf1y.sophisticatedcore.upgrades.feeding.FeedingUpgradeTab;
import net.p3pp3rf1y.sophisticatedcore.upgrades.filter.FilterUpgradeContainer;
import net.p3pp3rf1y.sophisticatedcore.upgrades.filter.FilterUpgradeTab;
import net.p3pp3rf1y.sophisticatedcore.upgrades.jukebox.JukeboxUpgradeTab;
import net.p3pp3rf1y.sophisticatedcore.upgrades.magnet.MagnetUpgradeContainer;
import net.p3pp3rf1y.sophisticatedcore.upgrades.magnet.MagnetUpgradeTab;
import net.p3pp3rf1y.sophisticatedcore.upgrades.pickup.PickupUpgradeTab;
import net.p3pp3rf1y.sophisticatedcore.upgrades.pickup.PickupUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedcore.upgrades.pump.PumpUpgradeTab;
import net.p3pp3rf1y.sophisticatedcore.upgrades.stonecutter.StonecutterUpgradeContainer;
import net.p3pp3rf1y.sophisticatedcore.upgrades.stonecutter.StonecutterUpgradeTab;
import net.p3pp3rf1y.sophisticatedcore.upgrades.tank.TankInventoryPart;
import net.p3pp3rf1y.sophisticatedcore.upgrades.tank.TankUpgradeTab;
import net.p3pp3rf1y.sophisticatedcore.upgrades.voiding.VoidUpgradeContainer;
import net.p3pp3rf1y.sophisticatedcore.upgrades.voiding.VoidUpgradeTab;
import net.p3pp3rf1y.sophisticatedcore.upgrades.xppump.XpPumpUpgradeContainer;
import net.p3pp3rf1y.sophisticatedcore.upgrades.xppump.XpPumpUpgradeTab;

import static net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems.ADVANCED_COMPACTING_TYPE;
import static net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems.ADVANCED_DEPOSIT_TYPE;
import static net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems.ADVANCED_FEEDING_TYPE;
import static net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems.ADVANCED_PUMP_TYPE;
import static net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems.ADVANCED_REFILL_TYPE;
import static net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems.ADVANCED_RESTOCK_TYPE;
import static net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems.ADVANCED_VOID_TYPE;
import static net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems.ANVIL_TYPE;
import static net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems.AUTO_BLASTING_TYPE;
import static net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems.AUTO_SMELTING_TYPE;
import static net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems.AUTO_SMOKING_TYPE;
import static net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems.BACKPACK_CONTAINER_TYPE;
import static net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems.BATTERY_TYPE;
import static net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems.BLASTING_TYPE;
import static net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems.COMPACTING_TYPE;
import static net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems.CRAFTING_TYPE;
import static net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems.DEPOSIT_TYPE;
import static net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems.FEEDING_TYPE;
import static net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems.INCEPTION_TYPE;
import static net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems.JUKEBOX_TYPE;
import static net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems.MAGNET_ADVANCED_TYPE;
import static net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems.MAGNET_BASIC_TYPE;
import static net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems.PICKUP_ADVANCED_TYPE;
import static net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems.PICKUP_BASIC_TYPE;
import static net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems.PUMP_TYPE;
import static net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems.REFILL_TYPE;
import static net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems.RESTOCK_TYPE;
import static net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems.SETTINGS_CONTAINER_TYPE;
import static net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems.SMELTING_TYPE;
import static net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems.SMOKING_TYPE;
import static net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems.STONECUTTER_TYPE;
import static net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems.TANK_TYPE;
import static net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems.TOOL_SWAPPER_TYPE;
import static net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems.VOID_TYPE;
import static net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems.XP_PUMP_TYPE;

public class ModItems {
	public static void registerScreens() {
		MenuScreens.register(BACKPACK_CONTAINER_TYPE, BackpackScreen::constructScreen);
		MenuScreens.register(SETTINGS_CONTAINER_TYPE, BackpackSettingsScreen::constructScreen);

		UpgradeGuiManager.registerTab(PICKUP_BASIC_TYPE, (ContentsFilteredUpgradeContainer<PickupUpgradeWrapper> uc, Position p, StorageScreenBase<?> s) ->
				new PickupUpgradeTab.Basic(uc, p, s, Config.SERVER.pickupUpgrade.slotsInRow.get(), SBPButtonDefinitions.BACKPACK_CONTENTS_FILTER_TYPE));
		UpgradeGuiManager.registerTab(PICKUP_ADVANCED_TYPE, (ContentsFilteredUpgradeContainer<PickupUpgradeWrapper> uc, Position p, StorageScreenBase<?> s) ->
				new PickupUpgradeTab.Advanced(uc, p, s, Config.SERVER.advancedPickupUpgrade.slotsInRow.get(), SBPButtonDefinitions.BACKPACK_CONTENTS_FILTER_TYPE));
		UpgradeGuiManager.registerTab(FilterUpgradeContainer.BASIC_TYPE, (FilterUpgradeContainer uc, Position p, StorageScreenBase<?> s) ->
				new FilterUpgradeTab.Basic(uc, p, s, Config.SERVER.filterUpgrade.slotsInRow.get(), SBPButtonDefinitions.BACKPACK_CONTENTS_FILTER_TYPE));
		UpgradeGuiManager.registerTab(FilterUpgradeContainer.ADVANCED_TYPE, (FilterUpgradeContainer uc, Position p, StorageScreenBase<?> s) ->
				new FilterUpgradeTab.Advanced(uc, p, s, Config.SERVER.advancedFilterUpgrade.slotsInRow.get(), SBPButtonDefinitions.BACKPACK_CONTENTS_FILTER_TYPE));
		UpgradeGuiManager.registerTab(MAGNET_BASIC_TYPE, (MagnetUpgradeContainer uc, Position p, StorageScreenBase<?> s) ->
				new MagnetUpgradeTab.Basic(uc, p, s, Config.SERVER.magnetUpgrade.slotsInRow.get(), SBPButtonDefinitions.BACKPACK_CONTENTS_FILTER_TYPE));
		UpgradeGuiManager.registerTab(MAGNET_ADVANCED_TYPE, (MagnetUpgradeContainer uc, Position p, StorageScreenBase<?> s) ->
				new MagnetUpgradeTab.Advanced(uc, p, s, Config.SERVER.advancedMagnetUpgrade.slotsInRow.get(), SBPButtonDefinitions.BACKPACK_CONTENTS_FILTER_TYPE));
		UpgradeGuiManager.registerTab(FEEDING_TYPE, (FeedingUpgradeContainer uc, Position p, StorageScreenBase<?> s) ->
				new FeedingUpgradeTab.Basic(uc, p, s, Config.SERVER.feedingUpgrade.slotsInRow.get()));
		UpgradeGuiManager.registerTab(ADVANCED_FEEDING_TYPE, (FeedingUpgradeContainer uc, Position p, StorageScreenBase<?> s) ->
				new FeedingUpgradeTab.Advanced(uc, p, s, Config.SERVER.advancedFeedingUpgrade.slotsInRow.get()));
		UpgradeGuiManager.registerTab(COMPACTING_TYPE, (CompactingUpgradeContainer uc, Position p, StorageScreenBase<?> s) ->
				new CompactingUpgradeTab.Basic(uc, p, s, Config.SERVER.compactingUpgrade.slotsInRow.get()));
		UpgradeGuiManager.registerTab(ADVANCED_COMPACTING_TYPE, (CompactingUpgradeContainer uc, Position p, StorageScreenBase<?> s) ->
				new CompactingUpgradeTab.Advanced(uc, p, s, Config.SERVER.advancedCompactingUpgrade.slotsInRow.get()));
		UpgradeGuiManager.registerTab(VOID_TYPE, (VoidUpgradeContainer uc, Position p, StorageScreenBase<?> s) ->
				new VoidUpgradeTab.Basic(uc, p, s, Config.SERVER.voidUpgrade.slotsInRow.get()));
		UpgradeGuiManager.registerTab(ADVANCED_VOID_TYPE, (VoidUpgradeContainer uc, Position p, StorageScreenBase<?> s) ->
				new VoidUpgradeTab.Advanced(uc, p, s, Config.SERVER.advancedVoidUpgrade.slotsInRow.get()));
		UpgradeGuiManager.registerTab(RESTOCK_TYPE, (ContentsFilteredUpgradeContainer<RestockUpgradeWrapper> uc, Position p, StorageScreenBase<?> s) ->
				new RestockUpgradeTab.Basic(uc, p, s, SBPButtonDefinitions.BACKPACK_CONTENTS_FILTER_TYPE));
		UpgradeGuiManager.registerTab(ADVANCED_RESTOCK_TYPE, (ContentsFilteredUpgradeContainer<RestockUpgradeWrapper> uc, Position p, StorageScreenBase<?> s) ->
				new RestockUpgradeTab.Advanced(uc, p, s, SBPButtonDefinitions.BACKPACK_CONTENTS_FILTER_TYPE));
		UpgradeGuiManager.registerTab(DEPOSIT_TYPE, DepositUpgradeTab.Basic::new);
		UpgradeGuiManager.registerTab(ADVANCED_DEPOSIT_TYPE, DepositUpgradeTab.Advanced::new);
		UpgradeGuiManager.registerTab(REFILL_TYPE, (RefillUpgradeContainer uc, Position p, StorageScreenBase<?> s) -> new RefillUpgradeTab.Basic(uc, p, s,
				Config.SERVER.refillUpgrade.slotsInRow.get()));
		UpgradeGuiManager.registerTab(ADVANCED_REFILL_TYPE, (RefillUpgradeContainer uc, Position p, StorageScreenBase<?> s) -> new RefillUpgradeTab.Advanced(uc, p, s,
				Config.SERVER.advancedRefillUpgrade.slotsInRow.get()));
		UpgradeGuiManager.registerTab(SMELTING_TYPE, CookingUpgradeTab.SmeltingUpgradeTab::new);
		UpgradeGuiManager.registerTab(AUTO_SMELTING_TYPE, (AutoCookingUpgradeContainer<SmeltingRecipe, AutoCookingUpgradeWrapper.AutoSmeltingUpgradeWrapper> uc, Position p, StorageScreenBase<?> s) ->
				new AutoCookingUpgradeTab.AutoSmeltingUpgradeTab(uc, p, s, Config.SERVER.autoSmeltingUpgrade.inputFilterSlotsInRow.get(), Config.SERVER.autoSmeltingUpgrade.fuelFilterSlotsInRow.get()));
		UpgradeGuiManager.registerTab(SMOKING_TYPE, CookingUpgradeTab.SmokingUpgradeTab::new);
		UpgradeGuiManager.registerTab(AUTO_SMOKING_TYPE, (AutoCookingUpgradeContainer<SmokingRecipe, AutoCookingUpgradeWrapper.AutoSmokingUpgradeWrapper> uc, Position p, StorageScreenBase<?> s) ->
				new AutoCookingUpgradeTab.AutoSmokingUpgradeTab(uc, p, s, Config.SERVER.autoSmokingUpgrade.inputFilterSlotsInRow.get(), Config.SERVER.autoSmokingUpgrade.fuelFilterSlotsInRow.get()));
		UpgradeGuiManager.registerTab(BLASTING_TYPE, CookingUpgradeTab.BlastingUpgradeTab::new);
		UpgradeGuiManager.registerTab(AUTO_BLASTING_TYPE, (AutoCookingUpgradeContainer<BlastingRecipe, AutoCookingUpgradeWrapper.AutoBlastingUpgradeWrapper> uc, Position p, StorageScreenBase<?> s) ->
				new AutoCookingUpgradeTab.AutoBlastingUpgradeTab(uc, p, s, Config.SERVER.autoBlastingUpgrade.inputFilterSlotsInRow.get(), Config.SERVER.autoBlastingUpgrade.fuelFilterSlotsInRow.get()));
		UpgradeGuiManager.registerTab(CRAFTING_TYPE, (CraftingUpgradeContainer uc, Position p, StorageScreenBase<?> s) ->
				new CraftingUpgradeTab(uc, p, s, SBPButtonDefinitions.SHIFT_CLICK_TARGET));
		UpgradeGuiManager.registerTab(INCEPTION_TYPE, InceptionUpgradeTab::new);
		UpgradeGuiManager.registerTab(STONECUTTER_TYPE, (StonecutterUpgradeContainer upgradeContainer, Position position, StorageScreenBase<?> screen) ->
				new StonecutterUpgradeTab(upgradeContainer, position, screen, SBPButtonDefinitions.SHIFT_CLICK_TARGET));
		UpgradeGuiManager.registerTab(JUKEBOX_TYPE, JukeboxUpgradeTab::new);
		UpgradeGuiManager.registerTab(TOOL_SWAPPER_TYPE, ToolSwapperUpgradeTab::new);
		UpgradeGuiManager.registerTab(TANK_TYPE, TankUpgradeTab::new);
		UpgradeGuiManager.registerTab(BATTERY_TYPE, BatteryUpgradeTab::new);
		UpgradeGuiManager.registerInventoryPart(TANK_TYPE, TankInventoryPart::new);
		UpgradeGuiManager.registerInventoryPart(BATTERY_TYPE, BatteryInventoryPart::new);
		UpgradeGuiManager.registerTab(PUMP_TYPE, PumpUpgradeTab.Basic::new);
		UpgradeGuiManager.registerTab(ADVANCED_PUMP_TYPE, PumpUpgradeTab.Advanced::new);
		UpgradeGuiManager.registerTab(XP_PUMP_TYPE, (XpPumpUpgradeContainer upgradeContainer, Position position, StorageScreenBase<?> screen) ->
				new XpPumpUpgradeTab(upgradeContainer, position, screen, Config.SERVER.xpPumpUpgrade.mendingOn.get()));
		UpgradeGuiManager.registerTab(ANVIL_TYPE, AnvilUpgradeTab::new);
	}
}
