package net.p3pp3rf1y.sophisticatedbackpacks.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;

public class SBPCommand {
	private static final int OP_LEVEL = 2;

	private SBPCommand() {}

	public static void init() {
		ArgumentTypeRegistry.registerArgumentType(SophisticatedBackpacks.getRL("backpack_uuid"), BackpackUUIDArgumentType.class, SingletonArgumentInfo.contextFree(BackpackUUIDArgumentType::backpackUuid));
		ArgumentTypeRegistry.registerArgumentType(SophisticatedBackpacks.getRL("player_name"), BackpackPlayerArgumentType.class, SingletonArgumentInfo.contextFree(BackpackPlayerArgumentType::playerName));

		CommandRegistrationCallback.EVENT.register(SBPCommand::registerCommands);
	}

	private static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext, Commands.CommandSelection selection) {
		LiteralCommandNode<CommandSourceStack> mainNode = dispatcher.register(
				Commands.literal("sbp")
						.requires(cs -> cs.hasPermission(OP_LEVEL))
						.then(ListCommand.register())
						.then(GiveCommand.register())
						.then(RemoveNonPlayerCommand.register())
		);
		dispatcher.register(Commands.literal("sophisticatedbackpacks").requires(cs -> cs.hasPermission(OP_LEVEL)).redirect(mainNode));
	}
}
