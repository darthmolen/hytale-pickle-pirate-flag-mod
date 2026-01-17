/*
 * Pickle Pirate Flag Mod for Hytale
 * Map marker provider for showing flags on the world map
 */
package dev.smolen.pickleflag;

import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.packets.worldmap.ContextMenuItem;
import com.hypixel.hytale.protocol.packets.worldmap.MapMarker;
import com.hypixel.hytale.server.core.asset.type.gameplay.GameplayConfig;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.WorldMapTracker;
import com.hypixel.hytale.server.core.universe.world.worldmap.WorldMapManager;
import com.hypixel.hytale.server.core.util.PositionUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides map markers for all placed Pickle Pirate Flags.
 *
 * This class implements the WorldMapManager.MarkerProvider interface,
 * which is called by the game to get markers to display on the world map.
 *
 * Markers show:
 * - Discovered flags with full name and pickle_flag_marker.png icon
 * - Undiscovered flags with "Unknown Flag" and grayed-out icon
 */
public class PickleFlagMarkerProvider implements WorldMapManager.MarkerProvider {

    // Map marker icon (matches Phase 1 asset)
    private static final String DISCOVERED_ICON = "Pickle_Flag.png";

    /**
     * Called by the game to update map markers for a player.
     *
     * @param world                  The world being viewed
     * @param gameplayConfig         Gameplay configuration
     * @param worldMapTracker        Tracker for the player viewing the map
     * @param chunkViewRadiusSquared View radius squared
     * @param playerChunkX           Player's chunk X coordinate
     * @param playerChunkZ           Player's chunk Z coordinate
     */
    @Override
    public void update(World world, GameplayConfig gameplayConfig,
                       WorldMapTracker worldMapTracker,
                       int chunkViewRadiusSquared,
                       int playerChunkX, int playerChunkZ) {

        // Iterate through all placed flags
        for (FlagManager.FlagData flag : PickleFlagPlugin.get().getFlagManager().getAllFlags()) {

            // TODO: Discovery system disabled until proximity detection is implemented
            // All flags shown as discovered for now
            String markerId = "PickleFlag-" + flag.id();
            String displayName = flag.name();

            // Send marker to map
            worldMapTracker.trySendMarker(
                chunkViewRadiusSquared,
                playerChunkX,
                playerChunkZ,
                new Vector3d(flag.x(), flag.y(), flag.z()),
                0.0f,  // No rotation
                markerId,
                displayName,
                flag,
                (id, name, flagData) -> new MapMarker(
                    id,
                    name,
                    DISCOVERED_ICON,
                    PositionUtil.toTransformPacket(new Transform(
                        flagData.x(), flagData.y(), flagData.z())),
                    createContextMenuItems(flagData, worldMapTracker)
                )
            );
        }
    }

    /**
     * Create context menu items for a flag marker (right-click menu).
     *
     * @param flag            The flag data
     * @param worldMapTracker The player's map tracker
     * @return Array of context menu items, or null if none
     */
    private ContextMenuItem[] createContextMenuItems(FlagManager.FlagData flag,
                                                      WorldMapTracker worldMapTracker) {

        List<ContextMenuItem> items = new ArrayList<>();

        // Add rename option - opens the rename UI via command
        items.add(new ContextMenuItem("Rename", "pickleflag manage " + flag.id()));

        if (items.isEmpty()) {
            return null;
        }

        return items.toArray(new ContextMenuItem[0]);
    }
}
