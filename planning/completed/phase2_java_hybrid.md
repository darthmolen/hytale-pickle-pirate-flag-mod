# Phase 2: Java Hybrid - Map Marker with Rename

## Goal
Add Java plugin to enable right-click rename functionality while keeping asset pack for models/textures.

## Features

| Feature | Implementation |
|---------|----------------|
| Wave animation | From Phase 1 |
| Custom map marker | `WorldMapManager.MarkerProvider` |
| Right-click rename | `InteractiveCustomUIPage` |
| Custom marker icons | Discovered/undiscovered states |

## Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    Asset Pack                            │
│  - Model (.blockymodel)                                 │
│  - Texture (.png)                                       │
│  - Animation (.blockyanim)                              │
│  - UI Page (PickleFlagRename.ui)                        │
│  - Map Icons (discovered/undiscovered)                  │
└─────────────────────────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────┐
│                    Java Plugin                           │
│  - PickleFlagPlugin.java (main)                         │
│  - PickleFlagBlock.java (block component)               │
│  - PickleFlagPoiProvider.java (map markers)             │
│  - RenameUI.java (rename dialog)                        │
└─────────────────────────────────────────────────────────┘
```

## Tasks

### 1. Create Java Plugin Structure
```
src/main/java/dev/smolen/pickleflag/
├── PickleFlagPlugin.java
├── PickleFlagBlock.java
├── PickleFlagPoiProvider.java
├── RenameUI.java
└── PoiManager.java
```

### 2. Implement PickleFlagBlock Component
- Store: UUID, name
- Serialization via BuilderCodec
- No ticking needed (no discovery system yet)

### 3. Implement PoiManager
- In-memory storage of placed flags
- Add/remove/rename operations
- Track flag positions

### 4. Implement PickleFlagPoiProvider
```java
public class PickleFlagPoiProvider implements WorldMapManager.MarkerProvider {
    public void update(World world, ...) {
        for (PoiData poi : poiManager.getAllPois()) {
            worldMapTracker.trySendMarker(..., new MapMarker(
                id,
                poi.name(),
                "Pickle_Flag.png",
                transform,
                createContextMenu(poi)  // Rename option
            ));
        }
    }
}
```

### 5. Implement RenameUI
- Extend `InteractiveCustomUIPage`
- Reference `Pages/PickleFlagRename.ui`
- Handle name update via PoiManager

### 6. Create UI File
```
Common/UI/Custom/Pages/PickleFlagRename.ui
```
- Text input field for name
- Confirm/Cancel buttons

### 7. Register Everything in Plugin
```java
protected void setup() {
    // Register block component
    blockComponent = getChunkStoreRegistry().registerComponent(...);

    // Register systems
    getChunkStoreRegistry().registerSystem(new OnFlagPlaced());

    // Register map marker provider
    getEventRegistry().registerGlobal(AddWorldEvent.class, event ->
        event.getWorld().getWorldMapManager()
            .getMarkerProviders().put("pickle_flag", new PickleFlagPoiProvider())
    );
}
```

### 8. Update Item JSON
- Remove `BlockMapMarker` (handled by Java now)
- Add block interaction for rename:
```json
"BlockType": {
  "Interactions": {
    "Use": {
      "Interactions": [
        {
          "Type": "Custom",
          "Handler": "pickle_flag_rename"
        }
      ]
    }
  }
}
```

## Files

### Asset Pack
```
mods/PicklePirateFlag/
├── Common/
│   ├── UI/Custom/Pages/
│   │   └── PickleFlagRename.ui  (NEW)
│   └── UI/WorldMap/MapMarkers/
│       └── Pickle_Flag.png
└── Server/Item/Items/
    └── Pickle_Flag.json  (MODIFIED)
```

### Plugin JAR
```
mods/PicklePirateFlag.jar
├── manifest.json
└── dev/smolen/pickleflag/
    ├── PickleFlagPlugin.java
    ├── PickleFlagBlock.java
    ├── PickleFlagPoiProvider.java
    ├── RenameUI.java
    └── PoiManager.java
```

## Success Criteria

1. All Phase 1 features still work
2. Right-click flag opens rename dialog
3. Entering new name updates map marker
4. Renamed flag persists across server restart

## Dependencies

- Phase 1 complete
- Hytale Server API (HytaleServer.jar)
- Understanding of Codec serialization
