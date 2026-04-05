package com.spiritlight.mobkilltracker.v3.events;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class EventHandlerTest {

    // Actual Unicode prefix from Wynncraft system messages (from latest.log analysis)
    // The messages start with special PUA characters: 󏿼󏿿󏿾
    private static final String SYSTEM_PREFIX = "\uDBFF\uDFFC\uE014\uDBFF\uDFFF\uE002\uDBFF\uDFFE";

    @ParameterizedTest
    @ValueSource(
            strings = {
                // System messages with totem placement (should detect)
                "\uDBFF\uDFFC\uE014\uDBFF\uDFFF\uE002\uDBFF\uDFFE NotFAZuH has placed a mob totem in Essren's Hut at 577, 62, -1558",
                "\uDBFF\uDFFC\uE014\uDBFF\uDFFF\uE002\uDBFF\uDFFE NotFAZuH has placed a mob totem in Essren's Hut at 578,",
                "\uDBFF\uDFFC\uE014\uDBFF\uDFFF\uE002\uDBFF\uDFFE NotFAZuH has placed a mob totem in Essren's Hut",
            })
    void shouldDetectMobTotemPlacement(String message) {
        assertTrue(EventHandler.isMobTotemPlacement(message), "Should detect totem placement: " + message);
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                // Player chat messages (should NOT detect - contains ": ")
                "NotFAZuH: has placed a mob totem in",
                "NotFAZuH: placed a mob totem in Essren's Hut",
                "NotFAZuH: hey guys has placed a mob totem in",
                "Player: I just has placed a mob totem in my house",
                // Messages without totem keywords
                "\uDBFF\uDFFC\uE014\uDBFF\uDFFF\uE002\uDBFF\uDFFE NotFAZuH's Mob Totem has run out Get your own mob",
                "\uDBFF\uDFFC\uE014\uDBFF\uDFFF\uE002\uDBFF\uDFFE You are inside of NotFAZuH's mob totem. Get your own mob",
                "\uDBFF\uDFFC\uE014\uDBFF\uDFFF\uE002\uDBFF\uDFFE The Necromantic Site World Event starts in 3m 59s!",
                "\uDBFF\uDFFC\uE014\uDBFF\uDFFF\uE002\uDBFF\uDFFE Amaru48x has thrown a Combat Experience Bomb on EU7",
                "Amaru48x has thrown a Combat Experience Bomb on EU7",
                "Wynntils Crowd Sourcing",
                "Wynntils Telemetry",
                "§6[§aMKT §ev3§6] §r§fFound a totem, started recording...",
                "§6[§aMKT §ev3§6] §r§eA totem is already in progress, ignoring this one...",
            })
    void shouldNotDetectNonTotemMessages(String message) {
        assertFalse(EventHandler.isMobTotemPlacement(message), "Should NOT detect totem placement: " + message);
    }

    @Test
    void shouldNotDetectPlayerChatWithTotemKeywords() {
        // Player chat messages have ": " after the player name
        // This is the key differentiator from system messages
        assertFalse(EventHandler.isMobTotemPlacement("NotFAZuH: has placed a mob totem in"));
        assertFalse(EventHandler.isMobTotemPlacement("NotFAZuH: placed a mob totem in Essren's Hut"));
        assertFalse(EventHandler.isMobTotemPlacement("Player: I just has placed a mob totem in my house"));
        assertFalse(EventHandler.isMobTotemPlacement("NotFAZuH: Check out my mob totem in Detlas"));
    }

    @Test
    void shouldDetectSystemTotemMessageWithoutColonSpace() {
        // System messages don't have ": " pattern - they start with Unicode prefix
        // and directly have the player name followed by text
        assertTrue(
                EventHandler.isMobTotemPlacement(
                        "\uDBFF\uDFFC\uE014\uDBFF\uDFFF\uE002\uDBFF\uDFFE NotFAZuH has placed a mob totem in Essren's Hut at 577, 62, -1558"));
        assertTrue(
                EventHandler.isMobTotemPlacement(
                        "\uDBFF\uDFFC\uE014\uDBFF\uDFFF\uE002\uDBFF\uDFFE NotFAZuH has placed a mob totem in Essren's Hut at 578,"));
    }

    @Test
    void shouldNotDetectMobTotemRunOut() {
        // These messages don't contain "has placed"
        assertFalse(EventHandler.isMobTotemPlacement(
                "\uDBFF\uDFFC\uE014\uDBFF\uDFFF\uE002\uDBFF\uDFFE NotFAZuH's Mob Totem has run out Get your own mob"));
        assertFalse(EventHandler.isMobTotemPlacement("NotFAZuH's Mob Totem has run out"));
    }

    @Test
    void shouldNotDetectInsideMobTotemMessage() {
        // These messages don't contain "has placed"
        assertFalse(
                EventHandler.isMobTotemPlacement(
                        "\uDBFF\uDFFC\uE014\uDBFF\uDFFF\uE002\uDBFF\uDFFE You are inside of NotFAZuH's mob totem. Get your own mob"));
        assertFalse(EventHandler.isMobTotemPlacement("You are inside of Player's mob totem."));
    }
}
