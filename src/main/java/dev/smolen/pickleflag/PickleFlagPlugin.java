/*
 * Pickle Pirate Flag Mod for Hytale
 * A plantable flag that waves in the wind and shows as a map marker
 */
package dev.smolen.pickleflag;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.ISystem;
import com.hypixel.hytale.component.system.RefSystem;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.server.core.asset.type.blocktick.BlockTickStrategy;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockComponentChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.ChunkColumn;
import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
import com.hypixel.hytale.server.core.universe.world.chunk.section.ChunkSection;
import com.hypixel.hytale.server.core.universe.world.events.AddWorldEvent;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
// import com.hypixel.hytale.server.core.modules.block.interaction.BlockInteractionRegistry; // API doesn't exist

import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

/**
 * Main plugin class for the Pickle Pirate Flag mod.
 *
 * This plugin adds a plantable flagpole with a pickle pirate flag that:
 * - Waves in the wind (animation)
 * - Can be placed and picked up like a torch
 * - Shows as a marker on the map
 *
 * Architecture follows the ECS (Entity Component System) pattern used by Hytale:
 * - PickleFlagBlock: Component attached to placed flag blocks
 * - PlayerFlagData: Component tracking which flags a player has discovered
 * - FlagManager: Central manager for all placed flags (POI data)
 * - PickleFlagMarkerProvider: Provides map markers for placed flags
 */
public class PickleFlagPlugin extends JavaPlugin {

    // Singleton instance for global access
    private static PickleFlagPlugin instance;

    // Logger for debug output
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    // Manager for tracking all placed flags
    private FlagManager flagManager;

    // Component type for flag blocks (attached to ChunkStore)
    private ComponentType<ChunkStore, PickleFlagBlock> flagBlockComponent;

    // Component type for player flag data (attached to EntityStore)
    private ComponentType<EntityStore, PlayerFlagData> playerFlagDataComponent;

    /**
     * Constructor called by Hytale when loading the plugin.
     *
     * @param init Initialization data provided by the server
     */
    public PickleFlagPlugin(@Nonnull JavaPluginInit init) {
        super(init);

        // Log startup message
        LOGGER.atInfo().log("Pickle Pirate Flag v" + this.getManifest().getVersion().toString() + " loading...");

        // Initialize the flag manager
        this.flagManager = new FlagManager();
    }

    /**
     * Setup method called after construction to register all components and systems.
     * This is where we wire up the ECS architecture.
     */
    @Override
    protected void setup() {
        instance = this;
        LOGGER.atInfo().log("Setting up Pickle Pirate Flag plugin...");

        // Register player data component (tracks discovered flags per player)
        this.playerFlagDataComponent = this.getEntityStoreRegistry()
            .registerComponent(PlayerFlagData.class, "PlayerFlagData", PlayerFlagData.CODEC);

        // Register flag block component (data attached to placed flags)
        this.flagBlockComponent = this.getChunkStoreRegistry()
            .registerComponent(PickleFlagBlock.class, "PickleFlagBlockData", PickleFlagBlock.CODEC);

        // Register system to handle flag placement/removal
        this.getChunkStoreRegistry().registerSystem((ISystem) new OnFlagPlaced());

        // Register ticking system for flag animation and discovery checks
        this.getChunkStoreRegistry().registerSystem((ISystem) new FlagTicking());

        // Register system to initialize player data when they join
        this.getEntityStoreRegistry().registerSystem((ISystem) new PlayerJoinedSystem());

        // Register map marker provider when worlds are added
        this.getEventRegistry().registerGlobal(AddWorldEvent.class, event -> {
            event.getWorld().getWorldMapManager().getMarkerProviders()
                .put("pickle_flag_plugin", new PickleFlagMarkerProvider());
        });

        // Register commands for flag management
        this.getCommandRegistry().registerCommand(new PickleFlagCommand());

        LOGGER.atInfo().log("Pickle Pirate Flag plugin setup complete!");
    }

    // ==================== Getters ====================

    public ComponentType<ChunkStore, PickleFlagBlock> getFlagBlockComponent() {
        return this.flagBlockComponent;
    }

    public ComponentType<EntityStore, PlayerFlagData> getPlayerFlagDataComponent() {
        return this.playerFlagDataComponent;
    }

    public FlagManager getFlagManager() {
        return this.flagManager;
    }

    public static PickleFlagPlugin get() {
        return instance;
    }

    // ==================== Inner System Classes ====================

    /**
     * System that handles flag block placement and removal.
     * Triggered when a PickleFlagBlock component is added to or removed from a chunk.
     */
    public static class OnFlagPlaced extends RefSystem<ChunkStore> {

        private final Query<ChunkStore> QUERY = Query.and(
            BlockModule.BlockStateInfo.getComponentType(),
            PickleFlagPlugin.get().getFlagBlockComponent()
        );

        @Override
        public void onEntityAdded(@NonNullDecl Ref<ChunkStore> ref,
                                  @NonNullDecl AddReason reason,
                                  @NonNullDecl Store<ChunkStore> store,
                                  @NonNullDecl CommandBuffer<ChunkStore> commandBuffer) {

            PickleFlagBlock flagBlock = commandBuffer.getComponent(ref,
                PickleFlagPlugin.get().getFlagBlockComponent());

            if (flagBlock != null) {
                // Get world time for scheduling ticks
                WorldTimeResource timeResource = ((ChunkStore) commandBuffer.getExternalData())
                    .getWorld().getEntityStore().getStore().getResource(WorldTimeResource.getResourceType());

                // Get block position info
                BlockModule.BlockStateInfo blockInfo = commandBuffer.getComponent(ref,
                    BlockModule.BlockStateInfo.getComponentType());
                assert blockInfo != null;

                int localX = ChunkUtil.xFromBlockInColumn(blockInfo.getIndex());
                int localY = ChunkUtil.yFromBlockInColumn(blockInfo.getIndex());
                int localZ = ChunkUtil.zFromBlockInColumn(blockInfo.getIndex());

                // Schedule the flag for ticking (animation updates)
                BlockChunk blockChunk = commandBuffer.getComponent(blockInfo.getChunkRef(),
                    BlockChunk.getComponentType());
                BlockSection blockSection = blockChunk.getSectionAtBlockY(localY);
                blockSection.scheduleTick(
                    ChunkUtil.indexBlock(localX, localY, localZ),
                    flagBlock.getNextScheduledTick(timeResource)
                );

                // Calculate world coordinates
                ChunkColumn chunkColumn = commandBuffer.getComponent(blockInfo.getChunkRef(),
                    ChunkColumn.getComponentType());
                assert chunkColumn != null;

                Ref sectionRef = chunkColumn.getSection(ChunkUtil.chunkCoordinate(localY));
                assert sectionRef != null;

                ChunkSection chunkSection = commandBuffer.getComponent(sectionRef,
                    ChunkSection.getComponentType());

                int worldX = ChunkUtil.worldCoordFromLocalCoord(chunkSection.getX(), localX);
                int worldY = ChunkUtil.worldCoordFromLocalCoord(chunkSection.getY(), localY);
                int worldZ = ChunkUtil.worldCoordFromLocalCoord(chunkSection.getZ(), localZ);

                // Register the flag with the manager
                PickleFlagPlugin.get().getFlagManager().addFlag(
                    flagBlock.getFlagUniqueId(),
                    flagBlock.getFlagName(),
                    worldX, worldY, worldZ
                );

                LOGGER.atInfo().log("Pickle flag planted at " + worldX + ", " + worldY + ", " + worldZ +
                    " ID: " + flagBlock.getFlagUniqueId());
            }
        }

        @Override
        public void onEntityRemove(@NonNullDecl Ref<ChunkStore> ref,
                                   @NonNullDecl RemoveReason reason,
                                   @NonNullDecl Store<ChunkStore> store,
                                   @NonNullDecl CommandBuffer<ChunkStore> commandBuffer) {

            // Don't process chunk unloads as removals
            if (reason == RemoveReason.UNLOAD) {
                return;
            }

            PickleFlagBlock flagBlock = commandBuffer.getComponent(ref,
                PickleFlagPlugin.get().getFlagBlockComponent());

            if (flagBlock != null) {
                // Get block position for logging
                BlockModule.BlockStateInfo blockInfo = commandBuffer.getComponent(ref,
                    BlockModule.BlockStateInfo.getComponentType());
                assert blockInfo != null;

                int localX = ChunkUtil.xFromBlockInColumn(blockInfo.getIndex());
                int localY = ChunkUtil.yFromBlockInColumn(blockInfo.getIndex());
                int localZ = ChunkUtil.zFromBlockInColumn(blockInfo.getIndex());

                ChunkColumn chunkColumn = commandBuffer.getComponent(blockInfo.getChunkRef(),
                    ChunkColumn.getComponentType());
                assert chunkColumn != null;

                Ref sectionRef = chunkColumn.getSection(ChunkUtil.chunkCoordinate(localY));
                assert sectionRef != null;

                ChunkSection chunkSection = commandBuffer.getComponent(sectionRef,
                    ChunkSection.getComponentType());

                int worldX = ChunkUtil.worldCoordFromLocalCoord(chunkSection.getX(), localX);
                int worldY = ChunkUtil.worldCoordFromLocalCoord(chunkSection.getY(), localY);
                int worldZ = ChunkUtil.worldCoordFromLocalCoord(chunkSection.getZ(), localZ);

                // Update name from manager (may have been renamed)
                FlagManager.FlagData flagData = PickleFlagPlugin.get().getFlagManager()
                    .getFlag(flagBlock.getFlagUniqueId());
                if (flagData != null) {
                    flagBlock.setFlagName(flagData.name());
                }

                // Remove from manager
                PickleFlagPlugin.get().getFlagManager().removeFlag(flagBlock.getFlagUniqueId());

                LOGGER.atInfo().log("Pickle flag removed at " + worldX + ", " + worldY + ", " + worldZ +
                    " ID: " + flagBlock.getFlagUniqueId());
            }
        }

        @NullableDecl
        @Override
        public Query<ChunkStore> getQuery() {
            return this.QUERY;
        }
    }

    /**
     * System that handles per-tick updates for flags.
     * Updates animation phase and checks for player discovery.
     */
    public static class FlagTicking extends EntityTickingSystem<ChunkStore> {

        private static final Query<ChunkStore> QUERY = Query.and(
            BlockSection.getComponentType(),
            ChunkSection.getComponentType()
        );

        @Override
        public void tick(float dt, int index, @Nonnull ArchetypeChunk<ChunkStore> archetypeChunk,
                        @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer) {

            BlockSection blockSection = archetypeChunk.getComponent(index, BlockSection.getComponentType());
            assert blockSection != null;

            if (blockSection.getTickingBlocksCountCopy() == 0) {
                return;
            }

            ChunkSection chunkSection = archetypeChunk.getComponent(index, ChunkSection.getComponentType());
            assert chunkSection != null;

            BlockComponentChunk blockComponentChunk = commandBuffer.getComponent(
                chunkSection.getChunkColumnReference(), BlockComponentChunk.getComponentType());
            assert blockComponentChunk != null;

            WorldTimeResource timeResource = ((ChunkStore) commandBuffer.getExternalData())
                .getWorld().getEntityStore().getStore().getResource(WorldTimeResource.getResourceType());

            // Process each ticking block
            blockSection.forEachTicking(blockComponentChunk, commandBuffer, chunkSection.getY(),
                (blockCompChunk, cmdBuffer, localX, localY, localZ, blockId) -> {

                    Ref blockRef = blockCompChunk.getEntityReference(
                        ChunkUtil.indexBlockInColumn(localX, localY, localZ));

                    if (blockRef == null) {
                        return BlockTickStrategy.IGNORED;
                    }

                    PickleFlagBlock flagBlock = commandBuffer.getComponent(blockRef,
                        PickleFlagPlugin.get().getFlagBlockComponent());

                    if (flagBlock != null) {
                        // Calculate world position
                        int worldX = ChunkUtil.worldCoordFromLocalCoord(chunkSection.getX(), localX);
                        int worldY = ChunkUtil.worldCoordFromLocalCoord(chunkSection.getY(), localY);
                        int worldZ = ChunkUtil.worldCoordFromLocalCoord(chunkSection.getZ(), localZ);

                        // Call tick handler
                        flagBlock.onTick(worldX, worldY, worldZ,
                            ((ChunkStore) commandBuffer.getExternalData()).getWorld());

                        // Schedule next tick
                        var nextTick = flagBlock.getNextScheduledTick(timeResource);
                        if (nextTick != null) {
                            blockSection.scheduleTick(
                                ChunkUtil.indexBlock(localX, localY, localZ), nextTick);
                        }

                        return BlockTickStrategy.SLEEP;
                    }

                    return BlockTickStrategy.IGNORED;
                });
        }

        @NullableDecl
        @Override
        public Query<ChunkStore> getQuery() {
            return QUERY;
        }
    }

    /**
     * System that initializes player flag data when they join.
     */
    public static class PlayerJoinedSystem extends RefSystem<EntityStore> {

        @Nonnull
        @Override
        public Query<EntityStore> getQuery() {
            return Player.getComponentType();
        }

        @Override
        public void onEntityAdded(@Nonnull Ref<EntityStore> ref,
                                  @Nonnull AddReason reason,
                                  @Nonnull Store<EntityStore> store,
                                  @Nonnull CommandBuffer<EntityStore> commandBuffer) {

            Player player = store.getComponent(ref, Player.getComponentType());
            assert player != null;

            player.getWorld().execute(() -> {
                // Create player flag data if it doesn't exist
                PlayerFlagData playerData = store.getComponent(ref, PlayerFlagData.getComponentType());
                if (playerData == null) {
                    playerData = new PlayerFlagData();
                    store.addComponent(ref, PlayerFlagData.getComponentType(), playerData);
                }

                // Register with manager
                PickleFlagPlugin.get().getFlagManager()
                    .setPlayerFlagData(player.getUuid(), playerData);
            });
        }

        @Override
        public void onEntityRemove(@Nonnull Ref<EntityStore> ref,
                                   @Nonnull RemoveReason reason,
                                   @Nonnull Store<EntityStore> store,
                                   @Nonnull CommandBuffer<EntityStore> commandBuffer) {

            Player player = store.getComponent(ref, Player.getComponentType());
            assert player != null;

            PickleFlagPlugin.get().getFlagManager().removePlayerFlagData(player.getUuid());
        }
    }
}
