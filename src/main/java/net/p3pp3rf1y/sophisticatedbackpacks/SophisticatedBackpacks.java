package net.p3pp3rf1y.sophisticatedbackpacks;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.neoforged.fml.config.ModConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.p3pp3rf1y.sophisticatedbackpacks.command.SBPCommand;
import net.p3pp3rf1y.sophisticatedbackpacks.common.CommonEventHandler;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModPackets;
import net.p3pp3rf1y.sophisticatedbackpacks.registry.RegistryLoader;
import fuzs.forgeconfigapiport.api.config.v3.ForgeConfigRegistry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SophisticatedBackpacks implements ModInitializer {
	public static final String MOD_ID = "sophisticatedbackpacks";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private final RegistryLoader registryLoader = new RegistryLoader();
	public final CommonEventHandler commonEventHandler = new CommonEventHandler();

	@Override
	public void onInitialize() {
		ForgeConfigRegistry.INSTANCE.register(MOD_ID, ModConfig.Type.SERVER, Config.SERVER_SPEC);
		ForgeConfigRegistry.INSTANCE.register(MOD_ID, ModConfig.Type.COMMON, Config.COMMON_SPEC);
		Config.SERVER.initListeners();
		commonEventHandler.registerHandlers();
		SBPCommand.init();
		ModPackets.registerPackets();
		ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(registryLoader);
	}

	public static ResourceLocation getRL(String regName) {
		return new ResourceLocation(getRegistryName(regName));
	}

	public static String getRegistryName(String regName) {
		return MOD_ID + ":" + regName;
	}
}
