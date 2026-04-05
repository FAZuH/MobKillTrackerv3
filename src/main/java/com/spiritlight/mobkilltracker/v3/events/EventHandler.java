package com.spiritlight.mobkilltracker.v3.events;

import com.spiritlight.mobkilltracker.v3.Main;
import com.spiritlight.mobkilltracker.v3.core.DataHandler;
import com.spiritlight.mobkilltracker.v3.utils.drops.DropManager;
import com.spiritlight.mobkilltracker.v3.utils.drops.DropStatistics;
import com.spiritlight.mobkilltracker.v3.utils.minecraft.Message;
import java.text.DecimalFormat;
import net.minecraft.client.MinecraftClient;

public class EventHandler {

    public static void onDisconnect() {
        if (DataHandler.getLastHandler() != null
                && DataHandler.getLastHandler() instanceof DataHandler.ListenerHandler) {
            ((DataHandler.ListenerHandler) DataHandler.getLastHandler())
                    .onTermination(new TerminationEvent(null, TerminationEvent.Type.TERMINATE));
        }
    }

    public static void onTotemPlacement(TotemEvent event) {
        Message.debugv("Caught TotemEvent");

        if (DataHandler.isInProgress()) {
            Message.warn("A totem is already in progress, ignoring this one...");
            return; // Ignore
        }
        Message.info("Found a totem, started recording...");
        DataHandler.newListenedHandler()
                .onTerminate(() -> true)
                .whenComplete(DropManager.instance::insert)
                .start();
    }

    public static void onMessageReceived(String message) {
        if (!Main.configuration.isModEnabled()) return;
        // Debug
        Main.LOGGER.info("[MKT] Chat message received: {}", message);
        if (message.contains("has placed a mob totem in")
                || message.contains("wynncraft.com/store")) {
            Main.LOGGER.info("[MKT] Totem placement detected!");
            onTotemPlacement(new TotemEvent());
        }
    }

    private static final DecimalFormat df = new DecimalFormat("0.00");

    public static void onCompletion(CompletionEvent event) {
        if (MinecraftClient.getInstance().player == null) return;
        Message.debugv("Caught CompletionEvent");

        DropStatistics drops = event.getHandler().getStats();
        int totalDrops = drops.getQuantity(DropStatistics.ALL);
        int itemDrops = drops.getQuantity(DropStatistics.ITEM);
        int ingDrops = drops.getQuantity(DropStatistics.INGREDIENT);
        int kills = drops.getKills();
        double ingRate = (ingDrops == 0 ? 0 : (double) kills / ingDrops);
        double itemRate = (itemDrops == 0 ? 0 : (double) kills / itemDrops);
        Message.info(
                "\n"
                        + "§3§l Mob Totem Ended\n"
                        + "§rTotal Mobs Killed: §c"
                        + kills
                        + "\n"
                        + "§rTotal Items Dropped: §a"
                        + totalDrops
                        + "\n"
                        + "\n"
                        + "§6§l Item Summary: \n"
                        + "§rIngredient Drops: §b[✫✫✫] §rx"
                        + drops.getIngredient3()
                        + " §d[✫✫§8✫§d] §rx"
                        + drops.getIngredient2()
                        + " §e[✫§8✫✫§e] §rx"
                        + drops.getIngredient1()
                        + " §7[§8✫✫✫§7] §rx"
                        + drops.getIngredient0()
                        + "\n"
                        + "§5§lMythic §rDrops: "
                        + drops.getMythic()
                        + "\n"
                        + "§cFabled §rDrops: "
                        + drops.getFabled()
                        + "\n"
                        + "§bLegendary §rDrops: "
                        + drops.getLegendary()
                        + "\n"
                        + "§dRare §rDrops: "
                        + drops.getRare()
                        + "\n"
                        + "§aSet §rDrops: "
                        + drops.getSet()
                        + "\n"
                        + "§eUnique §rDrops: "
                        + drops.getUnique()
                        + "\n"
                        + "§rNormal §rDrops: "
                        + drops.getNormal()
                        + "\n"
                        + "Total drops: Item "
                        + itemDrops
                        + ", Ingredients "
                        + ingDrops
                        + "\n §c§lAdvanced details:\n"
                        + "§rItem Rate: "
                        + df.format(itemRate)
                        + " §7(Mobs/item)"
                        + "\n"
                        + "§rIngredient Rate: "
                        + df.format(ingRate)
                        + " §7(Mobs/Ingredient)"
                        + "\n"
                        + "§rRarity Index: "
                        + drops.getRarityIndex());
    }

    public static void termination(TerminationEvent event) {
        Message.debugv("Caught TerminationEvent");
    }
}
