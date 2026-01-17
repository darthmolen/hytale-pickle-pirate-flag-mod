# Phase 1: JSON-Only MVP

## Goal
Get the flag fully functional using only JSON configuration - no Java code required.

## Features

| Feature | Implementation |
|---------|----------------|
| Wave animation | Export `.blockyanim`, reference in item JSON |
| Static map marker | `BlockEntity.Components.BlockMapMarker` |
| Proper scale | Adjust `CustomModelScale` if needed |

## Tasks

### 1. Export Wave Animation
- [ ] Open Blockbench project
- [ ] Verify wave animation exists and plays correctly
- [ ] Export as `pickle_pirate_flagpole_wave.blockyanim`
- [ ] Copy to `Common/Blocks/`

### 2. Create Map Marker Icon
- [ ] Create 32x32 PNG icon for map marker
- [ ] Save to `Common/UI/WorldMap/MapMarkers/Pickle_Flag.png`

### 3. Update Item JSON
```json
{
  "BlockType": {
    "CustomModelAnimation": "Blocks/pickle_pirate_flagpole_wave.blockyanim",
    "Looping": true,
    "BlockEntity": {
      "Components": {
        "BlockMapMarker": {
          "Name": "Pickle Pirate Flag",
          "Icon": "Pickle_Flag.png"
        }
      }
    }
  }
}
```

### 4. Test In-Game
- [ ] Place flag, verify animation plays
- [ ] Open world map, verify marker appears
- [ ] Break and replace, verify marker updates

## Files Modified

```
mods/PicklePirateFlag/
├── Common/
│   ├── Blocks/
│   │   └── pickle_pirate_flagpole_wave.blockyanim  (NEW)
│   └── UI/WorldMap/MapMarkers/
│       └── Pickle_Flag.png  (NEW)
└── Server/Item/Items/
    └── Pickle_Flag.json  (MODIFIED)
```

## Success Criteria

1. Flag waves when placed
2. Marker appears on world map at flag location
3. Marker disappears when flag is broken
4. No server errors in logs

## Limitations (Accepted for MVP)

- Fixed marker name ("Pickle Pirate Flag")
- Cannot rename after placement
- No discovery system
- Held item orientation unchanged
