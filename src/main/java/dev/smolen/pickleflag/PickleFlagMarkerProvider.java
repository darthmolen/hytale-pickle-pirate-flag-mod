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

    // Icon for discovered flags
    private static final String DISCOVERED_ICON = "PickleFlag_Marker.png";

    // Icon for undiscovered flags
    private static final String UNDISCOVERED_ICON = "PickleFlag_Marker_Undiscovered.png";

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

        // Get player's discovery data
        PlayerFlagData playerFlagData = PickleFlagPlugin.get().getFlagManager()
            .getPlayerFlagData(worldMapTracker.getPlayer().getUuid());

        // Iterate through all placed flags
        for (FlagManager.FlagData flag : PickleFlagPlugin.get().getFlagManager().getAllFlags()) {

            // Check if player has discovered this flag
            boolean discovered = playerFlagData != null &&
                playerFlagData.hasDiscoveredFlag(flag.id());

            // Create unique marker ID
            String markerId = (discovered ? "Discovered" : "Undiscovered") + "PickleFlag-" + flag.id();

            // Determine display name
            String displayName = discovered ?
                "Pickle Flag - " + flag.name() :
                "Unknown Pickle Flag";

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
                    discovered ? DISCOVERED_ICON : UNDISCOVERED_ICON,
                    PositionUtil.toTransformPacket(new Transform(
                        flagData.x(), flagData.y(), flagData.z())),
                    createContextMenuItems(flagData, discovered, worldMapTracker)
                )
            );
        }
    }

    /**
     * Create context menu items for a flag marker (right-click menu).
     *
     * @param flag            The flag data
     * @param discovered      Whether the player has discovered this flag
     * @param worldMapTracker The player's map tracker
     * @return Array of context menu items, or null if none
     */
    private ContextMenuItem[] createContextMenuItems(FlagManager.FlagData flag,
                                                      boolean discovered,
                                                      WorldMapTracker worldMapTracker) {

        List<ContextMenuItem> items = new ArrayList<>();

        if (discovered) {
            // Could add teleport option here if desired
            // items.add(new ContextMenuItem("Teleport", "pickleflag tp " + flag.id()));

            // Add rename option for flag owner (would need permission check)
            // items.add(new ContextMenuItem("Rename", "pickleflag rename " + flag.id()));
        }

        if (items.isEmpty()) {
            return null;
        }

        return items.toArray(new ContextMenuItem[0]);
    }
}
