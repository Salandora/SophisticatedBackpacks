package net.p3pp3rf1y.sophisticatedbackpacks.init;

import net.p3pp3rf1y.sophisticatedbackpacks.compat.CompatModIds;
import net.p3pp3rf1y.sophisticatedbackpacks.compat.chipped.ChippedCompat;
import net.p3pp3rf1y.sophisticatedbackpacks.compat.litematica.LitematicaCompat;
import net.p3pp3rf1y.sophisticatedbackpacks.compat.mkb.ModernKeyBindingCompat;
import net.p3pp3rf1y.sophisticatedbackpacks.compat.trinkets.TrinketsCompat;
import net.p3pp3rf1y.sophisticatedcore.compat.CompatInfo;
import net.p3pp3rf1y.sophisticatedcore.compat.CompatRegistry;

import static net.p3pp3rf1y.sophisticatedcore.compat.CompatModIds.LITEMATICA;

public class ModCompat {
	private ModCompat() {
	}

	public static void register() {
		CompatRegistry.registerCompat(new CompatInfo(CompatModIds.TRINKETS, null), () -> TrinketsCompat::new);
		//CompatRegistry.registerCompat(new CompatInfo(CompatModIds.BOTANIA, null), () -> BotaniaCompat::new);
		CompatRegistry.registerCompat(new CompatInfo(CompatModIds.CHIPPED, null), () -> ChippedCompat::new);
		CompatRegistry.registerCompat(new CompatInfo(LITEMATICA, null), () -> LitematicaCompat::new);
		CompatRegistry.registerCompat(new CompatInfo(CompatModIds.MKB, null), () -> ModernKeyBindingCompat::new);
	}
}
