package net.p3pp3rf1y.sophisticatedbackpacks;

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModCompat;

public class SophisticatedBackpacksPreLaunch implements PreLaunchEntrypoint {
	@Override
	public void onPreLaunch() {
		ModCompat.register();
	}
}
