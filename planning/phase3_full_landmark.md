# Phase 3: Full Landmark Clone - Discovery System

## Goal
Complete feature parity with tr7zw's Landmark mod - discovery system, commands, per-player tracking.

## Features

| Feature | Implementation |
|---------|----------------|
| All Phase 2 features | Inherited |
| Discovery system | Title popup, particles, sound |
| Per-player tracking | `PlayerLandmarkData` component |
| Undiscovered markers | Different icon on map |
| Commands | `/pickleflag tp`, `/pickleflag list` |
| Random names | Pool of pirate-themed names |

## Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    Components                            │
├─────────────────────────────────────────────────────────┤
│  PickleFlagBlock (ChunkStore)                           │
│  - UUID, name, placedBy                                 │
│  - Ticks every 30s to check nearby players              │
│                                                         │
│  PlayerFlagData (EntityStore)                           │
│  - Set<UUID> discoveredFlags                            │
│  - Persists with player data                            │
└─────────────────────────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────┐
│                    Systems                               │
├─────────────────────────────────────────────────────────┤
│  OnFlagPlaced - Register POI when flag placed           │
│  OnFlagRemoved - Unregister POI when broken             │
│  FlagTicking - Check for nearby undiscovered players    │
│  PlayerSpawned - Load player discovery data             │
│  PlayerLeft - Save player discovery data                │
└─────────────────────────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────┐
│                    Discovery Flow                        │
├─────────────────────────────────────────────────────────┤
│  1. Flag ticks every 30 seconds                         │
│  2. Query players within 75 blocks                      │
│  3. For each player within 5 blocks:                    │
│     - Check if already discovered                       │
│     - If not: trigger discovery event                   │
│  4. Discovery event:                                    │
│     - Add to player's discovered set                    │
│     - Show title: "Flag Name" / "Landmark discovered"   │
│     - Spawn particles at flag                           │
│     - Play discovery sound                              │
│  5. Map marker updates to "discovered" icon             │
└─────────────────────────────────────────────────────────┘
```

## Tasks

### 1. Add PlayerFlagData Component
```java
public class PlayerFlagData implements Component<EntityStore> {
    private Set<UUID> discoveredFlags = new HashSet<>();

    public boolean hasDiscovered(UUID flagId) {
        return discoveredFlags.contains(flagId);
    }

    public void addDiscovered(UUID flagId) {
        discoveredFlags.add(flagId);
    }
}
```

### 2. Add Ticking System to PickleFlagBlock
```java
public void onTick(int x, int y, int z, World world) {
    // Get nearby players (75 block radius)
    SpatialResource spatial = store.getResource(EntityModule.get().getPlayerSpatialResourceType());
    spatial.collect(new Vector3d(x, y, z), 75.0, players);

    for (Ref playerRef : players) {
        PlayerFlagData data = store.getComponent(playerRef, PlayerFlagData.getComponentType());
        if (data.hasDiscovered(this.uuid)) continue;

        // Check if within 5 blocks
        Vector3d pos = transform.getPosition();
        if (pos.distanceSquaredTo(x, y, z) <= 25.0) {
            triggerDiscovery(playerRef, data);
        }
    }
}

private void triggerDiscovery(Ref playerRef, PlayerFlagData data) {
    data.addDiscovered(this.uuid);

    // Show title
    EventTitleUtil.showEventTitleToPlayer(
        playerRef,
        Message.raw(this.name),
        Message.raw("Pickle Flag discovered"),
        true
    );

    // Particles
    ParticleUtil.spawnParticleEffect("Teleport", x, y, z, players, store);

    // Sound
    int soundId = SoundEvent.getAssetMap().getIndex("SFX_Discovery_Z1_Medium");
    SoundUtil.playSoundEvent2d(playerRef, soundId, SoundCategory.UI, store);
}
```

### 3. Update Map Marker Provider
```java
public void update(...) {
    PlayerFlagData playerData = poiManager.getPlayerData(player.getUuid());

    for (PoiData poi : poiManager.getAllPois()) {
        boolean discovered = playerData.hasDiscovered(poi.id());

        String icon = discovered ? "Pickle_Flag.png" : "Pickle_Flag_Undiscovered.png";
        String name = discovered ? poi.name() : "Undiscovered Flag";

        worldMapTracker.trySendMarker(..., new MapMarker(
            id, name, icon, transform,
            discovered ? createContextMenu(poi) : null
        ));
    }
}
```

### 4. Add Commands
```java
public class PickleFlagCommand extends AbstractCommand {
    // /pickleflag tp <id> - Teleport to flag (if discovered)
    // /pickleflag list - List discovered flags
    // /pickleflag rename <id> <name> - Rename flag (admin)
}
```

### 5. Add Random Name Generator
```java
private static final String[] PIRATE_NAMES = {
    "Blackbeard's Rest",
    "Davy Jones' Marker",
    "The Salty Dog Post",
    "Kraken's Watch",
    "Treasure Point",
    "Scallywag's Landing",
    "Captain's Claim",
    "Buccaneer's Banner",
    "Sea Dog Station",
    "Plunder Point"
};
```

### 6. Create Undiscovered Icon
- Create `Pickle_Flag_Undiscovered.png` (greyed out or question mark)

### 7. Persist Player Data
- Register `PlayerFlagData` with EntityStore
- Data saves/loads with player automatically via Codec

## Files

### New Assets
```
Common/UI/WorldMap/MapMarkers/
├── Pickle_Flag.png
└── Pickle_Flag_Undiscovered.png
```

### Updated Plugin
```
dev/smolen/pickleflag/
├── PickleFlagPlugin.java  (MODIFIED)
├── PickleFlagBlock.java   (MODIFIED - add ticking)
├── PickleFlagPoiProvider.java  (MODIFIED - discovery state)
├── PlayerFlagData.java    (NEW)
├── PickleFlagCommand.java (NEW)
├── RenameUI.java
└── PoiManager.java        (MODIFIED - player data)
```

## Success Criteria

1. All Phase 2 features still work
2. Approaching a flag for first time triggers discovery
3. Title, particles, and sound play on discovery
4. Undiscovered flags show different icon on map
5. Discovery state persists across sessions
6. Commands work for teleport and listing

## Future Enhancements (Phase 4+)

- Teleport to discovered flags
- Flag sharing between players
- Flag categories/colors
- Integration with teams/factions
- Flag placement limits per player
