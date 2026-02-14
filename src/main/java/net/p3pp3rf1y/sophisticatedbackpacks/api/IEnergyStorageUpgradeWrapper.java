package net.p3pp3rf1y.sophisticatedbackpacks.api;

import com.github.salandora.sophisticatedfabriclib.energy.api.v1.IEnergyStorage;

import javax.annotation.Nullable;

public interface IEnergyStorageUpgradeWrapper {
	@Nullable
	IEnergyStorage wrapStorage(@Nullable IEnergyStorage energyStorage);
}
