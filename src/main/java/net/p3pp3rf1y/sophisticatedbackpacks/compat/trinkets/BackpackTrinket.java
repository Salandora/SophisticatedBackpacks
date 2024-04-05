package net.p3pp3rf1y.sophisticatedbackpacks.compat.trinkets;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.Trinket;
import dev.emi.trinkets.api.client.TrinketRenderer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedbackpacks.client.render.BackpackLayerRenderer;
import net.p3pp3rf1y.sophisticatedbackpacks.client.render.BackpackModelManager;
import net.p3pp3rf1y.sophisticatedbackpacks.client.render.IBackpackModel;

public class BackpackTrinket implements Trinket, TrinketRenderer {
    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        // slotIndex needs to be > -1 to indicate that it is equipped
        stack.inventoryTick(entity.level(), entity, slot.index(), true);
    }

    @Override
    public void render(ItemStack stack, SlotReference slotReference, EntityModel<? extends LivingEntity> entityModel, PoseStack poseStack, MultiBufferSource multiBufferSource, int light, LivingEntity livingEntity, float v, float v1, float v2, float v3, float v4, float v5) {
        if (!stack.isEmpty()) {
            poseStack.pushPose();
            IBackpackModel model = BackpackModelManager.getBackpackModel(stack.getItem());
            EquipmentSlot equipmentSlot = model.getRenderEquipmentSlot();
            BackpackLayerRenderer.renderBackpack(entityModel, livingEntity, poseStack, multiBufferSource, light, stack, !livingEntity.getItemBySlot(equipmentSlot).isEmpty(), BackpackModelManager.getBackpackModel(stack.getItem()));
            poseStack.popPose();
        }
    }
}
