package net.salandora.sophisticatedbackpacks.unittests;

import net.fabricmc.api.ModInitializer;

import org.slf4j.LoggerFactory;

public class UnitTestsInitializer implements ModInitializer {
	@Override
	public void onInitialize() {


		LoggerFactory.getLogger("sophisticatedbackpacks testmod").info("SophisticatedBackpacks unit tests successful.");
	}
}
