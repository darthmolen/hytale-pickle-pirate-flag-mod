# How the Pickle Pirate Flag Mod Was Made

A step-by-step walkthrough of creating this Hytale mod, designed to teach you the fundamentals of Hytale modding.

## Overview

This document explains how we built a mod that:
- Adds a plantable flagpole block with a pickle pirate design
- Shows the flag as a marker on the world map
- Tracks which players have discovered each flag
- Supports waving animation

## Prerequisites

Before starting, you need:
- Java 25 JDK installed
- Gradle build system
- A code editor (VS Code, IntelliJ IDEA)
- Basic Java knowledge
- (Optional) Blockbench for 3D models

## Step 1: Understanding Hytale's Architecture

### Server-Side Only
Unlike Minecraft, Hytale mods run **entirely on the server**. Players don't install anything - they just connect and receive mod content automatically.

### Entity Component System (ECS)
Hytale uses ECS architecture:

```
Entity (ID) + Component (Data) + System (Logic) = Behavior
```

For our flag:
- **Entity**: The placed flag block
- **Component**: `PickleFlagBlock` (stores flag ID, name, animation state)
- **System**: `FlagTicking` (updates animation, checks for player discovery)

### Registries
Everything is registered through registries:
- `ChunkStoreRegistry` - For block components
- `EntityStoreRegistry` - For player components
- `EventRegistry` - For global events
- `CommandRegistry` - For commands

## Step 2: Project Structure

We organized the project as follows:

```
hytale/
├── src/main/java/dev/smolen/pickleflag/
│   ├── PickleFlagPlugin.java      # Main entry point
│   ├── PickleFlagBlock.java       # Block data component
│   ├── PlayerFlagData.java        # Player discovery tracking
│   ├── FlagManager.java           # Central flag registry
│   └── PickleFlagMarkerProvider.java  # Map markers
├── src/main/resources/
│   ├── manifest.json              # Plugin metadata
│   └── assets/                    # Textures and models
├── build.gradle                   # Build configuration
└── documentation/                 # You're reading this!
```

## Step 3: The Plugin Entry Point

`PickleFlagPlugin.java` is the heart of the mod. It extends `JavaPlugin`:

```java
public class PickleFlagPlugin extends JavaPlugin {

    public PickleFlagPlugin(JavaPluginInit init) {
        super(init);
        // Constructor: Called when plugin loads
    }

    @Override
    protected void setup() {
        // Setup: Register all components and systems
    }
}
```

### What we register in setup():

1. **Player data component** - Tracks discovered flags per player
```java
this.playerFlagDataComponent = this.getEntityStoreRegistry()
    .registerComponent(PlayerFlagData.class, "PlayerFlagData", PlayerFlagData.CODEC);
```

2. **Flag block component** - Data attached to placed flags
```java
this.flagBlockComponent = this.getChunkStoreRegistry()
    .registerComponent(PickleFlagBlock.class, "PickleFlagBlockData", PickleFlagBlock.CODEC);
```

3. **Systems** - Logic for flag behavior
```java
this.getChunkStoreRegistry().registerSystem(new OnFlagPlaced());   // Placement/removal
this.getChunkStoreRegistry().registerSystem(new FlagTicking());    // Animation/discovery
this.getEntityStoreRegistry().registerSystem(new PlayerJoinedSystem());  // Player init
```

4. **Map marker provider** - Shows flags on the map
```java
this.getEventRegistry().registerGlobal(AddWorldEvent.class, event -> {
    event.getWorld().getWorldMapManager().getMarkerProviders()
        .put("pickle_flag_plugin", new PickleFlagMarkerProvider());
});
```

## Step 4: Block Component (PickleFlagBlock)

The block component stores data for each placed flag:

```java
public class PickleFlagBlock implements Component<ChunkStore> {
    private UUID flagUniqueId;      // Unique identifier
    private String flagName;         // Display name
    private float animationPhase;    // Animation state (0.0-1.0)
}
```

### Serialization with Codecs

Hytale uses `BuilderCodec` for saving/loading data:

```java
public static final BuilderCodec<PickleFlagBlock> CODEC = BuilderCodec
    .builder(PickleFlagBlock.class, PickleFlagBlock::new)
    .append(
        new KeyedCodec<>("FlagUUID", Codec.UUID_BINARY),
        (block, uuid) -> block.flagUniqueId = uuid,
        block -> block.flagUniqueId
    ).add()
    // ... more fields
    .build();
```

### The onTick Method

Called periodically to update the flag:

```java
public void onTick(int x, int y, int z, World world) {
    // 1. Update animation phase
    this.animationPhase = (this.animationPhase + 0.1f) % 1.0f;

    // 2. Find nearby players
    spatialResource.getSpatialStructure().collect(
        new Vector3d(x, y, z), DETECTION_RADIUS, nearbyPlayers);

    // 3. Check for discovery
    for (Ref<?> playerRef : nearbyPlayers) {
        if (!playerFlagData.hasDiscoveredFlag(this.getFlagUniqueId())) {
            if (playerDistance <= DISCOVERY_RADIUS) {
                // Player discovered the flag!
                playerFlagData.addDiscoveredFlag(this.getFlagUniqueId());
                showDiscoveryNotification(player);
            }
        }
    }
}
```

## Step 5: Player Data Component (PlayerFlagData)

Tracks which flags each player has discovered:

```java
public class PlayerFlagData implements Component<EntityStore> {
    private Set<String> discoveredFlags = new HashSet<>();

    public boolean hasDiscoveredFlag(String flagId) {
        return this.discoveredFlags.contains(flagId);
    }

    public void addDiscoveredFlag(String flagId) {
        this.discoveredFlags.add(flagId);
    }
}
```

This is persisted to the world file via codec:

```java
public static final BuilderCodec<PlayerFlagData> CODEC = BuilderCodec
    .builder(PlayerFlagData.class, PlayerFlagData::new)
    .append(
        new KeyedCodec<>("DiscoveredFlags", BuilderCodec.STRING_ARRAY),
        (data, ids) -> data.discoveredFlags = new HashSet<>(Arrays.asList(ids)),
        data -> data.discoveredFlags.toArray(new String[0])
    ).add()
    .build();
```

## Step 6: Map Markers (PickleFlagMarkerProvider)

Shows flags on the world map:

```java
public class PickleFlagMarkerProvider implements WorldMapManager.MarkerProvider {

    @Override
    public void update(World world, GameplayConfig config,
                       WorldMapTracker tracker, ...) {

        for (FlagManager.FlagData flag : flagManager.getAllFlags()) {
            boolean discovered = playerFlagData.hasDiscoveredFlag(flag.id());

            tracker.trySendMarker(
                // position, etc.
                new MapMarker(
                    id, name,
                    discovered ? "PickleFlag_Marker.png" : "PickleFlag_Marker_Undiscovered.png",
                    position,
                    contextMenuItems
                )
            );
        }
    }
}
```

## Step 7: Systems (Event Handlers)

### OnFlagPlaced System
Handles when a flag is placed or removed:

```java
public class OnFlagPlaced extends RefSystem<ChunkStore> {

    @Override
    public void onEntityAdded(Ref<ChunkStore> ref, ...) {
        // Flag was placed
        // 1. Get position
        // 2. Schedule ticking
        // 3. Register with FlagManager
    }

    @Override
    public void onEntityRemove(Ref<ChunkStore> ref, ...) {
        // Flag was removed (picked up)
        // 1. Unregister from FlagManager
    }
}
```

### FlagTicking System
Updates all flags each tick:

```java
public class FlagTicking extends EntityTickingSystem<ChunkStore> {

    @Override
    public void tick(float dt, ...) {
        // For each chunk section with ticking blocks
        blockSection.forEachTicking((blockCompChunk, ...) -> {
            PickleFlagBlock flagBlock = getComponent(blockRef);
            if (flagBlock != null) {
                flagBlock.onTick(worldX, worldY, worldZ, world);
            }
        });
    }
}
```

## Step 8: Manifest and Build

### manifest.json
```json
{
    "Group": "dev.smolen",
    "Name": "PicklePirateFlag",
    "Version": "1.0.0",
    "Main": "dev.smolen.pickleflag.PickleFlagPlugin",
    "Description": "Plantable pickle pirate flagpole with map markers",
    "Authors": ["smolen"],
    "Dependencies": [],
    "ServerVersion": ">=1.0.0"
}
```

### build.gradle
```groovy
plugins {
    id 'java'
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

jar {
    archiveBaseName = 'PicklePirateFlag'
    from('src/main/resources') {
        include '**/*'
    }
}
```

## Step 9: Testing

1. **Build**: `./scripts/build.sh`
2. **Deploy**: `./scripts/deploy.sh ~/hytale-server`
3. **Start server** and check logs for plugin load
4. **Connect** and test:
   - Place a flag
   - Walk away and check the map
   - Walk close to discover it
   - Pick it up

## Lessons Learned

1. **Study existing mods** - The Landmark mod was invaluable
2. **ECS is different** - Think in components and systems, not objects
3. **Server-side only** - No client mods, everything streams
4. **Codecs are essential** - Learn the BuilderCodec pattern
5. **Test incrementally** - Build, deploy, test often

## Next Steps

To extend this mod, you could:
- Add flag colors/designs
- Add rename command
- Add teleport-to-flag feature
- Add territory claiming mechanics
- Add flag capture game mode

## Resources Used

- [HytaleModding.dev](https://hytalemodding.dev/en)
- [Landmark mod](https://github.com/tr7zw/Landmark) (decompiled for reference)
- [Hytale official modding docs](https://hytale.com/news/2025/11/hytale-modding-strategy-and-status)

---

*This mod was created as a learning exercise. Feel free to use it as a template for your own mods!*
