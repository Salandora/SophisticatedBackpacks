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
import net.p3pp3rf1y.sophisticatedbackpacks.common.BackpackWrapperLookup;
import net.p3pp3rf1y.sophisticatedbackpacks.mixin.client.accessor.ItemRendererAccessor;

public class BackpackItemStackRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
	private final Minecraft minecraft = Minecraft.getInstance();

	@Override
	public void render(ItemStack stack, ItemDisplayContext mode, PoseStack poseStack, MultiBufferSource vertexConsumers, int light, int overlay) {
		//ItemRenderer.render does transformations that would need to be transformed against in complicated way so rather pop the pose here and push the new one with the same transforms
		// applied in the correct order with the getModel
		poseStack.popPose();
		poseStack.pushPose();
		ItemRenderer itemRenderer = minecraft.getItemRenderer();
		BakedModel model = itemRenderer.getModel(stack, null, minecraft.player, 0);

		boolean leftHand = minecraft.player != null && minecraft.player.getOffhandItem() == stack;
		if (mode != ItemDisplayContext.NONE) {
			model.getTransforms().getTransform(mode).apply(leftHand, poseStack);
		}
		poseStack.translate(-0.5D, -0.5D, -0.5D);

		RenderType rendertype = ItemBlockRenderTypes.getRenderType(stack, true);
		VertexConsumer ivertexbuilder = ItemRenderer.getFoilBufferDirect(vertexConsumers, rendertype, true, stack.hasFoil());
		((ItemRendererAccessor) itemRenderer).callRenderModelLists(model, stack, light, overlay, poseStack, ivertexbuilder);
		BackpackWrapperLookup.get(stack).flatMap(backpackWrapper -> backpackWrapper.getRenderInfo().getItemDisplayRenderInfo().getDisplayItem()).ifPresent(displayItem -> {
			poseStack.pushPose();
			poseStack.translate(0.5, 0.6, 0.25);
			poseStack.scale(0.5f, 0.5f, 0.5f);
			poseStack.mulPose(Axis.ZP.rotationDegrees(displayItem.getRotation()));
			itemRenderer.renderStatic(displayItem.getItem(), ItemDisplayContext.FIXED, light, overlay, poseStack, vertexConsumers, minecraft.level, 0);
			poseStack.popPose();
		});
	}
}
