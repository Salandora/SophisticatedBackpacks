package net.p3pp3rf1y.sophisticatedbackpacks.network;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.api.IBlockToolSwapUpgrade;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.util.PlayerInventoryProvider;

import java.util.concurrent.atomic.AtomicBoolean;

public class BlockToolSwapPacket implements FabricPacket {
	public static final PacketType<BlockToolSwapPacket> TYPE = PacketType.create(new ResourceLocation(SophisticatedBackpacks.MOD_ID, "block_tool_swap"), BlockToolSwapPacket::new);
	private final BlockPos pos;

	public BlockToolSwapPacket(BlockPos pos) {
		this.pos = pos;
	}

	public BlockToolSwapPacket(FriendlyByteBuf buffer) {
		this(BlockPos.of(buffer.readLong()));
	}

	public void handle(ServerPlayer player, PacketSender responseSender) {
		AtomicBoolean result = new AtomicBoolean(false);
		AtomicBoolean anyUpgradeCanInteract = new AtomicBoolean(false);
		PlayerInventoryProvider.get().runOnBackpacks(player, (backpack, inventoryName, identifier, slot) -> {
					BackpackWrapper.fromData(backpack).getUpgradeHandler().getWrappersThatImplement(IBlockToolSwapUpgrade.class)
							.forEach(upgrade -> {
								if (!upgrade.canProcessBlockInteract() || result.get()) {
									return;
								}
								anyUpgradeCanInteract.set(true);

								result.set(upgrade.onBlockInteract(player.level(), pos, player.level().getBlockState(pos), player));
							});
					return result.get();
				}
		);

		if (!anyUpgradeCanInteract.get()) {
			player.displayClientMessage(Component.translatable("gui.sophisticatedbackpacks.status.no_tool_swap_upgrade_present"), true);
			return;
		}
		if (!result.get()) {
			player.displayClientMessage(Component.translatable("gui.sophisticatedbackpacks.status.no_tool_found_for_block"), true);
		}
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeLong(pos.asLong());
	}

	@Override
	public PacketType<?> getType() {
		return TYPE;
	}
}
