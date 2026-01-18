/*
 * Pickle Pirate Flag Mod for Hytale
 * Block component for placed flags
 */
package pickleflag;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;

import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * Component attached to placed Pickle Pirate Flag blocks.
 *
 * This component stores:
 * - Unique identifier for this flag instance
 * - Display name (for map marker)
 * - Animation phase for the waving flag effect
 *
 * The component is serialized using Hytale's BuilderCodec system,
 * which handles saving/loading to the world file.
 */
public class PickleFlagBlock implements Component<ChunkStore> {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    // Discovery radius (blocks) - how close player needs to be to discover the flag
    private static final double DISCOVERY_RADIUS_SQUARED = 25.0; // 5 blocks

    // Detection radius (blocks) - how far to check for players during tick
    private static final double DETECTION_RADIUS = 50.0;

    // Tick interval in seconds
    private static final long TICK_INTERVAL_SECONDS = 5;

    /**
     * Codec for serializing/deserializing this component.
     * Defines how each field is saved to and loaded from storage.
     */
    public static final BuilderCodec<PickleFlagBlock> CODEC = BuilderCodec
        .builder(PickleFlagBlock.class, PickleFlagBlock::new)
        // Flag UUID - unique identifier for this flag instance
        .append(
            new KeyedCodec<>("FlagUUID", Codec.UUID_BINARY),
            (block, uuid) -> block.flagUniqueId = uuid,
            block -> block.flagUniqueId
        ).add()
        // Flag name - display name shown on map
        .append(
            new KeyedCodec<>("FlagName", Codec.STRING),
            (block, name) -> block.flagName = name,
            block -> block.flagName
        ).add()
        // Animation phase - current position in wave animation (0.0 to 1.0)
        .append(
            new KeyedCodec<>("AnimPhase", Codec.FLOAT, true),
            (block, phase) -> block.animationPhase = phase,
            block -> block.animationPhase
        ).add()
        .build();

    // Unique identifier for this flag
    private UUID flagUniqueId;

    // Display name for the flag (shown on map)
    private String flagName;

    // Current animation phase (0.0 to 1.0)
    private float animationPhase = 0.0f;

    /**
     * Default constructor (required for CODEC).
     */
    public PickleFlagBlock() {
    }

    /**
     * Called periodically to update the flag state.
     *
     * @param x World X coordinate
     * @param y World Y coordinate
     * @param z World Z coordinate
     * @param world The world this flag is in
     */
    public void onTick(int x, int y, int z, World world) {
        // Update animation phase (wraps around 0.0 to 1.0)
        this.animationPhase = (this.animationPhase + 0.1f) % 1.0f;

        // Sync name from manager (may have been updated)
        FlagManager.FlagData flagData = PickleFlagPlugin.get().getFlagManager()
            .getFlag(this.getFlagUniqueId());
        if (flagData != null) {
            this.flagName = flagData.name();
        }

        // TODO: Player discovery feature disabled due to generic type issues
        // The Store/SpatialResource API uses complex generics that need proper typing.
        // For now, flags will appear on the map but won't trigger proximity notifications.
        //
        // Future implementation should:
        // 1. Get EntityStore properly typed: Store<EntityStore> entityStore = world.getEntityStore().getStore();
        // 2. Use correct generic types for SpatialResource and Ref<EntityStore>
        // 3. Query nearby players and check discovery status
    }

    /**
     * Get the unique identifier for this flag.
     * Generates a new UUID if one doesn't exist yet.
     */
    public String getFlagUniqueId() {
        if (this.flagUniqueId == null) {
            this.flagUniqueId = UUID.randomUUID();
        }
        return this.flagUniqueId.toString();
    }

    /**
     * Get the display name for this flag.
     */
    public String getFlagName() {
        if (this.flagName == null) {
            this.flagName = "Pickle Pirate Flag";
        }
        return this.flagName;
    }

    /**
     * Set the display name for this flag.
     */
    public void setFlagName(String name) {
        this.flagName = name;
    }

    /**
     * Get the current animation phase (0.0 to 1.0).
     */
    public float getAnimationPhase() {
        return this.animationPhase;
    }

    /**
     * Calculate when this block should next be ticked.
     *
     * @param timeResource Current world time
     * @return Instant when next tick should occur
     */
    @NullableDecl
    public Instant getNextScheduledTick(WorldTimeResource timeResource) {
        Instant currentTime = timeResource.getGameTime();
        return currentTime.plus(TICK_INTERVAL_SECONDS, ChronoUnit.SECONDS);
    }

    /**
     * Clone this component (required by Component interface).
     */
    @NullableDecl
    @Override
    public Component<ChunkStore> clone() {
        PickleFlagBlock clone = new PickleFlagBlock();
        clone.flagUniqueId = this.flagUniqueId;
        clone.flagName = this.flagName;
        clone.animationPhase = this.animationPhase;
        return clone;
    }
}
