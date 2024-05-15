package net.p3pp3rf1y.sophisticatedbackpacks.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.resource.conditions.v1.DefaultResourceConditions;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraft.data.recipes.UpgradeRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.compat.CompatModIds;
import net.p3pp3rf1y.sophisticatedbackpacks.compat.chipped.ChippedCompat;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems;
import net.p3pp3rf1y.sophisticatedcore.api.Tags;
import net.p3pp3rf1y.sophisticatedcore.compat.chipped.BlockTransformationUpgradeItem;
import net.p3pp3rf1y.sophisticatedcore.crafting.ShapeBasedRecipeBuilder;
import net.p3pp3rf1y.sophisticatedcore.init.ModRecipes;
import net.p3pp3rf1y.sophisticatedcore.util.RegistryHelper;

import java.util.function.Consumer;

public class SBPRecipeProvider extends FabricRecipeProvider {
	private static final String HAS_UPGRADE_BASE = "has_upgrade_base";
	private static final String HAS_SMELTING_UPGRADE = "has_smelting_upgrade";

	public SBPRecipeProvider(FabricDataGenerator output) {
		super(output);
	}

	@Override
	public void generateRecipes(Consumer<FinishedRecipe> consumer) {
		ShapeBasedRecipeBuilder.shaped(ModItems.BACKPACK, ModItems.BASIC_BACKPACK_RECIPE_SERIALIZER)
				.pattern("SLS")
				.pattern("SCS")
				.pattern("LLL")
				.define('L', Items.LEATHER)
				.define('C', Tags.Items.WOODEN_CHESTS)
				.define('S', Items.STRING)
				.unlockedBy("has_leather", hasLeather())
				.save(consumer);

		SpecialRecipeBuilder.special(ModItems.BACKPACK_DYE_RECIPE_SERIALIZER).save(consumer, SophisticatedBackpacks.getRegistryName("backpack_dye"));

		ShapeBasedRecipeBuilder.shaped(ModItems.DIAMOND_BACKPACK, ModItems.BACKPACK_UPGRADE_RECIPE_SERIALIZER)
				.pattern("DDD")
				.pattern("DBD")
				.pattern("DDD")
				.define('D', ConventionalItemTags.DIAMONDS)
				.define('B', ModItems.GOLD_BACKPACK)
				.unlockedBy("has_gold_backpack", has(ModItems.GOLD_BACKPACK))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(ModItems.GOLD_BACKPACK, ModItems.BACKPACK_UPGRADE_RECIPE_SERIALIZER)
				.pattern("GGG")
				.pattern("GBG")
				.pattern("GGG")
				.define('G', ConventionalItemTags.GOLD_INGOTS)
				.define('B', ModItems.IRON_BACKPACK)
				.unlockedBy("has_iron_backpack", has(ModItems.IRON_BACKPACK))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(ModItems.IRON_BACKPACK, ModItems.BACKPACK_UPGRADE_RECIPE_SERIALIZER)
				.pattern("III")
				.pattern("IBI")
				.pattern("III")
				.define('I', ConventionalItemTags.IRON_INGOTS)
				.define('B', ModItems.BACKPACK)
				.unlockedBy("has_backpack", has(ModItems.BACKPACK))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(ModItems.PICKUP_UPGRADE)
				.pattern(" P ")
				.pattern("SBS")
				.pattern("RRR")
				.define('B', ModItems.UPGRADE_BASE)
				.define('R', ConventionalItemTags.REDSTONE_DUSTS)
				.define('S', Items.STRING)
				.define('P', Blocks.STICKY_PISTON)
				.unlockedBy(HAS_UPGRADE_BASE, has(ModItems.UPGRADE_BASE))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(ModItems.UPGRADE_BASE)
				.pattern("SIS")
				.pattern("ILI")
				.pattern("SIS")
				.define('L', Items.LEATHER)
				.define('I', ConventionalItemTags.IRON_INGOTS)
				.define('S', Items.STRING)
				.unlockedBy("has_leather", hasLeather())
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(ModItems.ADVANCED_PICKUP_UPGRADE, ModRecipes.UPGRADE_NEXT_TIER_SERIALIZER)
				.pattern(" D ")
				.pattern("GPG")
				.pattern("RRR")
				.define('D', ConventionalItemTags.DIAMONDS)
				.define('G', ConventionalItemTags.GOLD_INGOTS)
				.define('R', ConventionalItemTags.REDSTONE_DUSTS)
				.define('P', ModItems.PICKUP_UPGRADE)
				.unlockedBy("has_pickup_upgrade", has(ModItems.PICKUP_UPGRADE))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(ModItems.FILTER_UPGRADE)
				.pattern("RSR")
				.pattern("SBS")
				.pattern("RSR")
				.define('B', ModItems.UPGRADE_BASE)
				.define('R', ConventionalItemTags.REDSTONE_DUSTS)
				.define('S', Items.STRING)
				.unlockedBy(HAS_UPGRADE_BASE, has(ModItems.UPGRADE_BASE))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(ModItems.ADVANCED_FILTER_UPGRADE, ModRecipes.UPGRADE_NEXT_TIER_SERIALIZER)
				.pattern("GPG")
				.pattern("RRR")
				.define('G', ConventionalItemTags.GOLD_INGOTS)
				.define('R', ConventionalItemTags.REDSTONE_DUSTS)
				.define('P', ModItems.FILTER_UPGRADE)
				.unlockedBy("has_filter_upgrade", has(ModItems.FILTER_UPGRADE))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(ModItems.MAGNET_UPGRADE, ModRecipes.UPGRADE_NEXT_TIER_SERIALIZER)
				.pattern("EIE")
				.pattern("IPI")
				.pattern("R L")
				.define('E', Items.ENDER_PEARL)
				.define('I', ConventionalItemTags.IRON_INGOTS)
				.define('R', ConventionalItemTags.REDSTONE_DUSTS)
				.define('L', ConventionalItemTags.LAPIS)
				.define('P', ModItems.PICKUP_UPGRADE)
				.unlockedBy("has_pickup_upgrade", has(ModItems.PICKUP_UPGRADE))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(ModItems.ADVANCED_MAGNET_UPGRADE, ModRecipes.UPGRADE_NEXT_TIER_SERIALIZER)
				.pattern("EIE")
				.pattern("IPI")
				.pattern("R L")
				.define('E', Items.ENDER_PEARL)
				.define('I', ConventionalItemTags.IRON_INGOTS)
				.define('R', ConventionalItemTags.REDSTONE_DUSTS)
				.define('L', ConventionalItemTags.LAPIS)
				.define('P', ModItems.ADVANCED_PICKUP_UPGRADE)
				.unlockedBy("has_advanced_pickup_upgrade", has(ModItems.ADVANCED_PICKUP_UPGRADE))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(ModItems.ADVANCED_MAGNET_UPGRADE, ModRecipes.UPGRADE_NEXT_TIER_SERIALIZER)
				.pattern(" D ")
				.pattern("GMG")
				.pattern("RRR")
				.define('D', ConventionalItemTags.DIAMONDS)
				.define('G', ConventionalItemTags.GOLD_INGOTS)
				.define('R', ConventionalItemTags.REDSTONE_DUSTS)
				.define('M', ModItems.MAGNET_UPGRADE)
				.unlockedBy("has_magnet_upgrade", has(ModItems.MAGNET_UPGRADE))
				.save(consumer, new ResourceLocation(SophisticatedBackpacks.getRegistryName("advanced_magnet_upgrade_from_basic")));

		ShapeBasedRecipeBuilder.shaped(ModItems.FEEDING_UPGRADE)
				.pattern(" C ")
				.pattern("ABM")
				.pattern(" E ")
				.define('B', ModItems.UPGRADE_BASE)
				.define('C', Items.GOLDEN_CARROT)
				.define('A', Items.GOLDEN_APPLE)
				.define('M', Items.GLISTERING_MELON_SLICE)
				.define('E', Items.ENDER_PEARL)
				.unlockedBy(HAS_UPGRADE_BASE, has(ModItems.UPGRADE_BASE))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(ModItems.COMPACTING_UPGRADE)
				.pattern("IPI")
				.pattern("PBP")
				.pattern("RPR")
				.define('B', ModItems.UPGRADE_BASE)
				.define('I', ConventionalItemTags.IRON_INGOTS)
				.define('P', Items.PISTON)
				.define('R', ConventionalItemTags.REDSTONE_DUSTS)
				.unlockedBy(HAS_UPGRADE_BASE, has(ModItems.UPGRADE_BASE))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(ModItems.ADVANCED_COMPACTING_UPGRADE, ModRecipes.UPGRADE_NEXT_TIER_SERIALIZER)
				.pattern(" D ")
				.pattern("GCG")
				.pattern("RRR")
				.define('D', ConventionalItemTags.DIAMONDS)
				.define('G', ConventionalItemTags.GOLD_INGOTS)
				.define('R', ConventionalItemTags.REDSTONE_DUSTS)
				.define('C', ModItems.COMPACTING_UPGRADE)
				.unlockedBy("has_compacting_upgrade", has(ModItems.COMPACTING_UPGRADE))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(ModItems.VOID_UPGRADE)
				.pattern(" E ")
				.pattern("OBO")
				.pattern("ROR")
				.define('B', ModItems.UPGRADE_BASE)
				.define('E', Items.ENDER_PEARL)
				.define('O', Items.OBSIDIAN)
				.define('R', ConventionalItemTags.REDSTONE_DUSTS)
				.unlockedBy(HAS_UPGRADE_BASE, has(ModItems.UPGRADE_BASE))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(ModItems.ADVANCED_VOID_UPGRADE, ModRecipes.UPGRADE_NEXT_TIER_SERIALIZER)
				.pattern(" D ")
				.pattern("GVG")
				.pattern("RRR")
				.define('D', ConventionalItemTags.DIAMONDS)
				.define('G', ConventionalItemTags.GOLD_INGOTS)
				.define('R', ConventionalItemTags.REDSTONE_DUSTS)
				.define('V', ModItems.VOID_UPGRADE)
				.unlockedBy("has_void_upgrade", has(ModItems.VOID_UPGRADE))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(ModItems.RESTOCK_UPGRADE)
				.pattern(" P ")
				.pattern("IBI")
				.pattern("RCR")
				.define('B', ModItems.UPGRADE_BASE)
				.define('C', Tags.Items.WOODEN_CHESTS)
				.define('I', ConventionalItemTags.IRON_INGOTS)
				.define('R', ConventionalItemTags.REDSTONE_DUSTS)
				.define('P', Items.STICKY_PISTON)
				.unlockedBy(HAS_UPGRADE_BASE, has(ModItems.UPGRADE_BASE))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(ModItems.ADVANCED_RESTOCK_UPGRADE, ModRecipes.UPGRADE_NEXT_TIER_SERIALIZER)
				.pattern(" D ")
				.pattern("GVG")
				.pattern("RRR")
				.define('D', ConventionalItemTags.DIAMONDS)
				.define('G', ConventionalItemTags.GOLD_INGOTS)
				.define('R', ConventionalItemTags.REDSTONE_DUSTS)
				.define('V', ModItems.RESTOCK_UPGRADE)
				.unlockedBy("has_restock_upgrade", has(ModItems.RESTOCK_UPGRADE))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(ModItems.DEPOSIT_UPGRADE)
				.pattern(" P ")
				.pattern("IBI")
				.pattern("RCR")
				.define('B', ModItems.UPGRADE_BASE)
				.define('C', Tags.Items.WOODEN_CHESTS)
				.define('I', ConventionalItemTags.IRON_INGOTS)
				.define('R', ConventionalItemTags.REDSTONE_DUSTS)
				.define('P', Items.PISTON)
				.unlockedBy(HAS_UPGRADE_BASE, has(ModItems.UPGRADE_BASE))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(ModItems.ADVANCED_DEPOSIT_UPGRADE, ModRecipes.UPGRADE_NEXT_TIER_SERIALIZER)
				.pattern(" D ")
				.pattern("GVG")
				.pattern("RRR")
				.define('D', ConventionalItemTags.DIAMONDS)
				.define('G', ConventionalItemTags.GOLD_INGOTS)
				.define('R', ConventionalItemTags.REDSTONE_DUSTS)
				.define('V', ModItems.DEPOSIT_UPGRADE)
				.unlockedBy("has_deposit_upgrade", has(ModItems.DEPOSIT_UPGRADE))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(ModItems.REFILL_UPGRADE)
				.pattern(" E ")
				.pattern("IBI")
				.pattern("RCR")
				.define('B', ModItems.UPGRADE_BASE)
				.define('C', Tags.Items.WOODEN_CHESTS)
				.define('I', ConventionalItemTags.IRON_INGOTS)
				.define('R', ConventionalItemTags.REDSTONE_DUSTS)
				.define('E', Items.ENDER_PEARL)
				.unlockedBy(HAS_UPGRADE_BASE, has(ModItems.UPGRADE_BASE))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(ModItems.ADVANCED_REFILL_UPGRADE, ModRecipes.UPGRADE_NEXT_TIER_SERIALIZER)
				.pattern(" D ")
				.pattern("GFG")
				.pattern("RRR")
				.define('D', ConventionalItemTags.DIAMONDS)
				.define('G', ConventionalItemTags.GOLD_INGOTS)
				.define('R', ConventionalItemTags.REDSTONE_DUSTS)
				.define('F', ModItems.REFILL_UPGRADE)
				.unlockedBy("has_refill_upgrade", has(ModItems.REFILL_UPGRADE))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(ModItems.INCEPTION_UPGRADE)
				.pattern("ESE")
				.pattern("DBD")
				.pattern("EDE")
				.define('B', ModItems.UPGRADE_BASE)
				.define('S', Items.NETHER_STAR)
				.define('D', ConventionalItemTags.DIAMONDS)
				.define('E', Items.ENDER_EYE)
				.unlockedBy(HAS_UPGRADE_BASE, has(ModItems.UPGRADE_BASE))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(ModItems.EVERLASTING_UPGRADE)
				.pattern("CSC")
				.pattern("SBS")
				.pattern("CSC")
				.define('B', ModItems.UPGRADE_BASE)
				.define('S', Items.NETHER_STAR)
				.define('C', Items.END_CRYSTAL)
				.unlockedBy(HAS_UPGRADE_BASE, has(ModItems.UPGRADE_BASE))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(ModItems.SMELTING_UPGRADE)
				.pattern("RIR")
				.pattern("IBI")
				.pattern("RFR")
				.define('B', ModItems.UPGRADE_BASE)
				.define('R', ConventionalItemTags.REDSTONE_DUSTS)
				.define('I', ConventionalItemTags.IRON_INGOTS)
				.define('F', Items.FURNACE)
				.unlockedBy(HAS_UPGRADE_BASE, has(ModItems.UPGRADE_BASE))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(ModItems.AUTO_SMELTING_UPGRADE, ModRecipes.UPGRADE_NEXT_TIER_SERIALIZER)
				.pattern("DHD")
				.pattern("RSH")
				.pattern("GHG")
				.define('D', ConventionalItemTags.DIAMONDS)
				.define('G', ConventionalItemTags.GOLD_INGOTS)
				.define('R', ConventionalItemTags.REDSTONE_DUSTS)
				.define('H', Items.HOPPER)
				.define('S', ModItems.SMELTING_UPGRADE)
				.unlockedBy(HAS_SMELTING_UPGRADE, has(ModItems.SMELTING_UPGRADE))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(ModItems.CRAFTING_UPGRADE)
				.pattern(" T ")
				.pattern("IBI")
				.pattern(" C ")
				.define('B', ModItems.UPGRADE_BASE)
				.define('C', Tags.Items.CHESTS)
				.define('I', ConventionalItemTags.IRON_INGOTS)
				.define('T', Items.CRAFTING_TABLE)
				.unlockedBy(HAS_UPGRADE_BASE, has(ModItems.UPGRADE_BASE))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(ModItems.STONECUTTER_UPGRADE)
				.pattern(" S ")
				.pattern("IBI")
				.pattern(" R ")
				.define('B', ModItems.UPGRADE_BASE)
				.define('R', ConventionalItemTags.REDSTONE_DUSTS)
				.define('I', ConventionalItemTags.IRON_INGOTS)
				.define('S', Items.STONECUTTER)
				.unlockedBy(HAS_UPGRADE_BASE, has(ModItems.UPGRADE_BASE))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(ModItems.STACK_UPGRADE_TIER_1)
				.pattern("III")
				.pattern("IBI")
				.pattern("III")
				.define('B', ModItems.UPGRADE_BASE)
				.define('I', Items.IRON_BLOCK)
				.unlockedBy(HAS_UPGRADE_BASE, has(ModItems.UPGRADE_BASE))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(ModItems.STACK_UPGRADE_TIER_2)
				.pattern("GGG")
				.pattern("GSG")
				.pattern("GGG")
				.define('S', ModItems.STACK_UPGRADE_TIER_1)
				.define('G', Items.GOLD_BLOCK)
				.unlockedBy(HAS_UPGRADE_BASE, has(ModItems.STACK_UPGRADE_TIER_1))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(ModItems.STACK_UPGRADE_TIER_3)
				.pattern("DDD")
				.pattern("DSD")
				.pattern("DDD")
				.define('S', ModItems.STACK_UPGRADE_TIER_2)
				.define('D', Items.DIAMOND_BLOCK)
				.unlockedBy(HAS_UPGRADE_BASE, has(ModItems.STACK_UPGRADE_TIER_2))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(ModItems.STACK_UPGRADE_TIER_4)
				.pattern("NNN")
				.pattern("NSN")
				.pattern("NNN")
				.define('S', ModItems.STACK_UPGRADE_TIER_3)
				.define('N', Items.NETHERITE_BLOCK)
				.unlockedBy(HAS_UPGRADE_BASE, has(ModItems.STACK_UPGRADE_TIER_3))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(ModItems.JUKEBOX_UPGRADE)
				.pattern(" J ")
				.pattern("IBI")
				.pattern(" R ")
				.define('B', ModItems.UPGRADE_BASE)
				.define('R', ConventionalItemTags.REDSTONE_DUSTS)
				.define('I', ConventionalItemTags.IRON_INGOTS)
				.define('J', Items.JUKEBOX)
				.unlockedBy(HAS_UPGRADE_BASE, has(ModItems.UPGRADE_BASE))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(ModItems.TOOL_SWAPPER_UPGRADE)
				.pattern("RWR")
				.pattern("PBA")
				.pattern("ISI")
				.define('B', ModItems.UPGRADE_BASE)
				.define('S', Items.WOODEN_SHOVEL)
				.define('P', Items.WOODEN_PICKAXE)
				.define('A', Items.WOODEN_AXE)
				.define('W', Items.WOODEN_SWORD)
				.define('I', ConventionalItemTags.IRON_INGOTS)
				.define('R', ConventionalItemTags.REDSTONE_DUSTS)
				.unlockedBy(HAS_UPGRADE_BASE, has(ModItems.UPGRADE_BASE))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(ModItems.ADVANCED_TOOL_SWAPPER_UPGRADE, ModRecipes.UPGRADE_NEXT_TIER_SERIALIZER)
				.pattern(" D ")
				.pattern("GVG")
				.pattern("RRR")
				.define('D', ConventionalItemTags.DIAMONDS)
				.define('G', ConventionalItemTags.GOLD_INGOTS)
				.define('R', ConventionalItemTags.REDSTONE_DUSTS)
				.define('V', ModItems.TOOL_SWAPPER_UPGRADE)
				.unlockedBy("has_tool_swapper_upgrade", has(ModItems.TOOL_SWAPPER_UPGRADE))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(ModItems.TANK_UPGRADE, ModRecipes.UPGRADE_NEXT_TIER_SERIALIZER)
				.pattern("GGG")
				.pattern("GBG")
				.pattern("GGG")
				.define('G', ConventionalItemTags.GLASS_BLOCKS)
				.define('B', ModItems.UPGRADE_BASE)
				.unlockedBy(HAS_UPGRADE_BASE, has(ModItems.UPGRADE_BASE))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(ModItems.ADVANCED_FEEDING_UPGRADE, ModRecipes.UPGRADE_NEXT_TIER_SERIALIZER)
				.pattern(" D ")
				.pattern("GVG")
				.pattern("RRR")
				.define('D', ConventionalItemTags.DIAMONDS)
				.define('G', ConventionalItemTags.GOLD_INGOTS)
				.define('R', ConventionalItemTags.REDSTONE_DUSTS)
				.define('V', ModItems.FEEDING_UPGRADE)
				.unlockedBy("has_feeding_upgrade", has(ModItems.FEEDING_UPGRADE))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(ModItems.BATTERY_UPGRADE, ModRecipes.UPGRADE_NEXT_TIER_SERIALIZER)
				.pattern("GRG")
				.pattern("RBR")
				.pattern("GRG")
				.define('R', Items.REDSTONE_BLOCK)
				.define('G', ConventionalItemTags.GOLD_INGOTS)
				.define('B', ModItems.UPGRADE_BASE)
				.unlockedBy(HAS_UPGRADE_BASE, has(ModItems.UPGRADE_BASE))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(ModItems.PUMP_UPGRADE, ModRecipes.UPGRADE_NEXT_TIER_SERIALIZER)
				.pattern("GUG")
				.pattern("PBS")
				.pattern("GUG")
				.define('U', Items.BUCKET)
				.define('G', ConventionalItemTags.GLASS_BLOCKS)
				.define('P', Items.PISTON)
				.define('S', Items.STICKY_PISTON)
				.define('B', ModItems.UPGRADE_BASE)
				.unlockedBy(HAS_UPGRADE_BASE, has(ModItems.UPGRADE_BASE))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(ModItems.ADVANCED_PUMP_UPGRADE, ModRecipes.UPGRADE_NEXT_TIER_SERIALIZER)
				.pattern("DID")
				.pattern("GPG")
				.pattern("RRR")
				.define('I', Items.DISPENSER)
				.define('D', ConventionalItemTags.DIAMONDS)
				.define('G', ConventionalItemTags.GOLD_INGOTS)
				.define('R', ConventionalItemTags.REDSTONE_DUSTS)
				.define('P', ModItems.PUMP_UPGRADE)
				.unlockedBy("has_pump_upgrade", has(ModItems.PUMP_UPGRADE))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(ModItems.XP_PUMP_UPGRADE)
				.pattern("RER")
				.pattern("CPC")
				.pattern("RER")
				.define('R', ConventionalItemTags.REDSTONE_DUSTS)
				.define('E', Items.ENDER_EYE)
				.define('C', Items.EXPERIENCE_BOTTLE)
				.define('P', ModItems.ADVANCED_PUMP_UPGRADE)
				.unlockedBy("has_advanced_pump_upgrade", has(ModItems.ADVANCED_PUMP_UPGRADE))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(ModItems.SMOKING_UPGRADE)
				.pattern("RIR")
				.pattern("IBI")
				.pattern("RSR")
				.define('B', ModItems.UPGRADE_BASE)
				.define('R', ConventionalItemTags.REDSTONE_DUSTS)
				.define('I', ConventionalItemTags.IRON_INGOTS)
				.define('S', Items.SMOKER)
				.unlockedBy(HAS_UPGRADE_BASE, has(ModItems.UPGRADE_BASE))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(ModItems.SMOKING_UPGRADE)
				.pattern(" L ")
				.pattern("LSL")
				.pattern(" L ")
				.define('S', ModItems.SMELTING_UPGRADE)
				.define('L', net.minecraft.tags.ItemTags.LOGS)
				.unlockedBy(HAS_SMELTING_UPGRADE, has(ModItems.SMELTING_UPGRADE))
				.save(consumer, SophisticatedBackpacks.getRL("smoking_upgrade_from_smelting_upgrade"));

		ShapeBasedRecipeBuilder.shaped(ModItems.AUTO_SMOKING_UPGRADE, ModRecipes.UPGRADE_NEXT_TIER_SERIALIZER)
				.pattern("DHD")
				.pattern("RSH")
				.pattern("GHG")
				.define('D', ConventionalItemTags.DIAMONDS)
				.define('G', ConventionalItemTags.GOLD_INGOTS)
				.define('R', ConventionalItemTags.REDSTONE_DUSTS)
				.define('H', Items.HOPPER)
				.define('S', ModItems.SMOKING_UPGRADE)
				.unlockedBy("has_smoking_upgrade", has(ModItems.SMOKING_UPGRADE))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(ModItems.AUTO_SMOKING_UPGRADE)
				.pattern(" L ")
				.pattern("LSL")
				.pattern(" L ")
				.define('S', ModItems.AUTO_SMELTING_UPGRADE)
				.define('L', net.minecraft.tags.ItemTags.LOGS)
				.unlockedBy("has_auto_smelting_upgrade", has(ModItems.AUTO_SMELTING_UPGRADE))
				.save(consumer, SophisticatedBackpacks.getRL("auto_smoking_upgrade_from_auto_smelting_upgrade"));

		ShapeBasedRecipeBuilder.shaped(ModItems.BLASTING_UPGRADE)
				.pattern("RIR")
				.pattern("IBI")
				.pattern("RFR")
				.define('B', ModItems.UPGRADE_BASE)
				.define('R', ConventionalItemTags.REDSTONE_DUSTS)
				.define('I', ConventionalItemTags.IRON_INGOTS)
				.define('F', Items.BLAST_FURNACE)
				.unlockedBy(HAS_UPGRADE_BASE, has(ModItems.UPGRADE_BASE))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(ModItems.BLASTING_UPGRADE)
				.pattern("III")
				.pattern("ISI")
				.pattern("TTT")
				.define('S', ModItems.SMELTING_UPGRADE)
				.define('I', ConventionalItemTags.IRON_INGOTS)
				.define('T', Items.SMOOTH_STONE)
				.unlockedBy(HAS_SMELTING_UPGRADE, has(ModItems.SMELTING_UPGRADE))
				.save(consumer, SophisticatedBackpacks.getRL("blasting_upgrade_from_smelting_upgrade"));

		ShapeBasedRecipeBuilder.shaped(ModItems.AUTO_BLASTING_UPGRADE, ModRecipes.UPGRADE_NEXT_TIER_SERIALIZER)
				.pattern("DHD")
				.pattern("RSH")
				.pattern("GHG")
				.define('D', ConventionalItemTags.DIAMONDS)
				.define('G', ConventionalItemTags.GOLD_INGOTS)
				.define('R', ConventionalItemTags.REDSTONE_DUSTS)
				.define('H', Items.HOPPER)
				.define('S', ModItems.BLASTING_UPGRADE)
				.unlockedBy("has_blasting_upgrade", has(ModItems.BLASTING_UPGRADE))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(ModItems.AUTO_BLASTING_UPGRADE)
				.pattern("III")
				.pattern("ISI")
				.pattern("TTT")
				.define('S', ModItems.AUTO_SMELTING_UPGRADE)
				.define('I', ConventionalItemTags.IRON_INGOTS)
				.define('T', Items.SMOOTH_STONE)
				.unlockedBy("has_auto_smelting_upgrade", has(ModItems.AUTO_SMELTING_UPGRADE))
				.save(consumer, SophisticatedBackpacks.getRL("auto_blasting_upgrade_from_auto_smelting_upgrade"));

		ShapeBasedRecipeBuilder.shaped(ModItems.ANVIL_UPGRADE)
				.pattern("ADA")
				.pattern("IBI")
				.pattern(" C ")
				.define('A', Items.ANVIL)
				.define('D', ConventionalItemTags.DIAMONDS)
				.define('I', ConventionalItemTags.IRON_INGOTS)
				.define('B', ModItems.UPGRADE_BASE)
				.define('C', Tags.Items.WOODEN_CHESTS)
				.unlockedBy(HAS_UPGRADE_BASE, has(ModItems.UPGRADE_BASE))
				.save(consumer);

		new UpgradeRecipeBuilder(ModItems.SMITHING_BACKPACK_UPGRADE_RECIPE_SERIALIZER, Ingredient.of(ModItems.DIAMOND_BACKPACK),
				Ingredient.of(Items.NETHERITE_INGOT), ModItems.NETHERITE_BACKPACK)
				.unlocks("has_diamond_backpack", has(ModItems.DIAMOND_BACKPACK))
				.save(consumer, RegistryHelper.getItemKey(ModItems.NETHERITE_BACKPACK));

		addChippedUpgradeRecipes(consumer);
	}

	private static void addChippedUpgradeRecipes(Consumer<FinishedRecipe> consumer) {
		addChippedUpgradeRecipe(consumer, ChippedCompat.BOTANIST_WORKBENCH_UPGRADE, earth.terrarium.chipped.common.registry.ModBlocks.BOTANIST_WORKBENCH.get());
		addChippedUpgradeRecipe(consumer, ChippedCompat.GLASSBLOWER_WORKBENCH_UPGRADE, earth.terrarium.chipped.common.registry.ModBlocks.GLASSBLOWER.get());
		addChippedUpgradeRecipe(consumer, ChippedCompat.CARPENTER_WORKBENCH_UPGRADE, earth.terrarium.chipped.common.registry.ModBlocks.CARPENTERS_TABLE.get());
		addChippedUpgradeRecipe(consumer, ChippedCompat.SHEPHERD_WORKBENCH_UPGRADE, earth.terrarium.chipped.common.registry.ModBlocks.LOOM_TABLE.get());
		addChippedUpgradeRecipe(consumer, ChippedCompat.MASON_WORKBENCH_UPGRADE, earth.terrarium.chipped.common.registry.ModBlocks.MASON_TABLE.get());
		addChippedUpgradeRecipe(consumer, ChippedCompat.PHILOSOPHER_WORKBENCH_UPGRADE, earth.terrarium.chipped.common.registry.ModBlocks.ALCHEMY_BENCH.get());
		addChippedUpgradeRecipe(consumer, ChippedCompat.TINKERER_WORKBENCH_UPGRADE, earth.terrarium.chipped.common.registry.ModBlocks.TINKERING_TABLE.get());
	}

	private static void addChippedUpgradeRecipe(Consumer<FinishedRecipe> consumer, BlockTransformationUpgradeItem upgrade, Block workbench) {
		ShapeBasedRecipeBuilder.shaped(upgrade)
				.pattern(" W ")
				.pattern("IBI")
				.pattern(" R ")
				.define('B', ModItems.UPGRADE_BASE)
				.define('R', ConventionalItemTags.REDSTONE_DUSTS)
				.define('I', ConventionalItemTags.IRON_INGOTS)
				.define('W', workbench)
				.unlockedBy(HAS_UPGRADE_BASE, has(ModItems.UPGRADE_BASE))
				.condition(DefaultResourceConditions.allModsLoaded(CompatModIds.CHIPPED))
				.save(consumer);
	}

	private static InventoryChangeTrigger.TriggerInstance hasLeather() {
		return inventoryTrigger(ItemPredicate.Builder.item().of(Items.LEATHER).build());
	}
}
