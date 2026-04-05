package com.spiritlight.mobkilltracker.v3.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.spiritlight.mobkilltracker.v3.enums.Rarity;
import com.spiritlight.mobkilltracker.v3.enums.Tier;
import com.spiritlight.mobkilltracker.v3.enums.Type;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public class ItemDatabase {
    public static final ItemDatabase instance = new ItemDatabase();

    private final Map<String, Rarity> itemMap = new HashMap<>();
    private final Map<String, Tier> ingredientMap = new HashMap<>();

    public Type getItemType(String item) {
        if (itemMap.containsKey(item)) return Type.ITEM;
        if (ingredientMap.containsKey(item)) return Type.INGREDIENT;
        return Type.UNKNOWN;
    }

    @NotNull
    public Rarity getItemRarity(String item) {
        return itemMap.getOrDefault(item, Rarity.UNKNOWN);
    }

    @NotNull
    public Tier getIngredientTier(String ingredient) {
        return ingredientMap.getOrDefault(ingredient, Tier.UNKNOWN);
    }

    public void fetchItem() {
        System.out.println("Loading items from bundled data...");
        itemMap.clear();
        ingredientMap.clear();

        // Load from full result (contains both items and ingredients)
        loadFromFullResult();

        System.out.println(
                "[MKT-DEBUG] Loaded "
                        + itemMap.size()
                        + " items and "
                        + ingredientMap.size()
                        + " ingredients from bundled data");
        System.out.println("Items loaded.");
    }

    private void loadFromFullResult() {
        try (BufferedReader reader =
                new BufferedReader(
                        new InputStreamReader(
                                Objects.requireNonNull(
                                        getClass()
                                                .getResourceAsStream(
                                                        "/data/wynncraft-items-full.json")),
                                StandardCharsets.UTF_8))) {
            JsonElement dataElement = new Gson().fromJson(reader, JsonElement.class);

            if (dataElement == null || !dataElement.isJsonObject()) {
                System.out.println("[MKT-ERROR] Full result resource is null or not an object");
                return;
            }

            JsonObject dataObj = dataElement.getAsJsonObject();

            for (Map.Entry<String, JsonElement> entry : dataObj.entrySet()) {
                String itemName = entry.getKey();
                JsonElement itemData = entry.getValue();
                if (!itemData.isJsonObject()) continue;

                JsonObject itemObj = itemData.getAsJsonObject();
                String type = itemObj.has("type") ? itemObj.get("type").getAsString() : "";

                if ("ingredient".equals(type)) {
                    // Ingredient: has "tier" field like "TIER_0", "TIER_1", etc.
                    if (itemObj.has("tier")) {
                        String tierStr = itemObj.get("tier").getAsString();
                        Tier tier = parseTier(tierStr);
                        if (tier != Tier.UNKNOWN) {
                            ingredientMap.put(itemName, tier);
                        }
                    }
                } else {
                    // Item: has "tier" field for rarity (lowercase: normal, unique, rare, etc.)
                    if (itemObj.has("tier")) {
                        String rarity = itemObj.get("tier").getAsString();
                        Rarity itemRarity = Rarity.fromString(rarity);
                        if (itemRarity != Rarity.UNKNOWN) {
                            itemMap.put(itemName, itemRarity);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("[MKT-ERROR] Failed to load from full result: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Tier parseTier(String tierStr) {
        if (tierStr == null) return Tier.UNKNOWN;
        return switch (tierStr) {
            case "TIER_3", "3" -> Tier.THREE;
            case "TIER_2", "2" -> Tier.TWO;
            case "TIER_1", "1" -> Tier.ONE;
            case "TIER_0", "0" -> Tier.ZERO;
            default -> {
                System.out.println("[MKT-WARN] Unknown tier: " + tierStr);
                yield Tier.UNKNOWN;
            }
        };
    }
}
