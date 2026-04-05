package com.spiritlight.mobkilltracker.v3.enums;

/**
 * Item rarities in Wynncraft.
 *
 * <p>Based on Wynncraft API v3 item data. Valid tiers are: mythic, fabled, legendary, rare, unique,
 * normal.
 */
public enum Rarity {
    // How the weight is actually determined in v3:
    // The mythic is 1000000/7 (the probability, inverted)
    // Everything else is roughly 7 times less of the higher
    // tier's weight, that is, mythic has 7 times of fableds' weight,
    // and so on, it's a rough estimation but on this scale it doesn't
    // really matter i imagine

    MYTHIC(142857),
    FABLED(20408),
    LEGENDARY(2915),
    RARE(416),
    UNIQUE(59),
    NORMAL(8),
    UNKNOWN(0);

    final int weight;

    Rarity(int weight) {
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }

    /**
     * Parses a rarity string from the Wynncraft API.
     *
     * @param tier the tier string from API (e.g., "legendary", "mythic", "normal")
     * @return the corresponding Rarity enum value, or UNKNOWN if not recognized
     */
    public static Rarity fromString(String tier) {
        if (tier == null) {
            return UNKNOWN;
        }
        return switch (tier.toLowerCase()) {
            case "mythic" -> MYTHIC;
            case "fabled" -> FABLED;
            case "legendary" -> LEGENDARY;
            case "rare" -> RARE;
            case "unique" -> UNIQUE;
            case "normal" -> NORMAL;
            default -> UNKNOWN;
        };
    }
}
