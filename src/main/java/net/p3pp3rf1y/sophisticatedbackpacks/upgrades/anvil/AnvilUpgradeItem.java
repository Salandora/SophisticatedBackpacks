package net.p3pp3rf1y.sophisticatedbackpacks.upgrades.anvil;

import net.minecraft.world.item.CreativeModeTab;
import net.p3pp3rf1y.sophisticatedbackpacks.Config;
import net.p3pp3rf1y.sophisticatedcore.upgrades.UpgradeItemBase;
import net.p3pp3rf1y.sophisticatedcore.upgrades.UpgradeType;

public class AnvilUpgradeItem extends UpgradeItemBase<AnvilUpgradeWrapper> {
	private static final UpgradeType<AnvilUpgradeWrapper> TYPE = new UpgradeType<>(AnvilUpgradeWrapper::new);

	public AnvilUpgradeItem(CreativeModeTab itemGroup) {
		super(itemGroup, Config.SERVER.maxUpgradesPerStorage);
	}

	@Override
	public UpgradeType<AnvilUpgradeWrapper> getType() {
		return TYPE;
	}
}
