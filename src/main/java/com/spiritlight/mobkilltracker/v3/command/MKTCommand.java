package com.spiritlight.mobkilltracker.v3.command;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.spiritlight.mobkilltracker.v3.Main;
import com.spiritlight.mobkilltracker.v3.enums.Color;
import com.spiritlight.mobkilltracker.v3.utils.drops.DropManager;
import com.spiritlight.mobkilltracker.v3.utils.drops.DropStatistics;
import com.spiritlight.mobkilltracker.v3.utils.minecraft.Message;
import java.text.DecimalFormat;
import java.util.*;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class MKTCommand {

    private static final DecimalFormat df = new DecimalFormat("0.00");

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
                literal("mkt")
                        .executes(
                                context -> {
                                    help();
                                    return 1;
                                })
                        .then(
                                literal("toggle")
                                        .executes(
                                                context -> {
                                                    Main.configuration.setModEnabled(
                                                            !Main.configuration.isModEnabled());
                                                    Message.send(
                                                            "Toggled mod to "
                                                                    + (Main.configuration
                                                                                    .isModEnabled()
                                                                            ? Color.GREEN + "ON"
                                                                            : Color.RED + "OFF"));
                                                    return 1;
                                                }))
                        .then(
                                literal("last")
                                        .executes(
                                                context -> {
                                                    if (!DropManager.instance.hasData()) {
                                                        Message.warn(
                                                                "There are no data available right now.");
                                                        return 0;
                                                    }
                                                    Message.send(
                                                            DropManager.dropToString(
                                                                    DropManager.instance
                                                                            .getLast()));
                                                    return 1;
                                                }))
                        .then(
                                literal("trace")
                                        .executes(
                                                context -> {
                                                    Message.send(
                                                            "There are currently "
                                                                    + DropManager.instance.size()
                                                                    + (DropManager.instance.size()
                                                                                    == 1
                                                                            ? " stat"
                                                                            : " stats")
                                                                    + " available.");
                                                    Message.send(
                                                            "Do /mkt trace list to see all of them in brief context.");
                                                    Message.send(
                                                            "Or do /mkt trace <index> to see the specific of that stat.");
                                                    Message.send(
                                                            "Do /mkt trace delete <index> to delete that specific index.");
                                                    Message.send(
                                                            "Additionally you can do /mkt trace delete all to wipe them.");
                                                    return 1;
                                                })
                                        .then(
                                                literal("list")
                                                        .executes(
                                                                context -> {
                                                                    Message.send(
                                                                            "- - - Current Session Caches - - -");
                                                                    int kills = 0,
                                                                            items = 0,
                                                                            ingredients = 0;
                                                                    for (int i = 0;
                                                                            i
                                                                                    < DropManager
                                                                                            .instance
                                                                                            .size();
                                                                            i++) {
                                                                        final DropStatistics tmp =
                                                                                DropManager.instance
                                                                                        .get(i);
                                                                        final int item =
                                                                                tmp.getQuantity(
                                                                                        DropStatistics
                                                                                                .ITEM);
                                                                        final int ing =
                                                                                tmp.getQuantity(
                                                                                        DropStatistics
                                                                                                .INGREDIENT);
                                                                        final double iAvg =
                                                                                (item <= 0
                                                                                        ? 0
                                                                                        : (double)
                                                                                                        tmp
                                                                                                                .getKills()
                                                                                                / item);
                                                                        final double inAvg =
                                                                                (tmp.getQuantity(
                                                                                                        DropStatistics
                                                                                                                .INGREDIENT)
                                                                                                <= 0
                                                                                        ? 0
                                                                                        : (double)
                                                                                                        tmp
                                                                                                                .getKills()
                                                                                                / ing);
                                                                        Message.send(
                                                                                "Cache #"
                                                                                        + (i + 1)
                                                                                        + ": §r"
                                                                                        + tmp
                                                                                                .getKills()
                                                                                        + "§a kills; §r"
                                                                                        + tmp
                                                                                                .getQuantity(
                                                                                                        DropStatistics
                                                                                                                .ALL)
                                                                                        + "§a drops §7(§r"
                                                                                        + item
                                                                                        + "§7 items, §r"
                                                                                        + ing
                                                                                        + "§7 ingredients) §c(§7"
                                                                                        + df.format(
                                                                                                iAvg)
                                                                                        + ":"
                                                                                        + df.format(
                                                                                                inAvg)
                                                                                        + "§c) "
                                                                                        + tmp
                                                                                                .getRarityIndex());
                                                                        if (tmp.hasNote()) {
                                                                            Message.send(
                                                                                    "§7Notes of this data: "
                                                                                            + tmp
                                                                                                    .getNote());
                                                                        }
                                                                        kills += tmp.getKills();
                                                                        items += item;
                                                                        ingredients += ing;
                                                                    }
                                                                    final double divisor =
                                                                            DropManager.instance
                                                                                    .size();
                                                                    if (divisor > 0) {
                                                                        Message.send(
                                                                                "Stats (Avg.): Kills: "
                                                                                        + kills
                                                                                        + " ("
                                                                                        + df.format(
                                                                                                kills
                                                                                                        / divisor)
                                                                                        + "), Items: "
                                                                                        + items
                                                                                        + " ("
                                                                                        + df.format(
                                                                                                items
                                                                                                        / divisor)
                                                                                        + "), Ingredients: "
                                                                                        + ingredients
                                                                                        + " ("
                                                                                        + df.format(
                                                                                                ingredients
                                                                                                        / divisor)
                                                                                        + ")");
                                                                    }
                                                                    return 1;
                                                                }))
                                        .then(
                                                literal("delete")
                                                        .then(
                                                                literal("all")
                                                                        .executes(
                                                                                context -> {
                                                                                    Message.send(
                                                                                            "Cleared ALL session data!");
                                                                                    DropManager
                                                                                            .instance
                                                                                            .clear();
                                                                                    return 1;
                                                                                }))
                                                        .then(
                                                                literal("last")
                                                                        .executes(
                                                                                context -> {
                                                                                    if (DropManager
                                                                                                    .instance
                                                                                                    .size()
                                                                                            > 0) {
                                                                                        DropManager
                                                                                                .instance
                                                                                                .remove(
                                                                                                        DropManager
                                                                                                                        .instance
                                                                                                                        .size()
                                                                                                                - 1);
                                                                                        Message
                                                                                                .send(
                                                                                                        "Deleted last session data!");
                                                                                    }
                                                                                    return 1;
                                                                                }))
                                                        .then(
                                                                argument(
                                                                                "index",
                                                                                IntegerArgumentType
                                                                                        .integer())
                                                                        .executes(
                                                                                context -> {
                                                                                    int idx =
                                                                                            IntegerArgumentType
                                                                                                            .getInteger(
                                                                                                                    context,
                                                                                                                    "index")
                                                                                                    - 1;
                                                                                    if (idx < 0
                                                                                            || idx
                                                                                                    >= DropManager
                                                                                                            .instance
                                                                                                            .size()) {
                                                                                        Message
                                                                                                .send(
                                                                                                        "Invalid index.");
                                                                                        return 0;
                                                                                    }
                                                                                    DropManager
                                                                                            .instance
                                                                                            .remove(
                                                                                                    idx);
                                                                                    Message.send(
                                                                                            "Successfully removed index #"
                                                                                                    + (idx
                                                                                                            + 1)
                                                                                                    + "!");
                                                                                    return 1;
                                                                                })))
                                        .then(
                                                argument("index", IntegerArgumentType.integer())
                                                        .executes(
                                                                context -> {
                                                                    int idx =
                                                                            IntegerArgumentType
                                                                                            .getInteger(
                                                                                                    context,
                                                                                                    "index")
                                                                                    - 1;
                                                                    if (idx < 0
                                                                            || idx
                                                                                    >= DropManager
                                                                                            .instance
                                                                                            .size()) {
                                                                        Message.send(
                                                                                "Index illegal. Max index allowed: "
                                                                                        + DropManager
                                                                                                .instance
                                                                                                .size());
                                                                        return 0;
                                                                    }
                                                                    Message.send(
                                                                            DropManager
                                                                                    .dropToString(
                                                                                            DropManager
                                                                                                    .instance
                                                                                                    .get(
                                                                                                            idx)));
                                                                    return 1;
                                                                })))
                        .then(
                                literal("note")
                                        .executes(
                                                context -> {
                                                    Message.send(
                                                            "/mkt note all [note] - Sets all drop data with the note.");
                                                    Message.send(
                                                            "/mkt note last [note] - Sets last drop data with the note.");
                                                    Message.send(
                                                            "/mkt note # [note] - Sets specified drop data with this note.");
                                                    Message.send(
                                                            "If note was left empty, the notes are cleared.");
                                                    return 1;
                                                })
                                        .then(
                                                literal("all")
                                                        .then(
                                                                argument(
                                                                                "note",
                                                                                StringArgumentType
                                                                                        .greedyString())
                                                                        .executes(
                                                                                context -> {
                                                                                    String note =
                                                                                            StringArgumentType
                                                                                                    .getString(
                                                                                                            context,
                                                                                                            "note");
                                                                                    for (DropStatistics
                                                                                            drops :
                                                                                                    DropManager
                                                                                                            .instance
                                                                                                            .getBackingList()) {
                                                                                        drops
                                                                                                .setNote(
                                                                                                        note);
                                                                                    }
                                                                                    Message.send(
                                                                                            "Finished attaching note to all!");
                                                                                    return 1;
                                                                                })))
                                        .then(
                                                literal("last")
                                                        .then(
                                                                argument(
                                                                                "note",
                                                                                StringArgumentType
                                                                                        .greedyString())
                                                                        .executes(
                                                                                context -> {
                                                                                    String note =
                                                                                            StringArgumentType
                                                                                                    .getString(
                                                                                                            context,
                                                                                                            "note");
                                                                                    if (DropManager
                                                                                                    .instance
                                                                                                    .size()
                                                                                            > 0) {
                                                                                        DropManager
                                                                                                .instance
                                                                                                .get(
                                                                                                        DropManager
                                                                                                                        .instance
                                                                                                                        .size()
                                                                                                                - 1)
                                                                                                .setNote(
                                                                                                        note);
                                                                                        Message
                                                                                                .send(
                                                                                                        "Finished attaching note to last!");
                                                                                    }
                                                                                    return 1;
                                                                                })))
                                        .then(
                                                argument("index", IntegerArgumentType.integer())
                                                        .then(
                                                                argument(
                                                                                "note",
                                                                                StringArgumentType
                                                                                        .greedyString())
                                                                        .executes(
                                                                                context -> {
                                                                                    int idx =
                                                                                            IntegerArgumentType
                                                                                                            .getInteger(
                                                                                                                    context,
                                                                                                                    "index")
                                                                                                    - 1;
                                                                                    String note =
                                                                                            StringArgumentType
                                                                                                    .getString(
                                                                                                            context,
                                                                                                            "note");
                                                                                    if (idx < 0
                                                                                            || idx
                                                                                                    >= DropManager
                                                                                                            .instance
                                                                                                            .size()) {
                                                                                        Message
                                                                                                .send(
                                                                                                        "Invalid operation.");
                                                                                        return 0;
                                                                                    }
                                                                                    DropManager
                                                                                            .instance
                                                                                            .get(
                                                                                                    idx)
                                                                                            .setNote(
                                                                                                    note);
                                                                                    Message.send(
                                                                                            "Successfully attached note "
                                                                                                    + note
                                                                                                    + " to data #"
                                                                                                    + (idx
                                                                                                            + 1));
                                                                                    return 1;
                                                                                }))))
                        .then(
                                literal("export")
                                        .executes(
                                                context -> {
                                                    final int ENTRY_SIZE =
                                                            DropManager.instance.size();
                                                    Message.send(
                                                            "/mkt export all [name]: Exports all session data as JSON.");
                                                    Message.send(
                                                            "/mkt export range [name] <min> [max]: Exports session data in range as JSON.");
                                                    Message.send(
                                                            "/mkt export last [name]: Exports last session data as JSON.");
                                                    Message.send(
                                                            "/mkt export # [name]: Exports the #th index as JSON.");
                                                    Message.send(
                                                            "View stats via /mkt trace. There are "
                                                                    + ENTRY_SIZE
                                                                    + (ENTRY_SIZE == 1
                                                                            ? " entry"
                                                                            : " entries")
                                                                    + " available.");
                                                    return 1;
                                                })
                                        .then(
                                                literal("all")
                                                        .then(
                                                                argument(
                                                                                "name",
                                                                                StringArgumentType
                                                                                        .string())
                                                                        .executes(
                                                                                context -> {
                                                                                    String name =
                                                                                            StringArgumentType
                                                                                                    .getString(
                                                                                                            context,
                                                                                                            "name");
                                                                                    DropManager
                                                                                            .exportDrops(
                                                                                                    name,
                                                                                                    new ArrayList<>(
                                                                                                            DropManager
                                                                                                                    .instance
                                                                                                                    .getBackingList()));
                                                                                    return 1;
                                                                                })))
                                        .then(
                                                literal("last")
                                                        .then(
                                                                argument(
                                                                                "name",
                                                                                StringArgumentType
                                                                                        .string())
                                                                        .executes(
                                                                                context -> {
                                                                                    String name =
                                                                                            StringArgumentType
                                                                                                    .getString(
                                                                                                            context,
                                                                                                            "name");
                                                                                    if (DropManager
                                                                                                    .instance
                                                                                                    .size()
                                                                                            > 0) {
                                                                                        DropManager
                                                                                                .exportDrops(
                                                                                                        name,
                                                                                                        Collections
                                                                                                                .singletonList(
                                                                                                                        DropManager
                                                                                                                                .instance
                                                                                                                                .getLast()));
                                                                                    }
                                                                                    return 1;
                                                                                })))));
    }

    private static void help() {
        Message.send("[ +-- MobKillTracker v3 --+ ]");
        Message.send(
                "/mkt toggle - Toggles mod",
                "/mkt last - Shows last stat",
                "/mkt trace - Traces previous stats",
                "/mkt note - Note related commands",
                "/mkt export - Export related commands");
    }
}
