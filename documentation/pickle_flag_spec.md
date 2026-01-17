# Pickle Pirate Flag - Feature Specification

## Overview

A placeable flagpole with animated waving flag that serves as a player landmark on the world map. Based on the tr7zw Landmark mod architecture.

---

## Current State (What Works)

| Feature | Status | Notes |
|---------|--------|-------|
| Item exists in inventory | Working | Item ID: `PicklePirate:Pickle_Flag` |
| Can hold item | Working | Orientation: flag points LEFT when held |
| Can place block | Working | Orientation: flag points RIGHT when placed |
| Model displays | Working | 128 units tall (~2 blocks) |
| Texture displays | Working | 256x288 texture |
| Can rotate on placement | Working | `VariantRotation: NESW` |
| Can break/gather | Working | Soft gathering |

---

## Current Issues

### 1. Animation Not Playing

**Symptom**: Flag is static, no wave animation in-game.

**Root Cause**: Model exported as `.blockymodel` without embedded animations. Animations need to be exported as separate `.blockyanim` files and referenced in the item JSON.

**Fix Required**:
1. Export wave animation from Blockbench to `.blockyanim` file
2. Add `CustomModelAnimation` to item JSON:
```json
"BlockType": {
  "CustomModelAnimation": "Blocks/pickle_pirate_flagpole_wave.blockyanim",
  "Looping": true
}
```

### 2. No Map Marker

**Symptom**: Placed flag doesn't appear on world map.

**Options**:

**Option A - JSON-only (BlockMapMarker)**:
Use native `BlockEntity.Components.BlockMapMarker`:
```json
"BlockType": {
  "BlockEntity": {
    "Components": {
      "BlockMapMarker": {
        "Name": "Pickle Pirate Flag",
        "Icon": "Pickle_Flag.png"
      }
    }
  }
}
```
- Pros: No Java code needed
- Cons: Fixed name, no rename, no discovery system

**Option B - Java Plugin (like Landmark)**:
Implement `WorldMapManager.MarkerProvider`:
- Custom marker icons
- Rename via UI
- Discovery system with particles/sound
- Per-player discovered state

### 3. No Right-Click Rename

**Symptom**: Cannot rename the flag after placement.

**Requires**: Java plugin with:
- `InteractiveCustomUIPage` for rename dialog
- Custom UI file (`Pages/PickleFlagRename.ui`)
- Block interaction handler for right-click

### 4. Held Item Orientation

**Symptom**: When held, flag appears to point left. When placed, points right.

**Root Cause**: `PlayerAnimationsId: "Block"` uses a standard block holding animation that doesn't account for asymmetric models.

**Options**:
1. Accept the behavior (held is temporary)
2. Create custom `PlayerAnimationsId` (requires Java)
3. Mirror the model and adjust placement rotation

---

## Target Feature Set

### MVP (JSON-only)

1. Wave animation plays when placed
2. Static map marker (fixed name)
3. Proper model scale

### Full Version (Java Plugin)

1. Wave animation plays when placed
2. Map marker with custom icon
3. Right-click to rename
4. Discovery system (optional):
   - Title popup when first approaching
   - Particles and sound
   - Per-player discovery tracking

---

## Architecture Reference (Landmark Mod)

### Components

| Class | Purpose |
|-------|---------|
| `LandmarkPlugin` | Main plugin, registers components/systems |
| `LandmarkBlock` | Block component storing UUID, name, type |
| `PlayerLandmarkData` | Player component tracking discovered landmarks |
| `PoiManager` | In-memory POI storage |
| `LandmarkPoiProvider` | `WorldMapManager.MarkerProvider` implementation |
| `RenameUI` | `InteractiveCustomUIPage` for rename dialog |
| `LandmarkCommand` | `/landmark` command handler |

### Key APIs

```java
// Register map marker provider
world.getWorldMapManager().getMarkerProviders().put("key", provider);

// Show discovery title
EventTitleUtil.showEventTitleToPlayer(playerRef, title, subtitle, playSound);

// Spawn particles
ParticleUtil.spawnParticleEffect("Teleport", x, y, z, players, store);

// Play sound
SoundUtil.playSoundEvent2d(playerRef, soundIndex, SoundCategory.UI, store);
```

---

## File Locations

### Asset Pack
```
mods/PicklePirateFlag/
├── manifest.json
├── Common/
│   ├── Blocks/
│   │   └── pickle_pirate_flagpole.blockymodel
│   ├── BlockTextures/
│   │   └── pickle_pirate_flagpole.png
│   └── UI/WorldMap/MapMarkers/
│       └── Pickle_Flag.png (24x24 or 32x32)
└── Server/
    └── Item/Items/
        └── Pickle_Flag.json
```

### Plugin JAR (if implementing Java features)
```
mods/PicklePirateFlag.jar
├── manifest.json
└── dev/smolen/pickleflag/
    ├── PickleFlagPlugin.java
    ├── PickleFlagBlock.java
    ├── PickleFlagPoiProvider.java
    └── RenameUI.java
```

---

## Next Steps

1. **Fix Animation** - Export blockyanim, add to item JSON
2. **Test Animation** - Verify wave plays in-game
3. **Add Basic Map Marker** - JSON-only BlockMapMarker
4. **Test Map Marker** - Verify appears on world map
5. **Decide on Java features** - Rename UI, discovery system
