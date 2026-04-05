package com.spiritlight.mobkilltracker.v3.command;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.spiritlight.mobkilltracker.v3.Main;
import com.spiritlight.mobkilltracker.v3.enums.Color;
import com.spiritlight.mobkilltracker.v3.utils.drops.DropManager;
import com.spiritlight.mobkilltracker.v3.utils.drops.DropStatistics;
import com.spiritlight.mobkilltracker.v3.utils.minecraft.Message;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class MKTCommand {

    private static final DecimalFormat df = new DecimalFormat("0.00");

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal("mkt")
                .executes(ctx -> {
                    help();
                    return 1;
                })
                .then(literal("toggle").executes(MKTCommand::toggle))
                .then(literal("last").executes(MKTCommand::showLast))
                .then(buildTraceCommand())
                .then(buildNoteCommand())
                .then(buildExportCommand()));
    }

    private static LiteralArgumentBuilder<FabricClientCommandSource> buildTraceCommand() {
        return literal("trace")
                .executes(MKTCommand::traceHelp)
                .then(literal("list").executes(MKTCommand::traceList))
                .then(literal("delete")
                        .then(literal("all").executes(MKTCommand::deleteAll))
                        .then(literal("last").executes(MKTCommand::deleteLast))
                        .then(argument("index", IntegerArgumentType.integer()).executes(MKTCommand::deleteIndex)))
                .then(argument("index", IntegerArgumentType.integer()).executes(MKTCommand::showTraceIndex));
    }

    private static LiteralArgumentBuilder<FabricClientCommandSource> buildNoteCommand() {
        return literal("note")
                .executes(MKTCommand::noteHelp)
                .then(literal("all")
                        .then(argument("note", StringArgumentType.greedyString())
                                .executes(MKTCommand::noteAll)))
                .then(literal("last")
                        .then(argument("note", StringArgumentType.greedyString())
                                .executes(MKTCommand::noteLast)))
                .then(argument("index", IntegerArgumentType.integer())
                        .then(argument("note", StringArgumentType.greedyString())
                                .executes(MKTCommand::noteIndex)));
    }

    private static LiteralArgumentBuilder<FabricClientCommandSource> buildExportCommand() {
        return literal("export")
                .executes(MKTCommand::exportHelp)
                .then(literal("all")
                        .then(argument("name", StringArgumentType.string()).executes(MKTCommand::exportAll)))
                .then(literal("last")
                        .then(argument("name", StringArgumentType.string()).executes(MKTCommand::exportLast)));
    }

    private static int toggle(CommandContext<FabricClientCommandSource> ctx) {
        boolean enabled = !Main.configuration.isModEnabled();
        Main.configuration.setModEnabled(enabled);
        Message.send("Toggled mod to " + (enabled ? Color.GREEN + "ON" : Color.RED + "OFF"));
        return 1;
    }

    private static int showLast(CommandContext<FabricClientCommandSource> ctx) {
        if (!DropManager.instance.hasData()) {
            Message.warn("There are no data available right now.");
            return 0;
        }
        Message.send(DropManager.dropToString(DropManager.instance.getLast()));
        return 1;
    }

    private static int traceHelp(CommandContext<FabricClientCommandSource> ctx) {
        int size = DropManager.instance.size();
        Message.send("There are currently " + size + (size == 1 ? " stat" : " stats") + " available.");
        Message.send("Do /mkt trace list to see all of them in brief context.");
        Message.send("Or do /mkt trace <index> to see the specific of that stat.");
        Message.send("Do /mkt trace delete <index> to delete that specific index.");
        Message.send("Additionally you can do /mkt trace delete all to wipe them.");
        return 1;
    }

    private static int traceList(CommandContext<FabricClientCommandSource> ctx) {
        Message.send("- - - Current Session Caches - - -");
        int totalKills = 0, totalItems = 0, totalIngredients = 0;

        for (int i = 0; i < DropManager.instance.size(); i++) {
            DropStatistics stats = DropManager.instance.get(i);
            int items = stats.getQuantity(DropStatistics.ITEM);
            int ingredients = stats.getQuantity(DropStatistics.INGREDIENT);
            double itemRate = items > 0 ? (double) stats.getKills() / items : 0;
            double ingRate = ingredients > 0 ? (double) stats.getKills() / ingredients : 0;

            Message.send(formatTraceEntry(i + 1, stats, items, ingredients, itemRate, ingRate));

            if (stats.hasNote()) {
                Message.send("  §7Notes: " + stats.getNote());
            }

            totalKills += stats.getKills();
            totalItems += items;
            totalIngredients += ingredients;
        }

        double count = DropManager.instance.size();
        if (count > 0) {
            Message.send(formatTraceSummary(totalKills, totalItems, totalIngredients, count));
        }
        return 1;
    }

    private static String formatTraceEntry(
            int index, DropStatistics stats, int items, int ingredients, double itemRate, double ingRate) {
        return "Cache #"
                + index
                + ": §r"
                + stats.getKills()
                + "§a kills; §r"
                + stats.getQuantity(DropStatistics.ALL)
                + "§a drops §7(§r"
                + items
                + "§7 items, §r"
                + ingredients
                + "§7 ingredients) §c(§7"
                + df.format(itemRate)
                + ":"
                + df.format(ingRate)
                + "§c) "
                + stats.getRarityIndex();
    }

    private static String formatTraceSummary(int kills, int items, int ingredients, double count) {
        return "Stats (Avg.): Kills: "
                + kills
                + " ("
                + df.format(kills / count)
                + "), Items: "
                + items
                + " ("
                + df.format(items / count)
                + "), Ingredients: "
                + ingredients
                + " ("
                + df.format(ingredients / count)
                + ")";
    }

    private static int deleteAll(CommandContext<FabricClientCommandSource> ctx) {
        Message.send("Cleared ALL session data!");
        DropManager.instance.clear();
        return 1;
    }

    private static int deleteLast(CommandContext<FabricClientCommandSource> ctx) {
        if (DropManager.instance.size() > 0) {
            DropManager.instance.remove(DropManager.instance.size() - 1);
            Message.send("Deleted last session data!");
        }
        return 1;
    }

    private static int deleteIndex(CommandContext<FabricClientCommandSource> ctx) {
        int idx = IntegerArgumentType.getInteger(ctx, "index") - 1;
        if (idx < 0 || idx >= DropManager.instance.size()) {
            Message.send("Invalid index.");
            return 0;
        }
        DropManager.instance.remove(idx);
        Message.send("Successfully removed index #" + (idx + 1) + "!");
        return 1;
    }

    private static int showTraceIndex(CommandContext<FabricClientCommandSource> ctx) {
        int idx = IntegerArgumentType.getInteger(ctx, "index") - 1;
        if (idx < 0 || idx >= DropManager.instance.size()) {
            Message.send("Index illegal. Max index allowed: " + DropManager.instance.size());
            return 0;
        }
        Message.send(DropManager.dropToString(DropManager.instance.get(idx)));
        return 1;
    }

    private static int noteHelp(CommandContext<FabricClientCommandSource> ctx) {
        Message.send("/mkt note all [note] - Sets all drop data with the note.");
        Message.send("/mkt note last [note] - Sets last drop data with the note.");
        Message.send("/mkt note # [note] - Sets specified drop data with this note.");
        Message.send("If note was left empty, the notes are cleared.");
        return 1;
    }

    private static int noteAll(CommandContext<FabricClientCommandSource> ctx) {
        String note = StringArgumentType.getString(ctx, "note");
        for (DropStatistics drops : DropManager.instance.getBackingList()) {
            drops.setNote(note);
        }
        Message.send("Finished attaching note to all!");
        return 1;
    }

    private static int noteLast(CommandContext<FabricClientCommandSource> ctx) {
        if (DropManager.instance.size() > 0) {
            String note = StringArgumentType.getString(ctx, "note");
            DropManager.instance.get(DropManager.instance.size() - 1).setNote(note);
            Message.send("Finished attaching note to last!");
        }
        return 1;
    }

    private static int noteIndex(CommandContext<FabricClientCommandSource> ctx) {
        int idx = IntegerArgumentType.getInteger(ctx, "index") - 1;
        String note = StringArgumentType.getString(ctx, "note");
        if (idx < 0 || idx >= DropManager.instance.size()) {
            Message.send("Invalid operation.");
            return 0;
        }
        DropManager.instance.get(idx).setNote(note);
        Message.send("Successfully attached note " + note + " to data #" + (idx + 1));
        return 1;
    }

    private static int exportHelp(CommandContext<FabricClientCommandSource> ctx) {
        int size = DropManager.instance.size();
        Message.send("/mkt export all [name]: Exports all session data as JSON.");
        Message.send("/mkt export range [name] <min> [max]: Exports session data in range as JSON.");
        Message.send("/mkt export last [name]: Exports last session data as JSON.");
        Message.send("/mkt export # [name]: Exports the #th index as JSON.");
        Message.send(
                "View stats via /mkt trace. There are " + size + (size == 1 ? " entry" : " entries") + " available.");
        return 1;
    }

    private static int exportAll(CommandContext<FabricClientCommandSource> ctx) {
        String name = StringArgumentType.getString(ctx, "name");
        DropManager.exportDrops(name, new ArrayList<>(DropManager.instance.getBackingList()));
        return 1;
    }

    private static int exportLast(CommandContext<FabricClientCommandSource> ctx) {
        if (DropManager.instance.size() > 0) {
            String name = StringArgumentType.getString(ctx, "name");
            DropManager.exportDrops(name, Collections.singletonList(DropManager.instance.getLast()));
        }
        return 1;
    }

    private static void help() {
        Message.send("[ +-- MobKillTracker v3 --+ ]");
        Message.send("/mkt toggle - Toggles mod");
        Message.send("/mkt last - Shows last stat");
        Message.send("/mkt trace - Traces previous stats");
        Message.send("/mkt note - Note related commands");
        Message.send("/mkt export - Export related commands");
    }
}
