/*
 * Pickle Pirate Flag Mod for Hytale
 * Player component for tracking discovered flags
 */
package pickleflag;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Component attached to players to track which Pickle Pirate Flags they have discovered.
 *
 * This allows for:
 * - Showing discovered vs undiscovered flags differently on the map
 * - Triggering discovery events only once per player per flag
 * - Persisting discovery state across sessions
 *
 * The data is serialized as an array of flag UUIDs (strings).
 */
public class PlayerFlagData implements Component<EntityStore> {

    /**
     * Codec for serializing/deserializing this component.
     * Stores the set of discovered flag IDs as a string array.
     */
    @Nonnull
    public static final BuilderCodec<PlayerFlagData> CODEC = BuilderCodec
        .builder(PlayerFlagData.class, PlayerFlagData::new)
        .append(
            new KeyedCodec<>("DiscoveredFlags", BuilderCodec.STRING_ARRAY),
            (data, ids) -> data.discoveredFlags = new HashSet<>(Arrays.asList(ids)),
            data -> data.discoveredFlags.toArray(new String[0])
        ).add()
        .build();

    // Set of flag UUIDs that this player has discovered
    private Set<String> discoveredFlags = new HashSet<>();

    /**
     * Default constructor (required for CODEC).
     */
    public PlayerFlagData() {
    }

    /**
     * Get the component type for PlayerFlagData.
     * Used by other parts of the plugin to access this component.
     */
    @Nonnull
    public static ComponentType<EntityStore, PlayerFlagData> getComponentType() {
        return PickleFlagPlugin.get().getPlayerFlagDataComponent();
    }

    /**
     * Check if this player has discovered a specific flag.
     *
     * @param flagId The UUID of the flag to check
     * @return true if discovered, false otherwise
     */
    public boolean hasDiscoveredFlag(@Nonnull String flagId) {
        return this.discoveredFlags.contains(flagId);
    }

    /**
     * Mark a flag as discovered by this player.
     *
     * @param flagId The UUID of the flag to mark as discovered
     */
    public void addDiscoveredFlag(@Nonnull String flagId) {
        this.discoveredFlags.add(flagId);
    }

    /**
     * Get the total number of flags this player has discovered.
     */
    public int getDiscoveredCount() {
        return this.discoveredFlags.size();
    }

    /**
     * Clone this component (required by Component interface).
     */
    @Nonnull
    @Override
    public PlayerFlagData clone() {
        PlayerFlagData copy = new PlayerFlagData();
        copy.discoveredFlags.addAll(this.discoveredFlags);
        return copy;
    }

    /**
     * Reset all discovered flags (for testing/debugging).
     */
    public void reset() {
        this.discoveredFlags.clear();
    }
}
