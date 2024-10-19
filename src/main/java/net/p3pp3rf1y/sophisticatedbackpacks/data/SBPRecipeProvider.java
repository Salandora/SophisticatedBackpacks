package net.p3pp3rf1y.sophisticatedbackpacks.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.compat.chipped.ChippedCompat;
import net.p3pp3rf1y.sophisticatedbackpacks.crafting.*;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems;
import net.p3pp3rf1y.sophisticatedcore.compat.CompatModIds;
import net.p3pp3rf1y.sophisticatedcore.compat.chipped.BlockTransformationUpgradeItem;
import net.p3pp3rf1y.sophisticatedcore.crafting.ShapeBasedRecipeBuilder;
import net.p3pp3rf1y.sophisticatedcore.crafting.UpgradeNextTierRecipe;
import net.p3pp3rf1y.sophisticatedcore.util.RegistryHelper;

import java.util.concurrent.CompletableFuture;

public class SBPRecipeProvider extends FabricRecipeProvider {
	private static final String HAS_UPGRADE_BASE = "has_upgrade_base";
	private static final String HAS_SMELTING_UPGRADE = "has_smelting_upgrade";

	public SBPRecipeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registriesFuture);
	}

	@Override
	public void buildRecipes(RecipeOutput recipeOutput) {
		ShapeBasedRecipeBuilder.shaped(ModItems.BACKPACK.get(), BasicBackpackRecipe::new)
				.pattern("SLS")
				.pattern("SCS")
				.pattern("LLL")
				.define('L', ConventionalItemTags.LEATHERS)
				.define('C', ConventionalItemTags.WOODEN_CHESTS)
				.define('S', ConventionalItemTags.STRINGS)
				.unlockedBy("has_leather", hasLeather())
				.save(recipeOutput);

		SpecialRecipeBuilder.special(BackpackDyeRecipe::new).save(recipeOutput, SophisticatedBackpacks.getRegistryName("backpack_dye"));

		ShapeBasedRecipeBuilder.shaped(ModItems.DIAMOND_BACKPACK.get(), BackpackUpgradeRecipe::new)
				.pattern("DDD")
				.pattern("DBD")
				.pattern("DDD")
				.define('D', ConventionalItemTags.DIAMOND_GEMS)
				.define('B', ModItems.GOLD_BACKPACK.get())
				.unlockedBy("has_gold_backpack", has(ModItems.GOLD_BACKPACK.get()))
				.save(recipeOutput);

		ShapeBasedRecipeBuilder.shaped(ModItems.GOLD_BACKPACK.get(), BackpackUpgradeRecipe::new)
				.pattern("GGG")
				.pattern("GBG")
				.pattern("GGG")
				.define('G', ConventionalItemTags.GOLD_INGOTS)
				.define('B', ModItems.IRON_BACKPACK.get())
				.unlockedBy("has_iron_backpack", has(ModItems.IRON_BACKPACK.get()))
				.save(recipeOutput);

		ShapeBasedRecipeBuilder.shaped(ModItems.IRON_BACKPACK.get(), BackpackUpgradeRecipe::new)
				.pattern("III")
				.pattern("IBI")
				.pattern("III")
				.define('I', ConventionalItemTags.IRON_INGOTS)
				.define('B', ModItems.BACKPACK.get())
				.unlockedBy("has_backpack", has(ModItems.BACKPACK.get()))
				.save(recipeOutput);

		ShapeBasedRecipeBuilder.shaped(ModItems.IRON_BACKPACK.get(), BackpackUpgradeRecipe::new)
				.pattern(" I ")
				.pattern("IBI")
				.pattern(" I ")
				.define('I', ConventionalItemTags.IRON_INGOTS)
				.define('B', ModItems.COPPER_BACKPACK.get())
				.unlockedBy("has_copper_backpack", has(ModItems.COPPER_BACKPACK.get()))
				.save(recipeOutput, SophisticatedBackpacks.getRL("iron_backpack_from_copper"));

		ShapeBasedRecipeBuilder.shaped(ModItems.COPPER_BACKPACK.get(), BackpackUpgradeRecipe::new)
				.pattern("CCC")
				.pattern("CBC")
				.pattern("CCC")
				.define('C', ConventionalItemTags.COPPER_INGOTS)
				.define('B', ModItems.BACKPACK.get())
				.unlockedBy("has_backpack", has(ModItems.BACKPACK.get()))
				.save(recipeOutput);

		//using ShapeBasedRecipeBuilder here for simple items instead of just ShapedRecipeBuilder to avoid having ot clutter the code
		// with repeated definitions of item enabled conditional recipe for the different basic items
		ShapeBasedRecipeBuilder.shaped(ModItems.PICKUP_UPGRADE.get())
				.pattern(" P ")
				.pattern("SBS")
				.pattern("RRR")
				.define('B', ModItems.UPGRADE_BASE.get())
				.define('R', ConventionalItemTags.REDSTONE_DUSTS)
				.define('S', ConventionalItemTags.STRINGS)
				.define('P', Blocks.STICKY_PISTON)
				.unlockedBy(HAS_UPGRADE_BASE, has(ModItems.UPGRADE_BASE.get()))
				.save(recipeOutput);

		ShapeBasedRecipeBuilder.shaped(ModItems.UPGRADE_BASE.get())
				.pattern("SIS")
				.pattern("ILI")
				.pattern("SIS")
				.define('L', ConventionalItemTags.LEATHERS)
				.define('I', ConventionalItemTags.IRON_INGOTS)
				.define('S', ConventionalItemTags.STRINGS)
				.unlockedBy("has_leather", hasLeather())
				.save(recipeOutput);

		ShapeBasedRecipeBuilder.shaped(ModItems.ADVANCED_PICKUP_UPGRADE.get(), UpgradeNextTierRecipe::new)
				.pattern(" D ")
				.pattern("GPG")
				.pattern("RRR")
				.define('D', ConventionalItemTags.DIAMOND_GEMS)
				.define('G', ConventionalItemTags.GOLD_INGOTS)
				.define('R', ConventionalItemTags.REDSTONE_DUSTS)
				.define('P', ModItems.PICKUP_UPGRADE.get())
				.unlockedBy("has_pickup_upgrade", has(ModItems.PICKUP_UPGRADE.get()))
				.save(recipeOutput);

		ShapeBasedRecipeBuilder.shaped(ModItems.FILTER_UPGRADE.get())
				.pattern("RSR")
				.pattern("SBS")
				.pattern("RSR")
				.define('B', ModItems.UPGRADE_BASE.get())
				.define('R', ConventionalItemTags.REDSTONE_DUSTS)
				.define('S', ConventionalItemTags.STRINGS)
				.unlockedBy(HAS_UPGRADE_BASE, has(ModItems.UPGRADE_BASE.get()))
				.save(recipeOutput);

		ShapeBasedRecipeBuilder.shaped(ModItems.ADVANCED_FILTER_UPGRADE.get(), UpgradeNextTierRecipe::new)
				.pattern("GPG")
				.pattern("RRR")
				.define('G', ConventionalItemTags.GOLD_INGOTS)
				.define('R', ConventionalItemTags.REDSTONE_DUSTS)
				.define('P', ModItems.FILTER_UPGRADE.get())
				.unlockedBy("has_filter_upgrade", has(ModItems.FILTER_UPGRADE.get()))
				.save(recipeOutput);

		ShapeBasedRecipeBuilder.shaped(ModItems.MAGNET_UPGRADE.get(), UpgradeNextTierRecipe::new)
				.pattern("EIE")
				.pattern("IPI")
				.pattern("R L")
				.define('E', ConventionalItemTags.ENDER_PEARLS)
				.define('I', ConventionalItemTags.IRON_INGOTS)
				.define('R', ConventionalItemTags.REDSTONE_DUSTS)
				.define('L', ConventionalItemTags.LAPIS_GEMS)
				.define('P', ModItems.PICKUP_UPGRADE.get())
				.unlockedBy("has_pickup_upgrade", has(ModItems.PICKUP_UPGRADE.get()))
				.save(recipeOutput);

		ShapeBasedRecipeBuilder.shaped(ModItems.ADVANCED_MAGNET_UPGRADE.get(), UpgradeNextTierRecipe::new)
				.pattern("EIE")
				.pattern("IPI")
				.pattern("R L")
				.define('E', ConventionalItemTags.ENDER_PEARLS)
				.define('I', ConventionalItemTags.IRON_INGOTS)
				.define('R', ConventionalItemTags.REDSTONE_DUSTS)
				.define('L', ConventionalItemTags.LAPIS_GEMS)
				.define('P', ModItems.ADVANCED_PICKUP_UPGRADE.get())
				.unlockedBy("has_advanced_pickup_upgrade", has(ModItems.ADVANCED_PICKUP_UPGRADE.get()))
				.save(recipeOutput);

		ShapeBasedRecipeBuilder.shaped(ModItems.ADVANCED_MAGNET_UPGRADE.get(), UpgradeNextTierRecipe::new)
				.pattern(" D ")
				.pattern("GMG")
				.pattern("RRR")
				.define('D', ConventionalItemTags.DIAMOND_GEMS)
				.define('G', ConventionalItemTags.GOLD_INGOTS)
				.define('R', ConventionalItemTags.REDSTONE_DUSTS)
				.define('M', ModItems.MAGNET_UPGRADE.get())
				.unlockedBy("has_magnet_upgrade", has(ModItems.MAGNET_UPGRADE.get()))
				.save(recipeOutput, SophisticatedBackpacks.getRL("advanced_magnet_upgrade_from_basic"));

		ShapeBasedRecipeBuilder.shaped(ModItems.FEEDING_UPGRADE.get())
				.pattern(" C ")
				.pattern("ABM")
				.pattern(" E ")
				.define('B', ModItems.UPGRADE_BASE.get())
				.define('C', Items.GOLDEN_CARROT)
				.define('A', Items.GOLDEN_APPLE)
				.define('M', Items.GLISTERING_MELON_SLICE)
				.define('E', ConventionalItemTags.ENDER_PEARLS)
				.unlockedBy(HAS_UPGRADE_BASE, has(ModItems.UPGRADE_BASE.get()))
				.save(recipeOutput);

		ShapeBasedRecipeBuilder.shaped(ModItems.COMPACTING_UPGRADE.get())
				.pattern("IPI")
				.pattern("PBP")
				.pattern("RPR")
				.define('B', ModItems.UPGRADE_BASE.get())
				.define('I', ConventionalItemTags.IRON_INGOTS)
				.define('P', Items.PISTON)
				.define('R', ConventionalItemTags.REDSTONE_DUSTS)
				.unlockedBy(HAS_UPGRADE_BASE, has(ModItems.UPGRADE_BASE.get()))
				.save(recipeOutput);

		ShapeBasedRecipeBuilder.shaped(ModItems.ADVANCED_COMPACTING_UPGRADE.get(), UpgradeNextTierRecipe::new)
				.pattern(" D ")
				.pattern("GCG")
				.pattern("RRR")
				.define('D', ConventionalItemTags.DIAMOND_GEMS)
				.define('G', ConventionalItemTags.GOLD_INGOTS)
				.define('R', ConventionalItemTags.REDSTONE_DUSTS)
				.define('C', ModItems.COMPACTING_UPGRADE.get())
				.unlockedBy("has_compacting_upgrade", has(ModItems.COMPACTING_UPGRADE.get()))
				.save(recipeOutput);

		ShapeBasedRecipeBuilder.shaped(ModItems.VOID_UPGRADE.get())
				.pattern(" E ")
				.pattern("OBO")
				.pattern("ROR")
				.define('B', ModItems.UPGRADE_BASE.get())
				.define('E', ConventionalItemTags.ENDER_PEARLS)
				.define('O', ConventionalItemTags.OBSIDIANS)
				.define('R', ConventionalItemTags.REDSTONE_DUSTS)
				.unlockedBy(HAS_UPGRADE_BASE, has(ModItems.UPGRADE_BASE.get()))
				.save(recipeOutput);

		ShapeBasedRecipeBuilder.shaped(ModItems.ADVANCED_VOID_UPGRADE.get(), UpgradeNextTierRecipe::new)
				.pattern(" D ")
				.pattern("GVG")
				.pattern("RRR")
				.define('D', ConventionalItemTags.DIAMOND_GEMS)
				.define('G', ConventionalItemTags.GOLD_INGOTS)
				.define('R', ConventionalItemTags.REDSTONE_DUSTS)
				.define('V', ModItems.VOID_UPGRADE.get())
				.unlockedBy("has_void_upgrade", has(ModItems.VOID_UPGRADE.get()))
				.save(recipeOutput);

		ShapeBasedRecipeBuilder.shaped(ModItems.RESTOCK_UPGRADE.get())
				.pattern(" P ")
				.pattern("IBI")
				.pattern("RCR")
				.define('B', ModItems.UPGRADE_BASE.get())
				.define('C', ConventionalItemTags.WOODEN_CHESTS)
				.define('I', ConventionalItemTags.IRON_INGOTS)
				.define('R', ConventionalItemTags.REDSTONE_DUSTS)
				.define('P', Items.STICKY_PISTON)
				.unlockedBy(HAS_UPGRADE_BASE, has(ModItems.UPGRADE_BASE.get()))
				.save(recipeOutput);

		ShapeBasedRecipeBuilder.shaped(ModItems.ADVANCED_RESTOCK_UPGRADE.get(), UpgradeNextTierRecipe::new)
				.pattern(" D ")
				.pattern("GVG")
				.pattern("RRR")
				.define('D', ConventionalItemTags.DIAMOND_GEMS)
				.define('G', ConventionalItemTags.GOLD_INGOTS)
				.define('R', ConventionalItemTags.REDSTONE_DUSTS)
				.define('V', ModItems.RESTOCK_UPGRADE.get())
				.unlockedBy("has_restock_upgrade", has(ModItems.RESTOCK_UPGRADE.get()))
				.save(recipeOutput);

		ShapeBasedRecipeBuilder.shaped(ModItems.DEPOSIT_UPGRADE.get())
				.pattern(" P ")
				.pattern("IBI")
				.pattern("RCR")
				.define('B', ModItems.UPGRADE_BASE.get())
				.define('C', ConventionalItemTags.WOODEN_CHESTS)
				.define('I', ConventionalItemTags.IRON_INGOTS)
				.define('R', ConventionalItemTags.REDSTONE_DUSTS)
				.define('P', Items.PISTON)
				.unlockedBy(HAS_UPGRADE_BASE, has(ModItems.UPGRADE_BASE.get()))
				.save(recipeOutput);

		ShapeBasedRecipeBuilder.shaped(ModItems.ADVANCED_DEPOSIT_UPGRADE.get(), UpgradeNextTierRecipe::new)
				.pattern(" D ")
				.pattern("GVG")
				.pattern("RRR")
				.define('D', ConventionalItemTags.DIAMOND_GEMS)
				.define('G', ConventionalItemTags.GOLD_INGOTS)
				.define('R', ConventionalItemTags.REDSTONE_DUSTS)
				.define('V', ModItems.DEPOSIT_UPGRADE.get())
				.unlockedBy("has_deposit_upgrade", has(ModItems.DEPOSIT_UPGRADE.get()))
				.save(recipeOutput);

		ShapeBasedRecipeBuilder.shaped(ModItems.REFILL_UPGRADE.get())
				.pattern(" E ")
				.pattern("IBI")
				.pattern("RCR")
				.define('B', ModItems.UPGRADE_BASE.get())
				.define('C', ConventionalItemTags.WOODEN_CHESTS)
				.define('I', ConventionalItemTags.IRON_INGOTS)
				.define('R', ConventionalItemTags.REDSTONE_DUSTS)
				.define('E', ConventionalItemTags.ENDER_PEARLS)
				.unlockedBy(HAS_UPGRADE_BASE, has(ModItems.UPGRADE_BASE.get()))
				.save(recipeOutput);

		ShapeBasedRecipeBuilder.shaped(ModItems.ADVANCED_REFILL_UPGRADE.get(), UpgradeNextTierRecipe::new)
				.pattern(" D ")
				.pattern("GFG")
				.pattern("RRR")
				.define('D', ConventionalItemTags.DIAMOND_GEMS)
				.define('G', ConventionalItemTags.GOLD_INGOTS)
				.define('R', ConventionalItemTags.REDSTONE_DUSTS)
				.define('F', ModItems.REFILL_UPGRADE.get())
				.unlockedBy("has_refill_upgrade", has(ModItems.REFILL_UPGRADE.get()))
				.save(recipeOutput);

		ShapeBasedRecipeBuilder.shaped(ModItems.INCEPTION_UPGRADE.get())
				.pattern("ESE")
				.pattern("DBD")
				.pattern("EDE")
				.define('B', ModItems.UPGRADE_BASE.get())
				.define('S', Items.NETHER_STAR)
				.define('D', ConventionalItemTags.DIAMOND_GEMS)
				.define('E', Items.ENDER_EYE)
				.unlockedBy(HAS_UPGRADE_BASE, has(ModItems.UPGRADE_BASE.get()))
				.save(recipeOutput);

		ShapeBasedRecipeBuilder.shaped(ModItems.EVERLASTING_UPGRADE.get())
				.pattern("CSC")
				.pattern("SBS")
				.pattern("CSC")
				.define('B', ModItems.UPGRADE_BASE.get())
				.define('S', Items.NETHER_STAR)
				.define('C', Items.END_CRYSTAL)
				.unlockedBy(HAS_UPGRADE_BASE, has(ModItems.UPGRADE_BASE.get()))
				.save(recipeOutput);

		ShapeBasedRecipeBuilder.shaped(ModItems.SMELTING_UPGRADE.get())
				.pattern("RIR")
				.pattern("IBI")
				.pattern("RFR")
				.define('B', ModItems.UPGRADE_BASE.get())
				.define('R', ConventionalItemTags.REDSTONE_DUSTS)
				.define('I', ConventionalItemTags.IRON_INGOTS)
				.define('F', Items.FURNACE)
				.unlockedBy(HAS_UPGRADE_BASE, has(ModItems.UPGRADE_BASE.get()))
				.save(recipeOutput);

		ShapeBasedRecipeBuilder.shaped(ModItems.AUTO_SMELTING_UPGRADE.get(), UpgradeNextTierRecipe::new)
				.pattern("DHD")
				.pattern("RSH")
				.pattern("GHG")
				.define('D', ConventionalItemTags.DIAMOND_GEMS)
				.define('G', ConventionalItemTags.GOLD_INGOTS)
				.define('R', ConventionalItemTags.REDSTONE_DUSTS)
				.define('H', Items.HOPPER)
				.define('S', ModItems.SMELTING_UPGRADE.get())
				.unlockedBy(HAS_SMELTING_UPGRADE, has(ModItems.SMELTING_UPGRADE.get()))
				.save(recipeOutput);

		ShapeBasedRecipeBuilder.shaped(ModItems.CRAFTING_UPGRADE.get())
				.pattern(" T ")
				.pattern("IBI")
				.pattern(" C ")
				.define('B', ModItems.UPGRADE_BASE.get())
				.define('C', ConventionalItemTags.CHESTS)
				.define('I', ConventionalItemTags.IRON_INGOTS)
				.define('T', Items.CRAFTING_TABLE)
				.unlockedBy(HAS_UPGRADE_BASE, has(ModItems.UPGRADE_BASE.get()))
				.save(recipeOutput);

		ShapeBasedRecipeBuilder.shaped(ModItems.STONECUTTER_UPGRADE.get())
				.pattern(" S ")
				.pattern("IBI")
				.pattern(" R ")
				.define('B', ModItems.UPGRADE_BASE.get())
				.define('R', ConventionalItemTags.REDSTONE_DUSTS)
				.define('I', ConventionalItemTags.IRON_INGOTS)
				.define('S', Items.STONECUTTER)
				.unlockedBy(HAS_UPGRADE_BASE, has(ModItems.UPGRADE_BASE.get()))
				.save(recipeOutput);

		ShapeBasedRecipeBuilder.shaped(ModItems.STACK_UPGRADE_STARTER_TIER.get())
				.pattern("CCC")
				.pattern("CBC")
				.pattern("CCC")
				.define('B', ModItems.UPGRADE_BASE.get())
				.define('C', ConventionalItemTags.STORAGE_BLOCKS_COPPER)
				.unlockedBy(HAS_UPGRADE_BASE, has(ModItems.UPGRADE_BASE.get()))
				.save(recipeOutput);

		ShapeBasedRecipeBuilder.shaped(ModItems.STACK_UPGRADE_TIER_1.get())
				.pattern("III")
				.pattern("IBI")
				.pattern("III")
				.define('B', ModItems.UPGRADE_BASE.get())
				.define('I', ConventionalItemTags.STORAGE_BLOCKS_IRON)
				.unlockedBy(HAS_UPGRADE_BASE, has(ModItems.UPGRADE_BASE.get()))
				.save(recipeOutput);

		ShapeBasedRecipeBuilder.shaped(ModItems.STACK_UPGRADE_TIER_1.get())
				.pattern(" I ")
				.pattern("ISI")
				.pattern(" I ")
				.define('S', ModItems.STACK_UPGRADE_STARTER_TIER.get())
				.define('I', ConventionalItemTags.STORAGE_BLOCKS_IRON)
				.unlockedBy(HAS_UPGRADE_BASE, has(ModItems.UPGRADE_BASE.get()))
				.save(recipeOutput, SophisticatedBackpacks.getRL("stack_upgrade_tier_1_from_starter"));

		ShapeBasedRecipeBuilder.shaped(ModItems.STACK_UPGRADE_TIER_2.get())
				.pattern("GGG")
				.pattern("GSG")
				.pattern("GGG")
				.define('S', ModItems.STACK_UPGRADE_TIER_1.get())
				.define('G', ConventionalItemTags.STORAGE_BLOCKS_GOLD)
				.unlockedBy(HAS_UPGRADE_BASE, has(ModItems.STACK_UPGRADE_TIER_1.get()))
				.save(recipeOutput);

		ShapeBasedRecipeBuilder.shaped(ModItems.STACK_UPGRADE_TIER_3.get())
				.pattern("DDD")
				.pattern("DSD")
				.pattern("DDD")
				.define('S', ModItems.STACK_UPGRADE_TIER_2.get())
				.define('D', ConventionalItemTags.STORAGE_BLOCKS_DIAMOND)
				.unlockedBy(HAS_UPGRADE_BASE, has(ModItems.STACK_UPGRADE_TIER_2.get()))
				.save(recipeOutput);

		ShapeBasedRecipeBuilder.shaped(ModItems.STACK_UPGRADE_TIER_4.get())
				.pattern("NNN")
				.pattern("NSN")
				.pattern("NNN")
				.define('S', ModItems.STACK_UPGRADE_TIER_3.get())
				.define('N', ConventionalItemTags.STORAGE_BLOCKS_NETHERITE)
				.unlockedBy(HAS_UPGRADE_BASE, has(ModItems.STACK_UPGRADE_TIER_3.get()))
				.save(recipeOutput);

		ShapeBasedRecipeBuilder.shaped(ModItems.JUKEBOX_UPGRADE.get())
				.pattern(" J ")
				.pattern("IBI")
				.pattern(" R ")
				.define('B', ModItems.UPGRADE_BASE.get())
				.define('R', ConventionalItemTags.REDSTONE_DUSTS)
				.define('I', ConventionalItemTags.IRON_INGOTS)
				.define('J', Items.JUKEBOX)
				.unlockedBy(HAS_UPGRADE_BASE, has(ModItems.UPGRADE_BASE.get()))
				.save(recipeOutput);

		ShapeBasedRecipeBuilder.shaped(ModItems.TOOL_SWAPPER_UPGRADE.get())
				.pattern("RWR")
				.pattern("PBA")
				.pattern("ISI")
				.define('B', ModItems.UPGRADE_BASE.get())
				.define('S', Items.WOODEN_SHOVEL)
				.define('P', Items.WOODEN_PICKAXE)
				.define('A', Items.WOODEN_AXE)
				.define('W', Items.WOODEN_SWORD)
				.define('I', ConventionalItemTags.IRON_INGOTS)
				.define('R', ConventionalItemTags.REDSTONE_DUSTS)
				.unlockedBy(HAS_UPGRADE_BASE, has(ModItems.UPGRADE_BASE.get()))
				.save(recipeOutput);

		ShapeBasedRecipeBuilder.shaped(ModItems.ADVANCED_TOOL_SWAPPER_UPGRADE.get(), UpgradeNextTierRecipe::new)
				.pattern(" D ")
				.pattern("GVG")
				.pattern("RRR")
				.define('D', ConventionalItemTags.DIAMOND_GEMS)
				.define('G', ConventionalItemTags.GOLD_INGOTS)
				.define('R', ConventionalItemTags.REDSTONE_DUSTS)
				.define('V', ModItems.TOOL_SWAPPER_UPGRADE.get())
				.unlockedBy("has_tool_swapper_upgrade", has(ModItems.TOOL_SWAPPER_UPGRADE.get()))
				.save(recipeOutput);

		ShapeBasedRecipeBuilder.shaped(ModItems.TANK_UPGRADE.get(), UpgradeNextTierRecipe::new)
				.pattern("GGG")
				.pattern("GBG")
				.pattern("GGG")
				.define('G', ConventionalItemTags.GLASS_BLOCKS)
				.define('B', ModItems.UPGRADE_BASE.get())
				.unlockedBy(HAS_UPGRADE_BASE, has(ModItems.UPGRADE_BASE.get()))
				.save(recipeOutput);

		ShapeBasedRecipeBuilder.shaped(ModItems.ADVANCED_FEEDING_UPGRADE.get(), UpgradeNextTierRecipe::new)
				.pattern(" D ")
				.pattern("GVG")
				.pattern("RRR")
				.define('D', ConventionalItemTags.DIAMOND_GEMS)
				.define('G', ConventionalItemTags.GOLD_INGOTS)
				.define('R', ConventionalItemTags.REDSTONE_DUSTS)
				.define('V', ModItems.FEEDING_UPGRADE.get())
				.unlockedBy("has_feeding_upgrade", has(ModItems.FEEDING_UPGRADE.get()))
				.save(recipeOutput);

		ShapeBasedRecipeBuilder.shaped(ModItems.BATTERY_UPGRADE.get(), UpgradeNextTierRecipe::new)
				.pattern("GRG")
				.pattern("RBR")
				.pattern("GRG")
				.define('R', ConventionalItemTags.STORAGE_BLOCKS_REDSTONE)
				.define('G', ConventionalItemTags.GOLD_INGOTS)
				.define('B', ModItems.UPGRADE_BASE.get())
				.unlockedBy(HAS_UPGRADE_BASE, has(ModItems.UPGRADE_BASE.get()))
				.save(recipeOutput);

		ShapeBasedRecipeBuilder.shaped(ModItems.PUMP_UPGRADE.get(), UpgradeNextTierRecipe::new)
				.pattern("GUG")
				.pattern("PBS")
				.pattern("GUG")
				.define('U', Items.BUCKET)
				.define('G', ConventionalItemTags.GLASS_BLOCKS)
				.define('P', Items.PISTON)
				.define('S', Items.STICKY_PISTON)
				.define('B', ModItems.UPGRADE_BASE.get())
				.unlockedBy(HAS_UPGRADE_BASE, has(ModItems.UPGRADE_BASE.get()))
				.save(recipeOutput);

		ShapeBasedRecipeBuilder.shaped(ModItems.ADVANCED_PUMP_UPGRADE.get(), UpgradeNextTierRecipe::new)
				.pattern("DID")
				.pattern("GPG")
				.pattern("RRR")
				.define('I', Items.DISPENSER)
				.define('D', ConventionalItemTags.DIAMOND_GEMS)
				.define('G', ConventionalItemTags.GOLD_INGOTS)
				.define('R', ConventionalItemTags.REDSTONE_DUSTS)
				.define('P', ModItems.PUMP_UPGRADE.get())
				.unlockedBy("has_pump_upgrade", has(ModItems.PUMP_UPGRADE.get()))
				.save(recipeOutput);

		ShapeBasedRecipeBuilder.shaped(ModItems.XP_PUMP_UPGRADE.get())
				.pattern("RER")
				.pattern("CPC")
				.pattern("RER")
				.define('R', ConventionalItemTags.REDSTONE_DUSTS)
				.define('E', Items.ENDER_EYE)
				.define('C', Items.EXPERIENCE_BOTTLE)
				.define('P', ModItems.ADVANCED_PUMP_UPGRADE.get())
				.unlockedBy("has_advanced_pump_upgrade", has(ModItems.ADVANCED_PUMP_UPGRADE.get()))
				.save(recipeOutput);

		ShapeBasedRecipeBuilder.shaped(ModItems.SMOKING_UPGRADE.get())
				.pattern("RIR")
				.pattern("IBI")
				.pattern("RSR")
				.define('B', ModItems.UPGRADE_BASE.get())
				.define('R', ConventionalItemTags.REDSTONE_DUSTS)
				.define('I', ConventionalItemTags.IRON_INGOTS)
				.define('S', Items.SMOKER)
				.unlockedBy(HAS_UPGRADE_BASE, has(ModItems.UPGRADE_BASE.get()))
				.save(recipeOutput);

		ShapeBasedRecipeBuilder.shaped(ModItems.SMOKING_UPGRADE.get())
				.pattern(" L ")
				.pattern("LSL")
				.pattern(" L ")
				.define('S', ModItems.SMELTING_UPGRADE.get())
				.define('L', ItemTags.LOGS)
				.unlockedBy(HAS_SMELTING_UPGRADE, has(ModItems.SMELTING_UPGRADE.get()))
				.save(recipeOutput, SophisticatedBackpacks.getRL("smoking_upgrade_from_smelting_upgrade"));

		ShapeBasedRecipeBuilder.shaped(ModItems.AUTO_SMOKING_UPGRADE.get(), UpgradeNextTierRecipe::new)
				.pattern("DHD")
				.pattern("RSH")
				.pattern("GHG")
				.define('D', ConventionalItemTags.DIAMOND_GEMS)
				.define('G', ConventionalItemTags.GOLD_INGOTS)
				.define('R', ConventionalItemTags.REDSTONE_DUSTS)
				.define('H', Items.HOPPER)
				.define('S', ModItems.SMOKING_UPGRADE.get())
				.unlockedBy("has_smoking_upgrade", has(ModItems.SMOKING_UPGRADE.get()))
				.save(recipeOutput);

		ShapeBasedRecipeBuilder.shaped(ModItems.AUTO_SMOKING_UPGRADE.get())
				.pattern(" L ")
				.pattern("LSL")
				.pattern(" L ")
				.define('S', ModItems.AUTO_SMELTING_UPGRADE.get())
				.define('L', ItemTags.LOGS)
				.unlockedBy("has_auto_smelting_upgrade", has(ModItems.AUTO_SMELTING_UPGRADE.get()))
				.save(recipeOutput, SophisticatedBackpacks.getRL("auto_smoking_upgrade_from_auto_smelting_upgrade"));

		ShapeBasedRecipeBuilder.shaped(ModItems.BLASTING_UPGRADE.get())
				.pattern("RIR")
				.pattern("IBI")
				.pattern("RFR")
				.define('B', ModItems.UPGRADE_BASE.get())
				.define('R', ConventionalItemTags.REDSTONE_DUSTS)
				.define('I', ConventionalItemTags.IRON_INGOTS)
				.define('F', Items.BLAST_FURNACE)
				.unlockedBy(HAS_UPGRADE_BASE, has(ModItems.UPGRADE_BASE.get()))
				.save(recipeOutput);

		ShapeBasedRecipeBuilder.shaped(ModItems.BLASTING_UPGRADE.get())
				.pattern("III")
				.pattern("ISI")
				.pattern("TTT")
				.define('S', ModItems.SMELTING_UPGRADE.get())
				.define('I', ConventionalItemTags.IRON_INGOTS)
				.define('T', Items.SMOOTH_STONE)
				.unlockedBy(HAS_SMELTING_UPGRADE, has(ModItems.SMELTING_UPGRADE.get()))
				.save(recipeOutput, SophisticatedBackpacks.getRL("blasting_upgrade_from_smelting_upgrade"));

		ShapeBasedRecipeBuilder.shaped(ModItems.AUTO_BLASTING_UPGRADE.get(), UpgradeNextTierRecipe::new)
				.pattern("DHD")
				.pattern("RSH")
				.pattern("GHG")
				.define('D', ConventionalItemTags.DIAMOND_GEMS)
				.define('G', ConventionalItemTags.GOLD_INGOTS)
				.define('R', ConventionalItemTags.REDSTONE_DUSTS)
				.define('H', Items.HOPPER)
				.define('S', ModItems.BLASTING_UPGRADE.get())
				.unlockedBy("has_blasting_upgrade", has(ModItems.BLASTING_UPGRADE.get()))
				.save(recipeOutput);

		ShapeBasedRecipeBuilder.shaped(ModItems.AUTO_BLASTING_UPGRADE.get())
				.pattern("III")
				.pattern("ISI")
				.pattern("TTT")
				.define('S', ModItems.AUTO_SMELTING_UPGRADE.get())
				.define('I', ConventionalItemTags.IRON_INGOTS)
				.define('T', Items.SMOOTH_STONE)
				.unlockedBy("has_auto_smelting_upgrade", has(ModItems.AUTO_SMELTING_UPGRADE.get()))
				.save(recipeOutput, SophisticatedBackpacks.getRL("auto_blasting_upgrade_from_auto_smelting_upgrade"));

		ShapeBasedRecipeBuilder.shaped(ModItems.ANVIL_UPGRADE.get())
				.pattern("ADA")
				.pattern("IBI")
				.pattern(" C ")
				.define('A', Items.ANVIL)
				.define('D', ConventionalItemTags.DIAMOND_GEMS)
				.define('I', ConventionalItemTags.IRON_INGOTS)
				.define('B', ModItems.UPGRADE_BASE.get())
				.define('C', ConventionalItemTags.WOODEN_CHESTS)
				.unlockedBy(HAS_UPGRADE_BASE, has(ModItems.UPGRADE_BASE.get()))
				.save(recipeOutput);

		new SmithingBackpackUpgradeRecipeBuilder(SmithingBackpackUpgradeRecipe::new, Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE), Ingredient.of(ModItems.DIAMOND_BACKPACK.get()),
				Ingredient.of(Items.NETHERITE_INGOT), ModItems.NETHERITE_BACKPACK.get())
				.unlocks("has_diamond_backpack", has(ModItems.DIAMOND_BACKPACK.get()))
				.save(recipeOutput, RegistryHelper.getItemKey(ModItems.NETHERITE_BACKPACK.get()));

		addChippedUpgradeRecipes(recipeOutput);
	}

	private void addChippedUpgradeRecipes(RecipeOutput recipeOutput) {
		addChippedUpgradeRecipe(recipeOutput, ChippedCompat.BOTANIST_WORKBENCH_UPGRADE.get(), earth.terrarium.chipped.common.registry.ModBlocks.BOTANIST_WORKBENCH.get());
		addChippedUpgradeRecipe(recipeOutput, ChippedCompat.GLASSBLOWER_UPGRADE.get(), earth.terrarium.chipped.common.registry.ModBlocks.GLASSBLOWER.get());
		addChippedUpgradeRecipe(recipeOutput, ChippedCompat.CARPENTERS_TABLE_UPGRADE.get(), earth.terrarium.chipped.common.registry.ModBlocks.CARPENTERS_TABLE.get());
		addChippedUpgradeRecipe(recipeOutput, ChippedCompat.LOOM_TABLE_UPGRADE.get(), earth.terrarium.chipped.common.registry.ModBlocks.LOOM_TABLE.get());
		addChippedUpgradeRecipe(recipeOutput, ChippedCompat.MASON_TABLE_UPGRADE.get(), earth.terrarium.chipped.common.registry.ModBlocks.MASON_TABLE.get());
		addChippedUpgradeRecipe(recipeOutput, ChippedCompat.ALCHEMY_BENCH_UPGRADE.get(), earth.terrarium.chipped.common.registry.ModBlocks.ALCHEMY_BENCH.get());
		addChippedUpgradeRecipe(recipeOutput, ChippedCompat.TINKERING_TABLE_UPGRADE.get(), earth.terrarium.chipped.common.registry.ModBlocks.TINKERING_TABLE.get());
	}

	private void addChippedUpgradeRecipe(RecipeOutput recipeOutput, BlockTransformationUpgradeItem upgrade, Block workbench) {
		ShapeBasedRecipeBuilder.shaped(upgrade)
				.pattern(" W ")
				.pattern("IBI")
				.pattern(" R ")
				.define('B', ModItems.UPGRADE_BASE.get())
				.define('R', ConventionalItemTags.REDSTONE_DUSTS)
				.define('I', ConventionalItemTags.IRON_INGOTS)
				.define('W', workbench)
				.unlockedBy(HAS_UPGRADE_BASE, has(ModItems.UPGRADE_BASE.get()))
				.save(withConditions(recipeOutput, ResourceConditions.allModsLoaded(CompatModIds.CHIPPED)));
	}

	private static Criterion<?> hasLeather() {
		return inventoryTrigger(ItemPredicate.Builder.item().of(ConventionalItemTags.LEATHERS).build());
	}
}
