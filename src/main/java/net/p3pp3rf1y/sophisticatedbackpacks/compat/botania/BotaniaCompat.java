package net.p3pp3rf1y.sophisticatedbackpacks.compat.botania;

import vazkii.botania.api.BotaniaAPI;

import net.p3pp3rf1y.sophisticatedcore.compat.ICompat;
import net.p3pp3rf1y.sophisticatedcore.upgrades.magnet.MagnetUpgradeWrapper;

public class BotaniaCompat implements ICompat {
	@Override
	public void setup() {
		MagnetUpgradeWrapper.addMagnetPreventionChecker(entity -> BotaniaAPI.instance().hasSolegnoliaAround(entity));
	}
}
