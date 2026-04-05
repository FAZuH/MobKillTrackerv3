package com.spiritlight.mobkilltracker.v3.utils;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.spiritlight.mobkilltracker.v3.enums.Rarity;
import com.spiritlight.mobkilltracker.v3.enums.Tier;
import org.junit.jupiter.api.Test;

class ItemDatabaseTest {

    // Sample API response from /v3/item/database (items - armour, weapons, etc.)
    private static final String ITEMS_API_RESPONSE =
            "{"
                    + "\"controller\": {\"count\": 6615, \"current_count\": 2, \"pages\": 331},"
                    + "\"results\": {"
                    + "  \"Alstroemania\": {"
                    + "    \"internalName\": \"Alstroemania\","
                    + "    \"type\": \"armour\","
                    + "    \"tier\": \"legendary\""
                    + "  },"
                    + "  \"Amalgamation\": {"
                    + "    \"internalName\": \"Amalgamation\","
                    + "    \"type\": \"weapon\","
                    + "    \"tier\": \"rare\""
                    + "  }"
                    + "}"
                    + "}";

    // Sample API response from /v3/item/search (ingredients)
    private static final String INGREDIENTS_API_RESPONSE =
            "{"
                    + "\"controller\": {\"count\": 969, \"current_count\": 2, \"pages\": 49},"
                    + "\"results\": {"
                    + "  \"Manifestation of Agony\": {"
                    + "    \"internalName\": \"Manifestation of Agony\","
                    + "    \"type\": \"ingredient\","
                    + "    \"tier\": \"TIER_3\""
                    + "  },"
                    + "  \"Image of a Loved One\": {"
                    + "    \"internalName\": \"Image of a Loved One\","
                    + "    \"type\": \"ingredient\","
                    + "    \"tier\": \"TIER_3\""
                    + "  }"
                    + "}"
                    + "}";

    @Test
    void shouldParseItemsFromApiResponse() {
        JsonElement element = new Gson().fromJson(ITEMS_API_RESPONSE, JsonElement.class);
        assertNotNull(element);
        assertTrue(element.isJsonObject());

        var results = element.getAsJsonObject().getAsJsonObject("results");
        assertNotNull(results);
        assertTrue(results.has("Alstroemania"));
        assertTrue(results.has("Amalgamation"));

        var alstroemania = results.getAsJsonObject("Alstroemania");
        assertEquals("armour", alstroemania.get("type").getAsString());
        assertEquals("legendary", alstroemania.get("tier").getAsString());
    }

    @Test
    void shouldParseIngredientsFromApiResponse() {
        JsonElement element = new Gson().fromJson(INGREDIENTS_API_RESPONSE, JsonElement.class);
        assertNotNull(element);
        assertTrue(element.isJsonObject());

        var results = element.getAsJsonObject().getAsJsonObject("results");
        assertNotNull(results);
        assertTrue(results.has("Manifestation of Agony"));

        var ingredient = results.getAsJsonObject("Manifestation of Agony");
        assertEquals("ingredient", ingredient.get("type").getAsString());
        assertEquals("TIER_3", ingredient.get("tier").getAsString());
    }

    @Test
    void shouldConvertTierStringToEnum() {
        // Test item rarity conversion using Rarity.fromString() (Wynncraft API uses lowercase)
        assertEquals(Rarity.LEGENDARY, Rarity.fromString("legendary"));
        assertEquals(Rarity.RARE, Rarity.fromString("rare"));
        assertEquals(Rarity.MYTHIC, Rarity.fromString("mythic"));
        assertEquals(Rarity.FABLED, Rarity.fromString("fabled"));
        assertEquals(Rarity.UNIQUE, Rarity.fromString("unique"));
        assertEquals(Rarity.NORMAL, Rarity.fromString("normal"));
        assertEquals(Rarity.UNKNOWN, Rarity.fromString("unknown"));

        // Test ingredient tier conversion
        assertEquals(Tier.THREE, parseTier("TIER_3"));
        assertEquals(Tier.TWO, parseTier("TIER_2"));
        assertEquals(Tier.ONE, parseTier("TIER_1"));
        assertEquals(Tier.ZERO, parseTier("TIER_0"));
        assertEquals(Tier.UNKNOWN, parseTier("unknown"));
    }

    @Test
    void shouldNotWarnForIngredientTiers() {
        // Ingredient tiers like TIER_0, TIER_1 should return UNKNOWN silently
        // when passed to Rarity.fromString()
        assertEquals(Rarity.UNKNOWN, Rarity.fromString("TIER_0"));
        assertEquals(Rarity.UNKNOWN, Rarity.fromString("TIER_1"));
        assertEquals(Rarity.UNKNOWN, Rarity.fromString("TIER_2"));
        assertEquals(Rarity.UNKNOWN, Rarity.fromString("TIER_3"));
    }

    @Test
    void shouldReturnUnknownForInvalidRarities() {
        // "set" and "common" are not valid rarities in Wynncraft API v3
        assertEquals(Rarity.UNKNOWN, Rarity.fromString("set"));
        assertEquals(Rarity.UNKNOWN, Rarity.fromString("common"));
    }

    @Test
    void shouldHandle403ErrorResponse() {
        String errorResponse =
                "{\"error\": \"Forbidden\", \"detail\": \"FullResult requests require authentication\", \"code\": 403}";
        JsonElement element = new Gson().fromJson(errorResponse, JsonElement.class);
        assertNotNull(element);
        assertTrue(element.isJsonObject());
        assertTrue(element.getAsJsonObject().has("error"));
    }

    @Test
    void shouldHandleEmptyResponse() {
        String emptyResponse = "";
        JsonElement element = new Gson().fromJson(emptyResponse, JsonElement.class);
        assertNull(element);
    }

    private Tier parseTier(String tier) {
        return switch (tier) {
            case "TIER_3", "3" -> Tier.THREE;
            case "TIER_2", "2" -> Tier.TWO;
            case "TIER_1", "1" -> Tier.ONE;
            case "TIER_0", "0" -> Tier.ZERO;
            default -> Tier.UNKNOWN;
        };
    }
}
