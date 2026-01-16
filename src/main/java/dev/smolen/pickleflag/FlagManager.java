/*
 * Pickle Pirate Flag Mod for Hytale
 * Central manager for tracking all placed flags
 */
package dev.smolen.pickleflag;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Central manager for tracking all placed Pickle Pirate Flags.
 *
 * This class maintains:
 * - A registry of all placed flags with their positions
 * - Player flag data references for quick lookup
 *
 * The FlagManager is the single source of truth for flag positions,
 * which is used by the map marker provider to display flags on the map.
 */
public class FlagManager {

    // Map of flag UUID -> flag data
    private final Map<String, FlagData> flagDataMap = new HashMap<>();

    // Map of player UUID -> player flag data
    private final Map<UUID, PlayerFlagData> playerDataMap = new HashMap<>();

    /**
     * Register a new flag at the given position.
     *
     * @param id   Unique identifier for the flag
     * @param name Display name for the flag
     * @param x    World X coordinate
     * @param y    World Y coordinate
     * @param z    World Z coordinate
     */
    public void addFlag(String id, String name, int x, int y, int z) {
        this.flagDataMap.put(id, new FlagData(id, name, x, y, z));
    }

    /**
     * Remove a flag from the registry.
     *
     * @param id The UUID of the flag to remove
     */
    public void removeFlag(String id) {
        this.flagDataMap.remove(id);
    }

    /**
     * Get data for a specific flag.
     *
     * @param id The UUID of the flag
     * @return The flag data, or null if not found
     */
    public FlagData getFlag(String id) {
        return this.flagDataMap.get(id);
    }

    /**
     * Get all registered flags.
     *
     * @return Collection of all flag data
     */
    public Collection<FlagData> getAllFlags() {
        return this.flagDataMap.values();
    }

    /**
     * Rename a flag.
     *
     * @param id   The UUID of the flag
     * @param name The new name
     */
    public void renameFlag(String id, String name) {
        FlagData existing = this.flagDataMap.get(id);
        if (existing != null) {
            this.flagDataMap.put(id, new FlagData(
                existing.id(), name, existing.x(), existing.y(), existing.z()));
        }
    }

    /**
     * Register a player's flag data for quick lookup.
     *
     * @param playerUuid The player's UUID
     * @param data       The player's flag data component
     */
    public void setPlayerFlagData(UUID playerUuid, PlayerFlagData data) {
        this.playerDataMap.put(playerUuid, data);
    }

    /**
     * Remove a player's flag data (when they disconnect).
     *
     * @param playerUuid The player's UUID
     */
    public void removePlayerFlagData(UUID playerUuid) {
        this.playerDataMap.remove(playerUuid);
    }

    /**
     * Get a player's flag data.
     *
     * @param playerUuid The player's UUID
     * @return The player's flag data, or null if not found
     */
    public PlayerFlagData getPlayerFlagData(UUID playerUuid) {
        return this.playerDataMap.get(playerUuid);
    }

    /**
     * Record containing data about a placed flag.
     *
     * @param id   Unique identifier
     * @param name Display name
     * @param x    World X coordinate
     * @param y    World Y coordinate
     * @param z    World Z coordinate
     */
    public record FlagData(String id, String name, int x, int y, int z) {
    }
}
