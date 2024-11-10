package net.p3pp3rf1y.sophisticatedbackpacks.compat.litematica;

import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackStorage;
import net.p3pp3rf1y.sophisticatedcore.compat.litematica.LitematicaHelper;
import net.p3pp3rf1y.sophisticatedcore.util.StreamCodecHelper;

import javax.annotation.Nullable;
import java.util.UUID;

public record LitematicaBackpackContentsPayload(UUID backpackUuid, @Nullable CompoundTag backpackContents) implements CustomPacketPayload {
	public static final Type<LitematicaBackpackContentsPayload> TYPE = new Type<>(SophisticatedBackpacks.getRL("litematica_backpack_contents"));
	public static final StreamCodec<ByteBuf, LitematicaBackpackContentsPayload> STREAM_CODEC = StreamCodec.composite(
			UUIDUtil.STREAM_CODEC,
			LitematicaBackpackContentsPayload::backpackUuid,
			StreamCodecHelper.ofNullable(ByteBufCodecs.COMPOUND_TAG),
			LitematicaBackpackContentsPayload::backpackContents,
			LitematicaBackpackContentsPayload::new);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handlePayload(LitematicaBackpackContentsPayload payload, ClientPlayNetworking.Context context) {
		if (payload.backpackContents == null) {
			return;
		}

		BackpackStorage.get().setBackpackContents(payload.backpackUuid, payload.backpackContents);
		LitematicaHelper.incrementReceived(1);
	}
}
