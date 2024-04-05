package net.p3pp3rf1y.sophisticatedbackpacks.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.p3pp3rf1y.sophisticatedbackpacks.api.IBlockToolSwapUpgrade;
import net.p3pp3rf1y.sophisticatedbackpacks.common.BackpackWrapperLookup;
import net.p3pp3rf1y.sophisticatedbackpacks.util.PlayerInventoryProvider;
import net.p3pp3rf1y.sophisticatedcore.network.SimplePacketBase;

import java.util.concurrent.atomic.AtomicBoolean;

public class BlockToolSwapMessage extends SimplePacketBase {
	private final BlockPos pos;

	public BlockToolSwapMessage(BlockPos pos) {
		this.pos = pos;
	}

	public BlockToolSwapMessage(FriendlyByteBuf buffer) { this(buffer.readBlockPos()); }

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeBlockPos(this.pos);
	}

	@Override
	public boolean handle(Context context) {
		context.enqueueWork(() -> {
			ServerPlayer sender = context.getSender();
			if (sender == null) {
				return;
			}

			AtomicBoolean result = new AtomicBoolean(false);
			AtomicBoolean anyUpgradeCanInteract = new AtomicBoolean(false);
			PlayerInventoryProvider.get().runOnBackpacks(sender, (backpack, inventoryName, identifier, slot) -> BackpackWrapperLookup.get(backpack)
					.map(backpackWrapper -> {
								backpackWrapper.getUpgradeHandler().getWrappersThatImplement(IBlockToolSwapUpgrade.class)
										.forEach(upgrade -> {
											if (!upgrade.canProcessBlockInteract() || result.get()) {
												return;
											}
											anyUpgradeCanInteract.set(true);

											result.set(upgrade.onBlockInteract(sender.level(), pos, sender.level().getBlockState(pos), sender));
										});
								return result.get();
							}
					).orElse(false)
			);

			if (!anyUpgradeCanInteract.get()) {
				sender.displayClientMessage(Component.translatable("gui.sophisticatedbackpacks.status.no_tool_swap_upgrade_present"), true);
				return;
			}
			if (!result.get()) {
				sender.displayClientMessage(Component.translatable("gui.sophisticatedbackpacks.status.no_tool_found_for_block"), true);
			}
		});
		return true;
	}
}
