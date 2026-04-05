package com.spiritlight.mobkilltracker.v3;

import com.spiritlight.mobkilltracker.v3.command.MKTCommand;
import com.spiritlight.mobkilltracker.v3.command.MKTDebugCommand;
import com.spiritlight.mobkilltracker.v3.config.Config;
import com.spiritlight.mobkilltracker.v3.core.DataHandler;
import com.spiritlight.mobkilltracker.v3.events.EventHandler;
import com.spiritlight.mobkilltracker.v3.utils.ItemDatabase;
import com.spiritlight.mobkilltracker.v3.utils.drops.DropManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main implements ClientModInitializer {
    public static final String MODID = "mktv3";
    public static final String NAME = "MobKillTracker v3";
    public static final String VERSION = "3.0";

    public static final Logger LOGGER = LogManager.getLogger(MODID);
    public static final Config configuration = new Config();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(Main::export));
        Runtime.getRuntime().addShutdownHook(new Thread(Main::save));
    }

    @Override
    public void onInitializeClient() {
        try {
            Class.forName("com.spiritlight.mobkilltracker.v3.config.Config");
            configuration.load();
        } catch (Exception e) {
            LOGGER.error("Failed to fetch config: ", e);
        }

        try {
            ItemDatabase.instance.fetchItem();
        } catch (Exception e) {
            LOGGER.error("Cannot fetch the API: ", e);
        }

        ClientCommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess) -> {
                    MKTCommand.register(dispatcher);
                    MKTDebugCommand.register(dispatcher);
                });

        ClientReceiveMessageEvents.GAME.register(
                (message, overlay) -> {
                    EventHandler.onMessageReceived(message.getString());
                });

        ClientPlayConnectionEvents.DISCONNECT.register(
                (handler, client) -> {
                    EventHandler.onDisconnect();
                });

        ClientTickEvents.END_CLIENT_TICK.register(
                client -> {
                    if (client.world != null) {
                        if (DataHandler.isInProgress() && DataHandler.getLastHandler() != null) {
                            client.world
                                    .getEntities()
                                    .forEach(
                                            entity -> {
                                                DataHandler.getLastHandler()
                                                        .getHandler()
                                                        .onEntityUpdate(entity);
                                            });
                        }
                    }
                });
    }

    public static void export() {
        try {
            System.out.println("Exporting drops...");
            DropManager.exportAllDrops();
            System.out.println("Drops exported.");
        } catch (Throwable t) {
            t.printStackTrace();
            die(t);
            Thread.currentThread()
                    .getUncaughtExceptionHandler()
                    .uncaughtException(Thread.currentThread(), t);
        }
    }

    public static void save() {
        try {
            System.out.println("Saving config...");
            configuration.save();
            System.out.println("Config saved successfully.");
        } catch (Throwable t) {
            t.printStackTrace();
            die(t);
            Thread.currentThread()
                    .getUncaughtExceptionHandler()
                    .uncaughtException(Thread.currentThread(), t);
        }
    }

    public static void die(Throwable t) {
        if (t instanceof InterruptedException) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(t);
        }
    }
}
