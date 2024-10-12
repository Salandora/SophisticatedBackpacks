package net.p3pp3rf1y.sophisticatedbackpacks.network;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.util.InventoryInteractionHelper;
import net.p3pp3rf1y.sophisticatedbackpacks.util.PlayerInventoryProvider;

public class InventoryInteractionPacket implements FabricPacket {
	public static final PacketType<InventoryInteractionPacket> TYPE = PacketType.create(new ResourceLocation(SophisticatedBackpacks.MOD_ID, "inventory_interaction"), InventoryInteractionPacket::new);
	private final BlockPos pos;
	private final Direction face;

	public InventoryInteractionPacket(BlockPos pos, Direction face) {
		this.pos = pos;
		this.face = face;
	}

	public InventoryInteractionPacket(FriendlyByteBuf buffer) {
		this(buffer.readBlockPos(), buffer.readEnum(Direction.class));
	}

	public void handle(ServerPlayer player, PacketSender responseSender) {
		PlayerInventoryProvider.get().runOnBackpacks(player, (backpack, inventoryName, identifier, slot) -> {
			InventoryInteractionHelper.tryInventoryInteraction(pos, player.level(), backpack, face, player);
			player.swing(InteractionHand.MAIN_HAND, true);
			return true;
		});
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeBlockPos(pos);
		buffer.writeEnum(face);
	}

	@Override
	public PacketType<?> getType() {
		return TYPE;
	}
}
