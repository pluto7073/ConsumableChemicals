package ml.pluto7073.chemicals.commands;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import ml.pluto7073.chemicals.Chemicals;
import ml.pluto7073.chemicals.handlers.ConsumableChemicalHandler;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class ChemicalCommands {

	public static void register() {
		CommandRegistrationCallback.EVENT.register((dispatcher, context, selection) -> {
			LiteralArgumentBuilder<CommandSourceStack> chemicals = literal("chemicals");
			for (ConsumableChemicalHandler handler : Chemicals.REGISTRY) {
				if (handler.getId().getPath().equals("empty")) continue;
				LiteralArgumentBuilder<CommandSourceStack> custom = handler.createCustomChemicalCommandExtension();
				chemicals.then(custom != null ? custom : createChemicalCommand(handler));
			}
			dispatcher.register(chemicals);
		});
	}

	private static LiteralArgumentBuilder<CommandSourceStack> createChemicalCommand(ConsumableChemicalHandler handler) {
		return literal(handler.getId().toString())
				.then(createGetCommand(handler))
				.then(createSetCommand(handler))
				.then(createChangeCommand(handler));
	}

	private static LiteralArgumentBuilder<CommandSourceStack> createGetCommand(ConsumableChemicalHandler handler) {
		return literal("get")
				.then(argument("target", EntityArgument.player())
					.executes(ctx -> {
						ServerPlayer target = EntityArgument.getPlayer(ctx, "target");
						String amount = handler.formatAmount(handler.get(target));
						ctx.getSource().sendSuccess(() ->
								Component.translatable("command.chemical.get.response", target.getName(), amount,
										Component.translatable(handler.getLanguageKey())), true);
						return 1;
					}));
	}

	private static LiteralArgumentBuilder<CommandSourceStack> createSetCommand(ConsumableChemicalHandler handler) {
		return literal("set")
				.then(argument("target", EntityArgument.player())
				.then(argument("amount", FloatArgumentType.floatArg(0))
						.executes(ctx -> {
							ServerPlayer target = EntityArgument.getPlayer(ctx, "target");
							float amount = FloatArgumentType.getFloat(ctx, "amount");
							handler.set(target, amount);
							ctx.getSource().sendSuccess(() ->
									Component.translatable("command.chemical.set.response",
											Component.translatable(handler.getLanguageKey()), amount, target.getName()), true);
							return 1;
						})));
	}

	private static LiteralArgumentBuilder<CommandSourceStack> createChangeCommand(ConsumableChemicalHandler handler) {
		return literal("change")
				.then(argument("target", EntityArgument.player())
				.then(argument("amount", FloatArgumentType.floatArg())
						.executes(ctx -> {
							ServerPlayer target = EntityArgument.getPlayer(ctx, "target");
							float amount = FloatArgumentType.getFloat(ctx, "amount");
							float after = handler.add(target, amount);
							ctx.getSource().sendSuccess(() ->
									Component.translatable("command.chemical.change.response",
											handler.formatAmount(amount), handler.getLanguageKey(),
											target.getName(), handler.formatAmount(after)), true);
							return 1;
						})));
	}

}
