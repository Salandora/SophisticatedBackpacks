package net.p3pp3rf1y.sophisticatedbackpacks.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedbackpacks.api.CapabilityBackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.mixin.client.accessor.ItemRendererAccessor;

public class BackpackItemStackRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
	private final Minecraft minecraft = Minecraft.getInstance();

	@Override
	public void render(ItemStack stack, ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
		ItemRenderer itemRenderer = minecraft.getItemRenderer();
		BakedModel model = itemRenderer.getModel(stack, null, minecraft.player, 0);
		RenderType rendertype = ItemBlockRenderTypes.getRenderType(stack, true);
		VertexConsumer ivertexbuilder = ItemRenderer.getFoilBufferDirect(buffer, rendertype, true, stack.hasFoil());
		((ItemRendererAccessor) itemRenderer).callRenderModelLists(model, stack, combinedLight, combinedOverlay, poseStack, ivertexbuilder);
		stack.sophisticatedLibrary_getLazyCapability(CapabilityBackpackWrapper.getCapabilityInstance()).ifPresent(backpackWrapper ->
				backpackWrapper.getRenderInfo().getItemDisplayRenderInfo().getDisplayItem().ifPresent(displayItem -> {
			poseStack.translate(0.5, 0.6, 0.25);
			poseStack.scale(0.5f, 0.5f, 0.5f);
			poseStack.mulPose(Axis.ZP.rotationDegrees(displayItem.getRotation()));
			itemRenderer.renderStatic(displayItem.getItem(), ItemDisplayContext.FIXED, combinedLight, combinedOverlay, poseStack, buffer, minecraft.level, 0);
		}));
	}
}
