package net.p3pp3rf1y.sophisticatedbackpacks.compat.litematica.mixin;

import fi.dy.masa.litematica.materials.MaterialCache;
import fi.dy.masa.litematica.materials.MaterialListEntry;
import fi.dy.masa.litematica.materials.MaterialListUtils;
import fi.dy.masa.malilib.util.ItemType;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.p3pp3rf1y.sophisticatedbackpacks.common.BackpackWrapperLookup;
import net.p3pp3rf1y.sophisticatedbackpacks.util.PlayerInventoryProvider;

import java.util.List;

@Mixin(MaterialListUtils.class)
public class MaterialListUtilsMixin {
	@Inject(method = "getMaterialList", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/Object2IntOpenHashMap;keySet()Lit/unimi/dsi/fastutil/objects/ObjectSet;", remap = false), locals = LocalCapture.CAPTURE_FAILHARD)
	private static void sophisticatedBackpacks$injectTrinketBackpacks(Object2IntOpenHashMap<BlockState> countsTotal, Object2IntOpenHashMap<BlockState> countsMissing, Object2IntOpenHashMap<BlockState> countsMismatch, Player player, CallbackInfoReturnable<List<MaterialListEntry>> cir,
			List<MaterialListEntry> list, MaterialCache cache, Object2IntOpenHashMap<ItemType> itemTypesTotal, Object2IntOpenHashMap<ItemType> itemTypesMissing, Object2IntOpenHashMap<ItemType> itemTypesMismatch, Object2IntOpenHashMap<ItemType> playerInvItems) {
		PlayerInventoryProvider.get().runOnBackpacks(player, (backpack, inventoryHandlerName, identifier, slot) -> {
			sophisticatedBackpacks$processBackpack(playerInvItems, backpack);
			return false; // Return false here, we want to run over all backpacks
		});
	}

	@Inject(method = "updateAvailableCounts", at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;"), locals = LocalCapture.CAPTURE_FAILHARD)
	private static void sophisticatedBackpacks$injectTrinketBackpacks(List<MaterialListEntry> list, Player player, CallbackInfo ci, Object2IntOpenHashMap<ItemType> playerInvItems) {
		PlayerInventoryProvider.get().runOnBackpacks(player, (backpack, inventoryHandlerName, identifier, slot) -> {
			sophisticatedBackpacks$processBackpack(playerInvItems, backpack);
			return false; // Return false here, we want to run over all backpacks
		});
	}

	@Unique
	private static void sophisticatedBackpacks$processBackpack(Object2IntOpenHashMap<ItemType> map, ItemStack backpack) {
		BackpackWrapperLookup.get(backpack).ifPresent(wrapper -> {
			for (StorageView<ItemVariant> view : wrapper.getInventoryHandler().nonEmptyViews()) {
				map.addTo(new ItemType(view.getResource().toStack((int) view.getAmount()), true, false), (int)view.getAmount());
			}
		});
	}
}
