package net.p3pp3rf1y.sophisticatedbackpacks.compat.chipped;

import earth.terrarium.chipped.common.compat.jei.ChippedRecipeCategory;
import earth.terrarium.chipped.common.registry.ModBlocks;
import earth.terrarium.chipped.common.registry.ModRecipeTypes;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.SBPButtonDefinitions;
import net.p3pp3rf1y.sophisticatedbackpacks.compat.emi.EmiCompat;
import net.p3pp3rf1y.sophisticatedbackpacks.compat.jei.SBJeiPlugin;
import net.p3pp3rf1y.sophisticatedbackpacks.compat.rei.REIClientCompat;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems;
import net.p3pp3rf1y.sophisticatedcore.client.gui.StorageScreenBase;
import net.p3pp3rf1y.sophisticatedcore.client.gui.UpgradeGuiManager;
import net.p3pp3rf1y.sophisticatedcore.client.gui.utils.Position;
import net.p3pp3rf1y.sophisticatedcore.common.gui.UpgradeContainerRegistry;
import net.p3pp3rf1y.sophisticatedcore.common.gui.UpgradeContainerType;
import net.p3pp3rf1y.sophisticatedcore.compat.CompatModIds;
import net.p3pp3rf1y.sophisticatedcore.compat.ICompat;
import net.p3pp3rf1y.sophisticatedcore.compat.chipped.BlockTransformationUpgradeContainer;
import net.p3pp3rf1y.sophisticatedcore.compat.chipped.BlockTransformationUpgradeItem;
import net.p3pp3rf1y.sophisticatedcore.compat.chipped.BlockTransformationUpgradeTab;
import net.p3pp3rf1y.sophisticatedcore.compat.chipped.BlockTransformationUpgradeWrapper;

public class ChippedCompat implements ICompat {
	public static final BlockTransformationUpgradeItem BOTANIST_WORKBENCH_UPGRADE = ModItems.register("chipped/botanist_workbench_upgrade",
			() -> new BlockTransformationUpgradeItem(ModItems.CREATIVE_TAB, ModRecipeTypes.BOTANIST_WORKBENCH_TYPE));
	public static final BlockTransformationUpgradeItem GLASSBLOWER_WORKBENCH_UPGRADE = ModItems.register("chipped/glassblower_workbench_upgrade",
			() -> new BlockTransformationUpgradeItem(ModItems.CREATIVE_TAB, ModRecipeTypes.GLASSBLOWER_TYPE));
	public static final BlockTransformationUpgradeItem CARPENTER_WORKBENCH_UPGRADE = ModItems.register("chipped/carpenter_workbench_upgrade",
			() -> new BlockTransformationUpgradeItem(ModItems.CREATIVE_TAB, ModRecipeTypes.CARPENTERS_TABLE_TYPE));
	public static final BlockTransformationUpgradeItem SHEPHERD_WORKBENCH_UPGRADE = ModItems.register("chipped/shepherd_workbench_upgrade",
			() -> new BlockTransformationUpgradeItem(ModItems.CREATIVE_TAB, ModRecipeTypes.LOOM_TABLE_TYPE));
	public static final BlockTransformationUpgradeItem MASON_WORKBENCH_UPGRADE = ModItems.register("chipped/mason_workbench_upgrade",
			() -> new BlockTransformationUpgradeItem(ModItems.CREATIVE_TAB, ModRecipeTypes.MASON_TABLE_TYPE));
	public static final BlockTransformationUpgradeItem PHILOSOPHER_WORKBENCH_UPGRADE = ModItems.register("chipped/philosopher_workbench_upgrade",
			() -> new BlockTransformationUpgradeItem(ModItems.CREATIVE_TAB, ModRecipeTypes.ALCHEMY_BENCH_TYPE));
	public static final BlockTransformationUpgradeItem TINKERER_WORKBENCH_UPGRADE = ModItems.register("chipped/tinkerer_workbench_upgrade",
			() -> new BlockTransformationUpgradeItem(ModItems.CREATIVE_TAB, ModRecipeTypes.TINKERING_TABLE_TYPE));

	@Override
	public void init() {
		registerContainers();

		if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT) {
			return;
		}

		if (FabricLoader.getInstance().isModLoaded(CompatModIds.JEI)) {
			SBJeiPlugin.setAdditionalCatalystRegistrar(registration -> {
				registration.addRecipeCatalyst(new ItemStack(BOTANIST_WORKBENCH_UPGRADE), ChippedRecipeCategory.BOTANIST_WORKBENCH_RECIPE);
				registration.addRecipeCatalyst(new ItemStack(GLASSBLOWER_WORKBENCH_UPGRADE), ChippedRecipeCategory.GLASSBLOWER_RECIPE);
				registration.addRecipeCatalyst(new ItemStack(CARPENTER_WORKBENCH_UPGRADE), ChippedRecipeCategory.CARPENTERS_TABLE_RECIPE);
				registration.addRecipeCatalyst(new ItemStack(SHEPHERD_WORKBENCH_UPGRADE), ChippedRecipeCategory.LOOM_TABLE_RECIPE);
				registration.addRecipeCatalyst(new ItemStack(MASON_WORKBENCH_UPGRADE), ChippedRecipeCategory.MASON_TABLE_RECIPE);
				registration.addRecipeCatalyst(new ItemStack(PHILOSOPHER_WORKBENCH_UPGRADE), ChippedRecipeCategory.ALCHEMY_BENCH_RECIPE);
				registration.addRecipeCatalyst(new ItemStack(TINKERER_WORKBENCH_UPGRADE), ChippedRecipeCategory.TINKERING_TABLE_RECIPE);
			});
		}

		if (FabricLoader.getInstance().isModLoaded(CompatModIds.REI)) {
			REIClientCompat.setAdditionalCategories(registration -> {
				registration.addWorkstations(CategoryIdentifier.of(ModBlocks.BOTANIST_WORKBENCH.getId()), EntryStacks.of(BOTANIST_WORKBENCH_UPGRADE));
				registration.addWorkstations(CategoryIdentifier.of(ModBlocks.GLASSBLOWER.getId()), EntryStacks.of(GLASSBLOWER_WORKBENCH_UPGRADE));
				registration.addWorkstations(CategoryIdentifier.of(ModBlocks.CARPENTERS_TABLE.getId()), EntryStacks.of(CARPENTER_WORKBENCH_UPGRADE));
				registration.addWorkstations(CategoryIdentifier.of(ModBlocks.LOOM_TABLE.getId()), EntryStacks.of(SHEPHERD_WORKBENCH_UPGRADE));
				registration.addWorkstations(CategoryIdentifier.of(ModBlocks.MASON_TABLE.getId()), EntryStacks.of(MASON_WORKBENCH_UPGRADE));
				registration.addWorkstations(CategoryIdentifier.of(ModBlocks.ALCHEMY_BENCH.getId()), EntryStacks.of(PHILOSOPHER_WORKBENCH_UPGRADE));
				registration.addWorkstations(CategoryIdentifier.of(ModBlocks.TINKERING_TABLE.getId()), EntryStacks.of(TINKERER_WORKBENCH_UPGRADE));
			});
		}

		if (FabricLoader.getInstance().isModLoaded(CompatModIds.EMI)) {
			EmiCompat.WORKSTATIONS.register((consumer -> {
				consumer.accept(new EmiCompat.WorkstationEntry(new ResourceLocation("botanist_workbench"), ModBlocks.BOTANIST_WORKBENCH.get(), BOTANIST_WORKBENCH_UPGRADE));
				consumer.accept(new EmiCompat.WorkstationEntry(new ResourceLocation("glassblower"), ModBlocks.GLASSBLOWER.get(), GLASSBLOWER_WORKBENCH_UPGRADE));
				consumer.accept(new EmiCompat.WorkstationEntry(new ResourceLocation("carpenters_table"), ModBlocks.CARPENTERS_TABLE.get(), CARPENTER_WORKBENCH_UPGRADE));
				consumer.accept(new EmiCompat.WorkstationEntry(new ResourceLocation("loom_table"), ModBlocks.LOOM_TABLE.get(), SHEPHERD_WORKBENCH_UPGRADE));
				consumer.accept(new EmiCompat.WorkstationEntry(new ResourceLocation("mason_table"), ModBlocks.MASON_TABLE.get(), MASON_WORKBENCH_UPGRADE));
				consumer.accept(new EmiCompat.WorkstationEntry(new ResourceLocation("alchemy_bench"), ModBlocks.ALCHEMY_BENCH.get(), PHILOSOPHER_WORKBENCH_UPGRADE));
				consumer.accept(new EmiCompat.WorkstationEntry(new ResourceLocation("tinkering_table"), ModBlocks.TINKERING_TABLE.get(), TINKERER_WORKBENCH_UPGRADE));
			}));
		}
	}

	public void registerContainers() {
		registerUpgradeContainer(BOTANIST_WORKBENCH_UPGRADE);
		registerUpgradeContainer(GLASSBLOWER_WORKBENCH_UPGRADE);
		registerUpgradeContainer(CARPENTER_WORKBENCH_UPGRADE);
		registerUpgradeContainer(SHEPHERD_WORKBENCH_UPGRADE);
		registerUpgradeContainer(MASON_WORKBENCH_UPGRADE);
		registerUpgradeContainer(PHILOSOPHER_WORKBENCH_UPGRADE);
		registerUpgradeContainer(TINKERER_WORKBENCH_UPGRADE);
	}

	private void registerUpgradeContainer(BlockTransformationUpgradeItem item) {
		UpgradeContainerType<BlockTransformationUpgradeWrapper, BlockTransformationUpgradeContainer> containerType = new UpgradeContainerType<>(BlockTransformationUpgradeContainer::new);
		ResourceLocation itemId = Registry.ITEM.getKey(item);
		UpgradeContainerRegistry.register(itemId, containerType);
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			UpgradeGuiManager.registerTab(containerType, (BlockTransformationUpgradeContainer upgradeContainer, Position position, StorageScreenBase<?> screen) -> {
				String itemName = itemId.getPath();
				return new BlockTransformationUpgradeTab(upgradeContainer, position, screen, SBPButtonDefinitions.SHIFT_CLICK_TARGET, itemName.replace('/', '_').substring(0, itemName.length() - "_upgrade".length()));
			});
		}
	}

	@Override
	public void setup() {
		//noop
	}
}
