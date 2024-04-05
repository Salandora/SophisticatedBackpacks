package net.p3pp3rf1y.sophisticatedbackpacks.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.p3pp3rf1y.sophisticatedbackpacks.api.IEntityToolSwapUpgrade;
import net.p3pp3rf1y.sophisticatedbackpacks.common.BackpackWrapperLookup;
import net.p3pp3rf1y.sophisticatedbackpacks.util.PlayerInventoryProvider;
import net.p3pp3rf1y.sophisticatedcore.network.SimplePacketBase;

import java.util.concurrent.atomic.AtomicBoolean;

public class EntityToolSwapMessage extends SimplePacketBase {
	private final int entityId;

	public EntityToolSwapMessage(int entityId) {
		this.entityId = entityId;
	}

	public EntityToolSwapMessage(FriendlyByteBuf buffer) { this(buffer.readInt()); }

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeInt(this.entityId);
	}

	@Override
	public boolean handle(Context context) {
		context.enqueueWork(() -> {
			ServerPlayer sender = context.getSender();
			if (sender == null) {
				return;
			}

			Level level = sender.level();
			Entity entity = level.getEntity(entityId);

			if (entity == null) {
				return;
			}

			AtomicBoolean result = new AtomicBoolean(false);
			AtomicBoolean anyUpgradeCanInteract = new AtomicBoolean(false);
			PlayerInventoryProvider.get().runOnBackpacks(sender, (backpack, inventoryName, identifier, slot) -> BackpackWrapperLookup.get(backpack)
					.map(backpackWrapper -> {
								backpackWrapper.getUpgradeHandler().getWrappersThatImplement(IEntityToolSwapUpgrade.class)
										.forEach(upgrade -> {
											if (!upgrade.canProcessEntityInteract() || result.get()) {
												return;
											}
											anyUpgradeCanInteract.set(true);

											result.set(upgrade.onEntityInteract(level, entity, sender));
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
				sender.displayClientMessage(Component.translatable("gui.sophisticatedbackpacks.status.no_tool_found_for_entity"), true);
			}
		});
		return true;
	}
}
