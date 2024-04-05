package net.p3pp3rf1y.sophisticatedbackpacks.init;

import net.fabricmc.loader.api.FabricLoader;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.compat.CompatModIds;
import net.p3pp3rf1y.sophisticatedbackpacks.compat.chipped.ChippedCompat;
import net.p3pp3rf1y.sophisticatedbackpacks.compat.trinkets.TrinketsCompat;
import net.p3pp3rf1y.sophisticatedcore.compat.ICompat;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

public class ModCompat {
	private ModCompat() {}

	private static final Map<String, Supplier<Callable<ICompat>>> compatFactories = new HashMap<>();
	private static final Map<String, ICompat> loadedCompats = new HashMap<>();

	static {
		compatFactories.put(CompatModIds.TRINKETS, () -> TrinketsCompat::new);
		//compatFactories.put(CompatModIds.BOTANIA, () -> BotaniaCompat::new); // TODO readd Botania compat
		compatFactories.put(CompatModIds.CHIPPED, () -> ChippedCompat::new);
	}

	public static void compatsSetup() {
		loadedCompats.values().forEach(ICompat::setup);
	}

	public static void initCompats() {
		for (Map.Entry<String, Supplier<Callable<ICompat>>> entry : compatFactories.entrySet()) {
			if (FabricLoader.getInstance().isModLoaded(entry.getKey())) {
				try {
					loadedCompats.put(entry.getKey(), entry.getValue().get().call());
				}
				catch (Exception e) {
					SophisticatedBackpacks.LOGGER.error("Error instantiating compatibility ", e);
				}
			}
		}

		loadedCompats.values().forEach(ICompat::init);
	}
}
