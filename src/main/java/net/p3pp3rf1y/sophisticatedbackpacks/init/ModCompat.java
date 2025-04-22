package net.p3pp3rf1y.sophisticatedbackpacks.init;

import net.p3pp3rf1y.sophisticatedbackpacks.compat.CompatModIds;
import net.p3pp3rf1y.sophisticatedbackpacks.compat.chipped.ChippedCompat;
//import net.p3pp3rf1y.sophisticatedbackpacks.compat.mkb.ModernKeyBindingCompat;
import net.p3pp3rf1y.sophisticatedbackpacks.compat.trinkets.TrinketsCompat;
import net.p3pp3rf1y.sophisticatedcore.compat.CompatInfo;
import net.p3pp3rf1y.sophisticatedcore.compat.CompatRegistry;

import static net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks.MOD_ID;

public class ModCompat {
	private ModCompat() {
	}

	public static void register() {
		CompatRegistry registry = CompatRegistry.getRegistry(MOD_ID);
		registry.registerCompat(new CompatInfo(CompatModIds.TRINKETS, null), () -> new TrinketsCompat());
		//registry.registerCompat(new CompatInfo(CompatModIds.BOTANIA, null), () -> new BotaniaCompat());
		registry.registerCompat(new CompatInfo(CompatModIds.CHIPPED, null), () -> new ChippedCompat());
		//registry.registerCompat(new CompatInfo(CompatModIds.MKB, null), () -> new ModernKeyBindingCompat());
	}
}
