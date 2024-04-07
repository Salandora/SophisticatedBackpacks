package net.p3pp3rf1y.sophisticatedbackpacks.client;

import io.github.fabricators_of_create.porting_lib.models.geometry.IGeometryLoader;
import io.github.fabricators_of_create.porting_lib.models.geometry.RegisterGeometryLoadersCallback;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.fabricmc.fabric.api.event.client.player.ClientPickBlockApplyCallback;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.client.init.ModBlockColors;
import net.p3pp3rf1y.sophisticatedbackpacks.client.init.ModItemColors;
import net.p3pp3rf1y.sophisticatedbackpacks.client.render.BackpackBlockEntityRenderer;
import net.p3pp3rf1y.sophisticatedbackpacks.client.render.BackpackDynamicModel;
import net.p3pp3rf1y.sophisticatedbackpacks.client.render.BackpackItemStackRenderer;
import net.p3pp3rf1y.sophisticatedbackpacks.client.render.BackpackLayerRenderer;
import net.p3pp3rf1y.sophisticatedbackpacks.client.render.BackpackModel;
import net.p3pp3rf1y.sophisticatedbackpacks.client.render.ClientBackpackContentsTooltip;
import net.p3pp3rf1y.sophisticatedbackpacks.network.BlockPickMessage;
import net.p3pp3rf1y.sophisticatedbackpacks.network.SBPPacketHandler;
import net.p3pp3rf1y.sophisticatedcore.event.client.ClientLifecycleEvent;

import java.util.Map;

import static net.p3pp3rf1y.sophisticatedbackpacks.init.ModBlocks.BACKPACK;
import static net.p3pp3rf1y.sophisticatedbackpacks.init.ModBlocks.BACKPACK_TILE_TYPE;
import static net.p3pp3rf1y.sophisticatedbackpacks.init.ModBlocks.DIAMOND_BACKPACK;
import static net.p3pp3rf1y.sophisticatedbackpacks.init.ModBlocks.GOLD_BACKPACK;
import static net.p3pp3rf1y.sophisticatedbackpacks.init.ModBlocks.IRON_BACKPACK;
import static net.p3pp3rf1y.sophisticatedbackpacks.init.ModBlocks.NETHERITE_BACKPACK;
import static net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems.BACKPACKS;
import static net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems.EVERLASTING_BACKPACK_ITEM_ENTITY;

public class ClientEventHandler {
	private static final String BACKPACK_REG_NAME = "backpack";
	public static final ModelLayerLocation BACKPACK_LAYER = new ModelLayerLocation(SophisticatedBackpacks.getRL(BACKPACK_REG_NAME), "main");

	public static void registerHandlers() {
		ClientLifecycleEvent.CLIENT_LEVEL_LOAD.register((client, world) -> ClientBackpackContentsTooltip.onWorldLoad());

		ClientPickBlockApplyCallback.EVENT.register(ClientEventHandler::handleBlockPick);
		RegisterGeometryLoadersCallback.EVENT.register(ClientEventHandler::onModelRegistry);

		registerRenderers();
		registerLayer();

		LivingEntityFeatureRendererRegistrationCallback.EVENT.register(ClientEventHandler::registerBackpackLayer);

		ModBlockColors.register();
		ModItemColors.register();
	}

	private static void onModelRegistry(Map<ResourceLocation, IGeometryLoader<?>> loaders) {
		loaders.put(SophisticatedBackpacks.getRL(BACKPACK_REG_NAME), BackpackDynamicModel.Loader.INSTANCE);
	}

	private static void registerRenderers() {
		EntityRendererRegistry.register(EVERLASTING_BACKPACK_ITEM_ENTITY, ItemEntityRenderer::new);

		BlockEntityRenderers.register(BACKPACK_TILE_TYPE, context -> new BackpackBlockEntityRenderer());
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderType.cutout(), BACKPACK, IRON_BACKPACK, GOLD_BACKPACK, DIAMOND_BACKPACK, NETHERITE_BACKPACK);

		for (var item : BACKPACKS) {
			BuiltinItemRendererRegistry.INSTANCE.register(item, new BackpackItemStackRenderer());
		}
	}

	public static void registerLayer() {
		EntityModelLayerRegistry.registerModelLayer(BACKPACK_LAYER, BackpackModel::createBodyLayer);
	}



	private static void registerBackpackLayer(EntityType<? extends LivingEntity> entityType, LivingEntityRenderer<?, ?> livingEntityRenderer, LivingEntityFeatureRendererRegistrationCallback.RegistrationHelper registrationHelper, EntityRendererProvider.Context context) {
		registrationHelper.register(new BackpackLayerRenderer<>(livingEntityRenderer));
	}

	public static ItemStack handleBlockPick(Player player, HitResult result, ItemStack stack) {
		if (player.isCreative() || result.getType() != HitResult.Type.BLOCK) {
			return stack;
		}
		Level level = player.level();
		BlockPos pos = ((BlockHitResult)result).getBlockPos();
		BlockState state = level.getBlockState(pos);

		if (state.isAir()) {
			return stack;
		}

		ItemStack stackResult = state.getBlock().getCloneItemStack(level, pos, state);
		if (stackResult.isEmpty() || player.getInventory().findSlotMatchingItem(stackResult) > -1) {
			return stack;
		}

		SBPPacketHandler.sendToServer(new BlockPickMessage(stackResult));
		return stackResult;
	}
}
