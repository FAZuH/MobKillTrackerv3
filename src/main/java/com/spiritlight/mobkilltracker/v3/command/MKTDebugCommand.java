package com.spiritlight.mobkilltracker.v3.command;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.spiritlight.mobkilltracker.v3.Main;
import com.spiritlight.mobkilltracker.v3.core.DataHandler;
import com.spiritlight.mobkilltracker.v3.core.EntityEventHandler;
import com.spiritlight.mobkilltracker.v3.events.TerminationEvent;
import com.spiritlight.mobkilltracker.v3.utils.ItemDatabase;
import com.spiritlight.mobkilltracker.v3.utils.minecraft.Message;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;

public class MKTDebugCommand {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
                literal("mktd")
                        .executes(
                                context -> {
                                    Message.error(
                                            "Invalid syntax. /mktd stop|start <duration>|refetch|delay <duration>|log|logvalid|dump|tracklast|exportall|dumpviewed");
                                    return 1;
                                })
                        .then(
                                literal("stop")
                                        .executes(
                                                context -> {
                                                    if (DataHandler.getLastHandler()
                                                            instanceof
                                                            DataHandler.ListenerHandler) {
                                                        ((DataHandler.ListenerHandler)
                                                                        DataHandler
                                                                                .getLastHandler())
                                                                .onTermination(
                                                                        new TerminationEvent(
                                                                                null,
                                                                                TerminationEvent
                                                                                        .Type
                                                                                        .COMPLETE));
                                                    }
                                                    Message.info("OK");
                                                    return 1;
                                                }))
                        .then(
                                literal("start")
                                        .executes(
                                                context -> {
                                                    DataHandler.newListenedHandler().start(30);
                                                    Message.info("OK (Not Logged)");
                                                    return 1;
                                                })
                                        .then(
                                                argument("duration", IntegerArgumentType.integer())
                                                        .executes(
                                                                context -> {
                                                                    int length =
                                                                            IntegerArgumentType
                                                                                    .getInteger(
                                                                                            context,
                                                                                            "duration");
                                                                    DataHandler.newListenedHandler()
                                                                            .start(length);
                                                                    Message.info("OK (Not Logged)");
                                                                    return 1;
                                                                })))
                        .then(
                                literal("refetch")
                                        .executes(
                                                context -> {
                                                    Message.info("Processing...");
                                                    ItemDatabase.instance.fetchItem();
                                                    Message.info("Fetched.");
                                                    return 1;
                                                }))
                        .then(
                                literal("delay")
                                        .executes(
                                                context -> {
                                                    Message.warn(
                                                            "Invalid usage: /mktd delay <duration/ms>");
                                                    Message.warn("Default: 100(ms)");
                                                    Message.info(
                                                            "Description: Delay before an item is added to scan queue.");
                                                    return 1;
                                                })
                                        .then(
                                                argument("duration", IntegerArgumentType.integer())
                                                        .executes(
                                                                context -> {
                                                                    int duration =
                                                                            IntegerArgumentType
                                                                                    .getInteger(
                                                                                            context,
                                                                                            "duration");
                                                                    Main.configuration
                                                                            .setDelayMills(
                                                                                    duration);
                                                                    Message.info(
                                                                            "OK=" + duration
                                                                                    + "ms");
                                                                    return 1;
                                                                })))
                        .then(
                                literal("log")
                                        .executes(
                                                context -> {
                                                    Main.configuration.setLogging(
                                                            !Main.configuration.isLogging());
                                                    Message.info(
                                                            String.valueOf(
                                                                    Main.configuration
                                                                            .isLogging()));
                                                    return 1;
                                                }))
                        .then(
                                literal("logvalid")
                                        .executes(
                                                context -> {
                                                    Main.configuration.setLogValid(
                                                            !Main.configuration.doLogValid());
                                                    Message.info(
                                                            String.valueOf(
                                                                    Main.configuration
                                                                            .doLogValid()));
                                                    return 1;
                                                }))
                        .then(
                                literal("logv")
                                        .executes(
                                                context -> {
                                                    Main.configuration.setLogValid(
                                                            !Main.configuration.doLogValid());
                                                    Message.info(
                                                            String.valueOf(
                                                                    Main.configuration
                                                                            .doLogValid()));
                                                    return 1;
                                                }))
                        .then(
                                literal("tracklast")
                                        .executes(
                                                context -> {
                                                    Main.configuration.setTrackLast(
                                                            !Main.configuration.doTrackLast());
                                                    Message.info(
                                                            String.valueOf(
                                                                    Main.configuration
                                                                            .doTrackLast()));
                                                    DataHandler.invalidateLast();
                                                    return 1;
                                                }))
                        .then(
                                literal("exportall")
                                        .executes(
                                                context -> {
                                                    Main.export();
                                                    Message.info("OK");
                                                    return 1;
                                                }))
                        .then(
                                literal("dumpviewed")
                                        .executes(
                                                context -> {
                                                    Message.send(
                                                            EntityEventHandler.getViewedEntities()
                                                                    .toString());
                                                    Message.info("OK");
                                                    return 1;
                                                }))
                        .then(
                                literal("save")
                                        .executes(
                                                context -> {
                                                    Main.save();
                                                    Message.info("OK");
                                                    return 1;
                                                }))
                        .then(
                                literal("load")
                                        .executes(
                                                context -> {
                                                    try {
                                                        Main.configuration.load();
                                                    } catch (Exception e) {
                                                        Message.fatal(
                                                                "Cannot load config: "
                                                                        + e.getMessage());
                                                        e.printStackTrace();
                                                    }
                                                    Message.info("OK");
                                                    return 1;
                                                }))
                        .then(
                                literal("dump")
                                        .executes(
                                                context -> {
                                                    if (MinecraftClient.getInstance().world == null)
                                                        return 0;
                                                    Iterable<Entity> loadedEntities =
                                                            MinecraftClient.getInstance()
                                                                    .world
                                                                    .getEntities();
                                                    for (Entity e : loadedEntities) {
                                                        try {
                                                            Text itc =
                                                                    Message.builder(
                                                                                    "Entity "
                                                                                            + e.getName()
                                                                                                    .getString()
                                                                                            + ":")
                                                                            .addHoverEvent(
                                                                                    HoverEvent
                                                                                            .Action
                                                                                            .SHOW_TEXT,
                                                                                    Text.literal(
                                                                                            Message
                                                                                                    .formatJson(
                                                                                                            "Wynncraft Item Name:"
                                                                                                                    + (e
                                                                                                                                    .hasCustomName()
                                                                                                                            ? e.getCustomName()
                                                                                                                                            .getString()
                                                                                                                                    + "("
                                                                                                                                    + e.getName()
                                                                                                                                            .getString()
                                                                                                                                    + ")"
                                                                                                                            : e.getName()
                                                                                                                                    .getString())
                                                                                                                    + "\n"
                                                                                                                    + "Item UUID: "
                                                                                                                    + e
                                                                                                                            .getUuid()
                                                                                                                    + "\n\n"
                                                                                                                    + "Type: "
                                                                                                                    + e
                                                                                                                            .getType()
                                                                                                                    + "\nPos: "
                                                                                                                    + e
                                                                                                                            .getPos()
                                                                                                                    + "\n\nClick to track!")))
                                                                            .addClickEvent(
                                                                                    ClickEvent
                                                                                            .Action
                                                                                            .RUN_COMMAND,
                                                                                    "/compass "
                                                                                            + e.getBlockPos()
                                                                                                    .getX()
                                                                                            + " "
                                                                                            + e.getBlockPos()
                                                                                                    .getY()
                                                                                            + " "
                                                                                            + e.getBlockPos()
                                                                                                    .getZ())
                                                                            .build();
                                                            Message.sendRaw(itc);
                                                            System.out.println(
                                                                    e.getName().getString()
                                                                            + "#Type="
                                                                            + e.getType()
                                                                            + "#Pos="
                                                                            + e.getPos());
                                                        } catch (Exception ex) {
                                                            Message.error(
                                                                    "Error whilst dumping entity: "
                                                                            + ex.getMessage());
                                                            ex.printStackTrace();
                                                        }
                                                    }
                                                    return 1;
                                                })));
    }
}
