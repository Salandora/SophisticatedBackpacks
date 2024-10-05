package net.p3pp3rf1y.sophisticatedbackpacks.compat.chipped;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.p3pp3rf1y.sophisticatedbackpacks.Config;
import net.p3pp3rf1y.sophisticatedbackpacks.compat.emi.EmiCompat;
import net.p3pp3rf1y.sophisticatedbackpacks.compat.jei.SBPPlugin;
import net.p3pp3rf1y.sophisticatedbackpacks.compat.rei.REIClientCompat;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems;
import net.p3pp3rf1y.sophisticatedcore.common.gui.UpgradeContainerRegistry;
import net.p3pp3rf1y.sophisticatedcore.common.gui.UpgradeContainerType;
import net.p3pp3rf1y.sophisticatedcore.compat.CompatModIds;
import net.p3pp3rf1y.sophisticatedcore.compat.ICompat;
import net.p3pp3rf1y.sophisticatedcore.compat.chipped.BlockTransformationUpgradeContainer;
import net.p3pp3rf1y.sophisticatedcore.compat.chipped.BlockTransformationUpgradeItem;
import net.p3pp3rf1y.sophisticatedcore.compat.chipped.BlockTransformationUpgradeWrapper;
import earth.terrarium.chipped.common.compat.jei.WorkbenchCategory;
import earth.terrarium.chipped.common.registry.ModBlocks;
import earth.terrarium.chipped.common.registry.ModRecipeTypes;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;

public class ChippedCompat implements ICompat {

	public static final BlockTransformationUpgradeItem BOTANIST_WORKBENCH_UPGRADE = ModItems.register("chipped/botanist_workbench_upgrade",
			() -> new BlockTransformationUpgradeItem(ModRecipeTypes.WORKBENCH, Config.SERVER.maxUpgradesPerStorage));
	public static final BlockTransformationUpgradeItem GLASSBLOWER_UPGRADE = ModItems.register("chipped/glassblower_upgrade",
			() -> new BlockTransformationUpgradeItem(ModRecipeTypes.WORKBENCH, Config.SERVER.maxUpgradesPerStorage));
	public static final BlockTransformationUpgradeItem CARPENTERS_TABLE_UPGRADE = ModItems.register("chipped/carpenters_table_upgrade",
			() -> new BlockTransformationUpgradeItem(ModRecipeTypes.WORKBENCH, Config.SERVER.maxUpgradesPerStorage));
	public static final BlockTransformationUpgradeItem LOOM_TABLE_UPGRADE = ModItems.register("chipped/loom_table_upgrade",
			() -> new BlockTransformationUpgradeItem(ModRecipeTypes.WORKBENCH, Config.SERVER.maxUpgradesPerStorage));
	public static final BlockTransformationUpgradeItem MASON_TABLE_UPGRADE = ModItems.register("chipped/mason_table_upgrade",
			() -> new BlockTransformationUpgradeItem(ModRecipeTypes.WORKBENCH, Config.SERVER.maxUpgradesPerStorage));
	public static final BlockTransformationUpgradeItem ALCHEMY_BENCH_UPGRADE = ModItems.register("chipped/alchemy_bench_upgrade",
			() -> new BlockTransformationUpgradeItem(ModRecipeTypes.WORKBENCH, Config.SERVER.maxUpgradesPerStorage));
	public static final BlockTransformationUpgradeItem TINKERING_TABLE_UPGRADE = ModItems.register("chipped/tinkering_table_upgrade",
			() -> new BlockTransformationUpgradeItem(ModRecipeTypes.WORKBENCH, Config.SERVER.maxUpgradesPerStorage));

	@Override
	public void init() {
		registerContainers();

		if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT) {
			return;
		}

		if (FabricLoader.getInstance().isModLoaded(CompatModIds.JEI)) {
			SBPPlugin.setAdditionalCatalystRegistrar(registration -> {
				registration.addRecipeCatalyst(new ItemStack(BOTANIST_WORKBENCH_UPGRADE), WorkbenchCategory.RECIPE);
				registration.addRecipeCatalyst(new ItemStack(GLASSBLOWER_UPGRADE), WorkbenchCategory.RECIPE);
				registration.addRecipeCatalyst(new ItemStack(CARPENTERS_TABLE_UPGRADE), WorkbenchCategory.RECIPE);
				registration.addRecipeCatalyst(new ItemStack(LOOM_TABLE_UPGRADE), WorkbenchCategory.RECIPE);
				registration.addRecipeCatalyst(new ItemStack(MASON_TABLE_UPGRADE), WorkbenchCategory.RECIPE);
				registration.addRecipeCatalyst(new ItemStack(ALCHEMY_BENCH_UPGRADE), WorkbenchCategory.RECIPE);
				registration.addRecipeCatalyst(new ItemStack(TINKERING_TABLE_UPGRADE), WorkbenchCategory.RECIPE);
			});
		}

		if (FabricLoader.getInstance().isModLoaded(CompatModIds.REI)) {
			REIClientCompat.setAdditionalCategories(registration -> {
				registration.addWorkstations(CategoryIdentifier.of(ModBlocks.BOTANIST_WORKBENCH.getId()), EntryStacks.of(BOTANIST_WORKBENCH_UPGRADE));
				registration.addWorkstations(CategoryIdentifier.of(ModBlocks.GLASSBLOWER.getId()), EntryStacks.of(GLASSBLOWER_UPGRADE));
				registration.addWorkstations(CategoryIdentifier.of(ModBlocks.CARPENTERS_TABLE.getId()), EntryStacks.of(CARPENTERS_TABLE_UPGRADE));
				registration.addWorkstations(CategoryIdentifier.of(ModBlocks.LOOM_TABLE.getId()), EntryStacks.of(LOOM_TABLE_UPGRADE));
				registration.addWorkstations(CategoryIdentifier.of(ModBlocks.MASON_TABLE.getId()), EntryStacks.of(MASON_TABLE_UPGRADE));
				registration.addWorkstations(CategoryIdentifier.of(ModBlocks.ALCHEMY_BENCH.getId()), EntryStacks.of(ALCHEMY_BENCH_UPGRADE));
				registration.addWorkstations(CategoryIdentifier.of(ModBlocks.TINKERING_TABLE.getId()), EntryStacks.of(TINKERING_TABLE_UPGRADE));
			});
		}

		if (FabricLoader.getInstance().isModLoaded(CompatModIds.EMI)) {
			EmiCompat.WORKSTATIONS.register((consumer -> {
				consumer.accept(new EmiCompat.WorkstationEntry(new ResourceLocation("botanist_workbench"), ModBlocks.BOTANIST_WORKBENCH.get(), BOTANIST_WORKBENCH_UPGRADE));
				consumer.accept(new EmiCompat.WorkstationEntry(new ResourceLocation("glassblower"), ModBlocks.GLASSBLOWER.get(), GLASSBLOWER_UPGRADE));
				consumer.accept(new EmiCompat.WorkstationEntry(new ResourceLocation("carpenters_table"), ModBlocks.CARPENTERS_TABLE.get(), CARPENTERS_TABLE_UPGRADE));
				consumer.accept(new EmiCompat.WorkstationEntry(new ResourceLocation("loom_table"), ModBlocks.LOOM_TABLE.get(), LOOM_TABLE_UPGRADE));
				consumer.accept(new EmiCompat.WorkstationEntry(new ResourceLocation("mason_table"), ModBlocks.MASON_TABLE.get(), MASON_TABLE_UPGRADE));
				consumer.accept(new EmiCompat.WorkstationEntry(new ResourceLocation("alchemy_bench"), ModBlocks.ALCHEMY_BENCH.get(), ALCHEMY_BENCH_UPGRADE));
				consumer.accept(new EmiCompat.WorkstationEntry(new ResourceLocation("tinkering_table"), ModBlocks.TINKERING_TABLE.get(), TINKERING_TABLE_UPGRADE));
			}));
		}
	}

	public void registerContainers() {
		registerUpgradeContainer(BOTANIST_WORKBENCH_UPGRADE);
		registerUpgradeContainer(GLASSBLOWER_UPGRADE);
		registerUpgradeContainer(CARPENTERS_TABLE_UPGRADE);
		registerUpgradeContainer(LOOM_TABLE_UPGRADE);
		registerUpgradeContainer(MASON_TABLE_UPGRADE);
		registerUpgradeContainer(ALCHEMY_BENCH_UPGRADE);
		registerUpgradeContainer(TINKERING_TABLE_UPGRADE);
	}

	private void registerUpgradeContainer(BlockTransformationUpgradeItem item) {
		UpgradeContainerType<BlockTransformationUpgradeWrapper, BlockTransformationUpgradeContainer> containerType = new UpgradeContainerType<>(BlockTransformationUpgradeContainer::new);
		ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item);
		UpgradeContainerRegistry.register(itemId, containerType);
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			ChippedCompatClient.registerUpgradeTab(itemId, containerType);
		}
	}

	@Override
	public void setup() {
		//noop
	}
}
