package com.rourou.heldenmod.commands;

import com.rourou.heldenmod.hearts.HeartManager;
import com.rourou.heldenmod.ModItems;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class HeldenCommands {
    @SubscribeEvent
    public void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("helden")
                        .then(Commands.literal("takeheart")
                                .then(Commands.argument("target", StringArgumentType.word())
                                        .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                                .executes(ctx -> {
                                                    CommandSourceStack src = ctx.getSource();
                                                    String targetName = StringArgumentType.getString(ctx, "target");
                                                    int amount = IntegerArgumentType.getInteger(ctx, "amount");
                                                    ServerPlayer target = src.getServer().getPlayerList().getPlayerByName(targetName);
                                                    if (target == null) {
                                                        src.sendFailure(Component.literal("Spieler nicht gefunden"));
                                                        return 0;
                                                    }
                                                    HeartManager.removeHearts(target, amount);
                                                    // Give removed hearts as items to executor or drop
                                                    src.sendSuccess(Component.literal("Herzen entfernt."), true);
                                                    return 1;
                                                })))))
                        .then(Commands.literal("giveheart")
                                .then(Commands.argument("target", StringArgumentType.word())
                                        .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                                .executes(ctx -> {
                                                    CommandSourceStack src = ctx.getSource();
                                                    String targetName = StringArgumentType.getString(ctx, "target");
                                                    int amount = IntegerArgumentType.getInteger(ctx, "amount");
                                                    ServerPlayer target = src.getServer().getPlayerList().getPlayerByName(targetName);
                                                    if (target == null) {
                                                        src.sendFailure(Component.literal("Spieler nicht gefunden"));
                                                        return 0;
                                                    }
                                                    HeartManager.addHearts(target, amount);
                                                    src.sendSuccess(Component.literal("Herzen gegeben."), true);
                                                    return 1;
                                                }))))
        );
    }
}
