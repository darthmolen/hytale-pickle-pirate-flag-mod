# Gradle Build and Deployment Process

This document describes how the Pickle Pirate Flag mod is built and deployed to the Hytale server.

## Overview

The mod consists of two parts that must be deployed separately:

1. **Java Plugin (JAR)** - Contains the server-side logic (commands, UI handlers, map markers)
2. **Asset Pack** - Contains models, textures, animations, UI templates, and item definitions

Both are deployed to `/mnt/c/hytale-server/mods/`.

## Directory Structure

```
pickle_pirate_flag_mod/
├── src/main/java/          # Java source code → compiled into JAR
├── src/main/resources/     # Plugin manifest → bundled in JAR
├── pack/                   # Asset pack → deployed as folder
│   ├── Common/             # Shared assets (models, textures)
│   ├── Server/             # Server-side assets (items, languages)
│   └── manifest.json       # Pack manifest
├── build.gradle            # Build configuration
└── build/libs/             # Built JAR output
```

## Gradle Tasks

### `./gradlew jar`
Compiles Java code and packages it into a JAR file.

- **Input**: `src/main/java/**/*.java`, `src/main/resources/**/*`
- **Output**: `build/libs/PicklePirateFlag-1.0.0.jar`

### `./gradlew deployJar`
Copies the built JAR to the server mods folder.

- **Input**: `build/libs/PicklePirateFlag-1.0.0.jar`
- **Output**: `/mnt/c/hytale-server/mods/PicklePirateFlag-1.0.0.jar`

### `./gradlew deployAssets`
Syncs the pack folder to the server. Uses `Sync` (not `Copy`) to remove deleted files.

- **Input**: `pack/**/*`
- **Output**: `/mnt/c/hytale-server/mods/PicklePirateFlag/`

### `./gradlew deploy`
**Primary deployment task.** Runs both `deployJar` and `deployAssets`.

```bash
./gradlew deploy
```

## What Gets Deployed

### JAR Contents (Java Plugin)

| Source | Purpose |
|--------|---------|
| `src/main/java/dev/smolen/pickleflag/*.java` | Plugin logic |
| `src/main/resources/manifest.json` | Plugin manifest (tells server how to load) |

The JAR contains compiled `.class` files and the manifest. It handles:
- Block component registration (`PickleFlagBlock`)
- Player data tracking (`PlayerFlagData`)
- Map marker provider (`PickleFlagMarkerProvider`)
- Rename UI (`FlagRenameUI`)
- Commands (`PickleFlagCommand`)

### Asset Pack Contents

| Source Path | Server Path | Purpose |
|-------------|-------------|---------|
| `pack/Common/Blocks/*.blockymodel` | `mods/PicklePirateFlag/Common/Blocks/` | 3D model |
| `pack/Common/Blocks/Animations/*.blockyanim` | `mods/PicklePirateFlag/Common/Blocks/Animations/` | Wave animation |
| `pack/Common/BlockTextures/*.png` | `mods/PicklePirateFlag/Common/BlockTextures/` | Model texture |
| `pack/Common/Icons/ItemsGenerated/*.png` | `mods/PicklePirateFlag/Common/Icons/ItemsGenerated/` | Inventory icon |
| `pack/Common/UI/Custom/Pages/*.ui` | `mods/PicklePirateFlag/Common/UI/Custom/Pages/` | UI templates |
| `pack/Server/Item/Items/*.json` | `mods/PicklePirateFlag/Server/Item/Items/` | Item definition |
| `pack/Server/Languages/en-US/*.lang` | `mods/PicklePirateFlag/Server/Languages/en-US/` | Translations |
| `pack/manifest.json` | `mods/PicklePirateFlag/manifest.json` | Pack manifest |

## File Relationships

```
Item JSON (Pickle_Flag.json)
    ├── References: CustomModel → Blocks/pickle_pirate_flagpole.blockymodel
    ├── References: CustomModelTexture → BlockTextures/pickle_pirate_flagpole.png
    ├── References: CustomModelAnimation → Blocks/Animations/animation.flag_wave.blockyanim
    ├── References: Icon → Icons/ItemsGenerated/pickle_flag.png
    └── References: BlockEntity.Components.PickleFlagBlockData → Java component

Java Plugin
    ├── Registers: PickleFlagBlock component (matches "PickleFlagBlockData" in JSON)
    ├── Registers: PlayerFlagData component
    ├── Registers: PickleFlagMarkerProvider (references "Pickle_Flag.png" icon)
    ├── Registers: FlagRenameUI (references "Pages/FlagRename.ui")
    └── Registers: pickleflag command
```

## Development Workflow

### Making Java Changes
```bash
# Edit Java files
vim src/main/java/dev/smolen/pickleflag/SomeClass.java

# Build and deploy
./gradlew deploy

# Restart server (Java requires restart)
```

### Making Asset Changes
```bash
# Edit model in Blockbench
# Export to pack/Common/Blocks/pickle_pirate_flagpole.blockymodel

# Deploy assets only (faster)
./gradlew deployAssets

# Restart server (most asset changes require restart)
```

### Full Rebuild
```bash
./gradlew clean deploy
```

## Important Notes

### Server Restart Required
Both Java plugin changes and most asset changes require a server restart. The server does not hot-reload:
- Java classes
- Item definitions
- Models/textures
- Animations

### Sync vs Copy
The `deployAssets` task uses Gradle's `Sync` type, which:
- Copies new and changed files
- **Deletes files** that no longer exist in the source

This prevents stale files from accumulating in the server folder.

### Component Name Matching
The Java component name must match the JSON reference exactly:

```java
// In PickleFlagPlugin.java
this.flagBlockComponent = this.getChunkStoreRegistry()
    .registerComponent(PickleFlagBlock.class, "PickleFlagBlockData", ...);
                                              ^^^^^^^^^^^^^^^^^^^
```

```json
// In Pickle_Flag.json
"BlockEntity": {
    "Components": {
        "PickleFlagBlockData": {}
         ^^^^^^^^^^^^^^^^^^^
    }
}
```

### Path References
Asset paths in JSON are relative to the pack's content type folder:
- `CustomModel: "Blocks/..."` → looks in `Common/Blocks/`
- `Texture: "BlockTextures/..."` → looks in `Common/BlockTextures/`
- `Icon: "Icons/..."` → looks in `Common/Icons/`

## Troubleshooting

### "Component not found" errors
- Check that component name in Java matches JSON exactly
- Ensure JAR was deployed (check file timestamp)
- Restart server after deploying JAR

### Model/texture not loading
- Check file paths in JSON match actual file locations
- Ensure assets were deployed (check `mods/PicklePirateFlag/` folder)
- Verify pack manifest.json exists and is valid

### Old files still present after deletion
- The `Sync` task should handle this automatically
- If issues persist, manually delete `mods/PicklePirateFlag/` and redeploy

### Gradle daemon hanging
- Stop the daemon: `./gradlew --stop`
- Run with `--no-daemon` flag: `./gradlew deploy --no-daemon`
