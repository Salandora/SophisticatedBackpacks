package net.p3pp3rf1y.sophisticatedbackpacks.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackStorage;
import net.p3pp3rf1y.sophisticatedcore.client.render.ClientStorageContentsTooltip;
import net.p3pp3rf1y.sophisticatedcore.network.SimplePacketBase;

import java.util.UUID;
import javax.annotation.Nullable;

public class BackpackContentsMessage extends SimplePacketBase {
	private final UUID backpackUuid;
	@Nullable
	private final CompoundTag backpackContents;

	public BackpackContentsMessage(UUID backpackUuid, @Nullable CompoundTag backpackContents) {
		this.backpackUuid = backpackUuid;
		this.backpackContents = backpackContents;
	}
	public BackpackContentsMessage(FriendlyByteBuf buffer) { this(buffer.readUUID(), buffer.readNbt()); }

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeUUID(this.backpackUuid);
		buffer.writeNbt(this.backpackContents);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public boolean handle(Context context) {
		context.enqueueWork(() -> {
			Player player = context.getClientPlayer();
			if (player == null || backpackContents == null) {
				return;
			}

			BackpackStorage.get().setBackpackContents(backpackUuid, backpackContents);
			ClientStorageContentsTooltip.refreshContents();
		});
		return true;
	}

}
