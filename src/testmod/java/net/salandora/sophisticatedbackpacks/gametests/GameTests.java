package net.salandora.sophisticatedbackpacks.gametests;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;

public class GameTests {
	@GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
	public void emptyTest(GameTestHelper context) {
		context.succeed();
	}
}
