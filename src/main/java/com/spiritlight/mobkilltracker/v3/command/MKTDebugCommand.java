package com.spiritlight.mobkilltracker.v3.command;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
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
        dispatcher.register(literal("mktd")
                .executes(ctx -> {
                    Message.error(
                            "Invalid syntax. /mktd stop|start <duration>|refetch|delay <duration>|log|logvalid|dump|tracklast|exportall|dumpviewed");
                    return 1;
                })
                .then(literal("stop").executes(MKTDebugCommand::stop))
                .then(literal("start")
                        .executes(ctx -> start(ctx, 30))
                        .then(argument("duration", IntegerArgumentType.integer())
                                .executes(ctx -> start(ctx, IntegerArgumentType.getInteger(ctx, "duration")))))
                .then(literal("refetch").executes(MKTDebugCommand::refetch))
                .then(literal("delay")
                        .executes(MKTDebugCommand::delayHelp)
                        .then(argument("duration", IntegerArgumentType.integer())
                                .executes(MKTDebugCommand::setDelay)))
                .then(literal("log").executes(MKTDebugCommand::toggleLog))
                .then(literal("logvalid").executes(MKTDebugCommand::toggleLogValid))
                .then(literal("logv").executes(MKTDebugCommand::toggleLogValid))
                .then(literal("tracklast").executes(MKTDebugCommand::toggleTrackLast))
                .then(literal("exportall").executes(MKTDebugCommand::exportAll))
                .then(literal("dumpviewed").executes(MKTDebugCommand::dumpViewed))
                .then(literal("save").executes(MKTDebugCommand::save))
                .then(literal("load").executes(MKTDebugCommand::load))
                .then(literal("dump").executes(MKTDebugCommand::dump)));
    }

    private static int stop(CommandContext<FabricClientCommandSource> ctx) {
        if (DataHandler.getLastHandler() instanceof DataHandler.ListenerHandler handler) {
            handler.onTermination(new TerminationEvent(null, TerminationEvent.Type.COMPLETE));
        }
        Message.info("OK");
        return 1;
    }

    private static int start(CommandContext<FabricClientCommandSource> ctx, int duration) {
        DataHandler.newListenedHandler().start(duration);
        Message.info("OK (Not Logged)");
        return 1;
    }

    private static int refetch(CommandContext<FabricClientCommandSource> ctx) {
        Message.info("Processing...");
        ItemDatabase.instance.fetchItem();
        Message.info("Fetched.");
        return 1;
    }

    private static int delayHelp(CommandContext<FabricClientCommandSource> ctx) {
        Message.warn("Invalid usage: /mktd delay <duration/ms>");
        Message.warn("Default: 100(ms)");
        Message.info("Description: Delay before an item is added to scan queue.");
        return 1;
    }

    private static int setDelay(CommandContext<FabricClientCommandSource> ctx) {
        int duration = IntegerArgumentType.getInteger(ctx, "duration");
        Main.configuration.setDelayMills(duration);
        Message.info("OK=" + duration + "ms");
        return 1;
    }

    private static int toggleLog(CommandContext<FabricClientCommandSource> ctx) {
        Main.configuration.setLogging(!Main.configuration.isLogging());
        Message.info(String.valueOf(Main.configuration.isLogging()));
        return 1;
    }

    private static int toggleLogValid(CommandContext<FabricClientCommandSource> ctx) {
        Main.configuration.setLogValid(!Main.configuration.doLogValid());
        Message.info(String.valueOf(Main.configuration.doLogValid()));
        return 1;
    }

    private static int toggleTrackLast(CommandContext<FabricClientCommandSource> ctx) {
        Main.configuration.setTrackLast(!Main.configuration.doTrackLast());
        Message.info(String.valueOf(Main.configuration.doTrackLast()));
        DataHandler.invalidateLast();
        return 1;
    }

    private static int exportAll(CommandContext<FabricClientCommandSource> ctx) {
        Main.export();
        Message.info("OK");
        return 1;
    }

    private static int dumpViewed(CommandContext<FabricClientCommandSource> ctx) {
        Message.send(EntityEventHandler.getViewedEntities().toString());
        Message.info("OK");
        return 1;
    }

    private static int save(CommandContext<FabricClientCommandSource> ctx) {
        Main.save();
        Message.info("OK");
        return 1;
    }

    private static int load(CommandContext<FabricClientCommandSource> ctx) {
        try {
            Main.configuration.load();
        } catch (Exception e) {
            Message.fatal("Cannot load config: " + e.getMessage());
            e.printStackTrace();
        }
        Message.info("OK");
        return 1;
    }

    private static int dump(CommandContext<FabricClientCommandSource> ctx) {
        if (MinecraftClient.getInstance().world == null) {
            return 0;
        }

        for (Entity e : MinecraftClient.getInstance().world.getEntities()) {
            try {
                Text hoverText = Text.literal("Wynncraft Item Name:"
                        + getEntityDisplayName(e)
                        + "\n"
                        + "Item UUID: "
                        + e.getUuid()
                        + "\n\n"
                        + "Type: "
                        + e.getType()
                        + "\nPos: "
                        + e.getPos()
                        + "\n\nClick to track!");

                Text message = Message.builder("Entity " + e.getName().getString() + ":")
                        .addHoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText)
                        .addClickEvent(
                                ClickEvent.Action.RUN_COMMAND,
                                "/compass "
                                        + e.getBlockPos().getX()
                                        + " "
                                        + e.getBlockPos().getY()
                                        + " "
                                        + e.getBlockPos().getZ())
                        .build();

                Message.sendRaw(message);
                System.out.println(e.getName().getString() + "#Type=" + e.getType() + "#Pos=" + e.getPos());
            } catch (Exception ex) {
                Message.error("Error whilst dumping entity: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
        return 1;
    }

    private static String getEntityDisplayName(Entity e) {
        if (e.hasCustomName()) {
            return e.getCustomName().getString() + "(" + e.getName().getString() + ")";
        }
        return e.getName().getString();
    }
}
