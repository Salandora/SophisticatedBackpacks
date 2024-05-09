package net.p3pp3rf1y.sophisticatedbackpacks.compat.litematica;

import net.p3pp3rf1y.sophisticatedcore.compat.ICompat;
import net.p3pp3rf1y.sophisticatedcore.network.PacketHandler;

import static net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems.BACKPACKS;
import static net.p3pp3rf1y.sophisticatedcore.compat.litematica.LitematicaCompat.REQUEST_CONTENTS_CAPABILITY;

public class LitematicaCompat implements ICompat {
	@Override
	public void setup() {
		// Register this on the SophisticatedCore channel
		PacketHandler.registerS2CMessage(BackpackContentsMessage.class, BackpackContentsMessage::new);

		REQUEST_CONTENTS_CAPABILITY.registerForItems((stack, context) -> BackpackContentsMessage::create, BACKPACKS);
	}
}
