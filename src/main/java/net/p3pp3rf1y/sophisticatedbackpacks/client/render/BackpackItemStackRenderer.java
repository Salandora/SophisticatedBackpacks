package net.p3pp3rf1y.sophisticatedbackpacks.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.IBackpackWrapper;

public class BackpackItemStackRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
	private final Minecraft minecraft = Minecraft.getInstance();

	@Override
	public void render(ItemStack stack, ItemDisplayContext mode, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
		//ItemRenderer.render does transformations that would need to be transformed against in complicated way so rather pop the pose here and push the new one with the same transforms
		// applied in the correct order with the getModel
		matrixStack.popPose();
		matrixStack.pushPose();
		ItemRenderer itemRenderer = minecraft.getItemRenderer();
		BakedModel model = itemRenderer.getModel(stack, null, minecraft.player, 0);

		boolean leftHand = minecraft.player != null && minecraft.player.getOffhandItem() == stack;
		if (mode != ItemDisplayContext.NONE) {
			model.getTransforms().getTransform(mode).apply(leftHand, matrixStack);
		}
		matrixStack.translate(-0.5D, -0.5D, -0.5D);
		RenderType rendertype = ItemBlockRenderTypes.getRenderType(stack, true);
		VertexConsumer ivertexbuilder = ItemRenderer.getFoilBufferDirect(buffer, rendertype, true, stack.hasFoil());
		itemRenderer.renderModelLists(model, stack, combinedLight, combinedOverlay, matrixStack, ivertexbuilder);
		IBackpackWrapper backpackWrapper = BackpackWrapper.fromStack(stack);
		backpackWrapper.getRenderInfo().getItemDisplayRenderInfo().getDisplayItem().ifPresent(displayItem -> {
			matrixStack.translate(0.5, 0.6, 0.25);
			matrixStack.scale(0.5f, 0.5f, 0.5f);
			matrixStack.mulPose(Axis.ZP.rotationDegrees(displayItem.getRotation()));
			itemRenderer.renderStatic(displayItem.getItem(), ItemDisplayContext.FIXED, combinedLight, combinedOverlay, matrixStack, buffer, minecraft.level, 0);
		});
	}
}
