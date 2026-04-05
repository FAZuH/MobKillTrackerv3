package com.spiritlight.mobkilltracker.v3.core;

import static com.spiritlight.mobkilltracker.v3.utils.SharedConstants.TOSS_MAGIC;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.spiritlight.mobkilltracker.v3.Main;
import com.spiritlight.mobkilltracker.v3.enums.Color;
import com.spiritlight.mobkilltracker.v3.enums.Rarity;
import com.spiritlight.mobkilltracker.v3.enums.Type;
import com.spiritlight.mobkilltracker.v3.utils.ItemDatabase;
import com.spiritlight.mobkilltracker.v3.utils.collections.ConcurrentTimedSet;
import com.spiritlight.mobkilltracker.v3.utils.drops.DropStatistics;
import com.spiritlight.mobkilltracker.v3.utils.math.StrictMath;
import com.spiritlight.mobkilltracker.v3.utils.minecraft.Message;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;

public class EntityEventHandler {
    private final DropStatistics stats = new DropStatistics();

    private static final List<String> KILL_INDICATOR = ImmutableList.of("combat xp", "guild xp", "shared");

    private static final Set<UUID> viewedEntities = new ConcurrentTimedSet<>(300, TimeUnit.SECONDS);

    public EntityEventHandler() {
        Message.debugv("Constructing EntityEventHandler");

        // Preventing duplications
        if (MinecraftClient.getInstance().world != null) {
            for (Entity e : MinecraftClient.getInstance().world.getEntities()) {
                this.storedEntities.add(e);
            }
        }
    }

    public DropStatistics getStats() {
        return stats;
    }

    private final Set<Entity> storedEntities = new LinkedHashSet<>();

    public static Set<UUID> getViewedEntities() {
        return ImmutableSet.copyOf(viewedEntities);
    }

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(16);

    public void onEntityUpdate(Entity entity) {
        if (entity == null) {
            Main.LOGGER.debug("[MKT] onEntityUpdate: entity is null");
            return;
        }
        if (storedEntities.contains(entity)) {
            Main.LOGGER.debug("[MKT] onEntityUpdate: entity already stored, skipping");
            return;
        }
        if (viewedEntities.contains(entity.getUuid())) {
            Main.LOGGER.debug("[MKT] onEntityUpdate: UUID already viewed, skipping");
            return;
        }

        Main.LOGGER.info(
                "[MKT] onEntityUpdate: Processing entity {} (type={})",
                entity.getName().getString(),
                entity.getClass().getSimpleName());

        if (MinecraftClient.getInstance().world == null) {
            Main.LOGGER.debug("[MKT] onEntityUpdate: world is null");
            return;
        }

        if (Main.configuration.getDelayMills() == 0) {
            CompletableFuture.runAsync(() -> this.processEntity(entity));
        } else {
            executor.schedule(() -> processEntity(entity), Main.configuration.getDelayMills(), TimeUnit.MILLISECONDS);
        }
    }

    private boolean processToss(ItemEntity entity) {

        if (Main.configuration.getDelayMills() != 0) return false;

        double entityY = entity.getY();
        List<? extends PlayerEntity> player =
                MinecraftClient.getInstance().world.getPlayers();
        double[] yAxis = player.stream()
                .filter(p -> !p.isDead())
                .mapToDouble(p -> p.getY())
                .toArray();
        for (double playerY : yAxis) {
            if (StrictMath.add(playerY, TOSS_MAGIC) == entityY) {
                Message.debugv("Cancelled item " + entity.getName().getString() + " due to dropped item detection");
                storedEntities.add(entity);
                viewedEntities.add(entity.getUuid());
                return true;
            }
        }
        return false;
    }

    private void processEntity(Entity entity) {
        Main.LOGGER.debug("[MKT] processEntity: Adding to storedEntities");
        storedEntities.add(entity);
        if (entity == null) {
            Main.LOGGER.debug("[MKT] processEntity: entity became null");
            return;
        }
        if (!viewedEntities.add(entity.getUuid())) {
            Main.LOGGER.debug("[MKT] processEntity: UUID already in viewedEntities, skipping");
            return;
        }

        Main.LOGGER.info(
                "[MKT] processEntity: Processing {} (isItemEntity={})",
                entity.getName().getString(),
                entity instanceof ItemEntity);

        if (entity instanceof ItemEntity) {
            ItemEntity entityItem = (ItemEntity) entity;
            Main.LOGGER.debug("[MKT] processEntity: ItemEntity detected");

            // Ignoring emerald for sake of our life
            if (entityItem.getStack().isOf(Items.EMERALD)) {
                Main.LOGGER.debug("[MKT] processEntity: Skipping emerald");
                return;
            }

            // Get item name from the item stack's display name and strip color codes
            String itemName = entityItem.getStack().getName().getString().replaceAll("§[0-9a-fk-or]", "");

            Type type = ItemDatabase.instance.getItemType(itemName);

            if (type == Type.UNKNOWN) {
                Main.LOGGER.debug("[MKT] processEntity: Unknown item, skipping: '{}'", itemName);
                return;
            }
            Main.LOGGER.info("[MKT] processEntity: Detected Wynncraft item '{}' (type={})", itemName, type);

            // Old schooled way due to involving some huge ass component that I'm too lazy to change
            if (Main.configuration.isLogging() || Main.configuration.doLogValid()) {
                Text itc = Message.builder(
                                "Processing item entity " + entity.getName().getString())
                        .addHoverEvent(
                                HoverEvent.Action.SHOW_TEXT,
                                Text.literal(Message.formatJson("Wynncraft Item Name:"
                                        + itemName
                                        + "\n\n"
                                        + "Item name: "
                                        + (entity.hasCustomName()
                                                ? entity.getCustomName().getString()
                                                        + "("
                                                        + entity.getName().getString()
                                                        + ")"
                                                : entity.getName().getString())
                                        + "\n"
                                        + "Item UUID: "
                                        + entity.getUuid()
                                        + "\n\nClick to track!")))
                        .addClickEvent(
                                ClickEvent.Action.RUN_COMMAND,
                                "/compass "
                                        + entity.getBlockPos().getX()
                                        + " "
                                        + entity.getBlockPos().getY()
                                        + " "
                                        + entity.getBlockPos().getZ())
                        .build();
                Message.sendRaw(itc);
            }
            if (processToss(entityItem)) return;
            switch (type) {
                case ITEM:
                    this.manageRarity(ItemDatabase.instance.getItemRarity(itemName));
                    break;
                case INGREDIENT:
                    this.stats.addTier(ItemDatabase.instance.getIngredientTier(itemName));
                    break;
            }
        } else {
            // Process entities here
            if (KILL_INDICATOR.stream().anyMatch(str -> entity.getName()
                    .getString()
                    .toLowerCase(Locale.ROOT)
                    .contains(str))) {
                if (Main.configuration.doLogValid()) {
                    Text component = Message.builder(Color.MAGENTA
                                    + "Processing kill "
                                    + entity.getName().getString())
                            .addHoverEvent(
                                    HoverEvent.Action.SHOW_TEXT,
                                    Text.literal(
                                            Message.formatJson(String.valueOf(entity.writeNbt(new NbtCompound())))))
                            .build();
                    Message.sendRaw(component);
                }
                stats.addKill();
            }
        }
    }

    private void manageRarity(Rarity rarity) {
        switch (rarity) {
            case MYTHIC:
                stats.addMythic();
                break;
            case FABLED:
                stats.addFabled();
                break;
            case LEGENDARY:
                stats.addLegendary();
                break;
            case RARE:
                stats.addRare();
                break;
            case UNIQUE:
                stats.addUnique();
                break;
            case NORMAL:
                stats.addNormal();
                break;
            case UNKNOWN:
                System.err.println("Found unknown rarity!");
        }
    }

    private void removeRarity(Rarity rarity) {
        switch (rarity) {
            case MYTHIC:
                stats.removeMythic();
                break;
            case FABLED:
                stats.removeFabled();
                break;
            case LEGENDARY:
                stats.removeLegendary();
                break;
            case RARE:
                stats.removeRare();
                break;
            case UNIQUE:
                stats.removeUnique();
                break;
            case NORMAL:
                stats.removeNormal();
                break;
            case UNKNOWN:
        }
    }
}
