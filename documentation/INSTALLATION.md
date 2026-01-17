# Pickle Pirate Flag Mod - Installation Guide

This guide walks you through installing the Pickle Pirate Flag mod on your Hytale server.

## Quick Start

If you already have a Hytale server running:

1. Download `PicklePirateFlag-1.0.0.jar`
2. Copy to your server's `mods/` folder
3. Copy the `pack/` folder to `mods/PicklePirateFlag/`
4. Restart the server
5. Done!

## Prerequisites

### For Server Operators
- Hytale dedicated server (version 2026.01+)
- Write access to the server's `mods/` folder

### For Developers
- Java 25 JDK
- Gradle (for building from source)

> **Note**: Players don't need to install anything! Hytale mods are server-side only.

## Installation Methods

### Method 1: Pre-built JAR (Recommended)

1. **Download the mod**
   - Get `PicklePirateFlag-1.0.0.jar` from releases

2. **Locate your server's mods folder**
   ```
   hytale-server/
   └── mods/           <-- Put JAR and pack here
   ```

3. **Copy the JAR**
   ```bash
   cp PicklePirateFlag-1.0.0.jar /path/to/hytale-server/mods/
   ```

4. **Copy the asset pack**
   ```bash
   cp -r pack /path/to/hytale-server/mods/PicklePirateFlag
   ```

5. **Restart the server**

6. **Verify installation**
   Check the server logs for:
   ```
   [INFO] [PluginManager] - dev.smolen:PicklePirateFlag from path PicklePirateFlag-1.0.0.jar
   [INFO] [PickleFlagPlugin] Pickle Pirate Flag v1.0.0 loading...
   ```

### Method 2: Build from Source

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/pickle-pirate-flag.git
   cd pickle-pirate-flag
   ```

2. **Build the mod**
   ```bash
   ./gradlew jar
   ```

3. **Deploy to server**
   ```bash
   cp build/libs/PicklePirateFlag-1.0.0.jar /path/to/hytale-server/mods/
   cp -r pack /path/to/hytale-server/mods/PicklePirateFlag
   ```

4. **Restart the server**

## Verification

### Server-Side
Check your server logs in `logs/` folder (files are timestamped like `2026-01-17_12-31-16_server.log`):
```
[INFO] [PluginManager] - dev.smolen:PicklePirateFlag from path PicklePirateFlag-1.0.0.jar
[INFO] [PickleFlagPlugin] Pickle Pirate Flag v1.0.0 loading...
```

### In-Game
1. Connect to the server
2. Enter Creative Mode
3. Look for "Pickle Pirate Flag" in the items/blocks menu
4. Place a flag
5. Open the map (M key) and look for the marker

## Troubleshooting

### Mod Not Loading

**Symptom**: No log messages about Pickle Pirate Flag

**Solutions**:
1. Verify the JAR is in the correct `mods/` folder
2. Check file permissions: `ls -la mods/`
3. Ensure the server fully restarted
4. Check for error messages in logs

### "Failed to load any asset packs"

**Symptom**: Server shuts down with asset pack error

**Solutions**:
1. Ensure pack folder is in `mods/PicklePirateFlag/` (not just `packs/`)
2. Check pack manifest.json exists and is valid JSON
3. Re-extract server Assets.zip if HytaleAssets is corrupted

### Texture Not Displaying

**Symptom**: Model appears but textures are wrong/white

**Solutions**:
1. Check UV offsets are within texture bounds
2. Verify texture file exists at correct path in pack
3. Check `pickle_flag.json` references correct model and texture paths

### Map Marker Not Showing

**Symptom**: Flag placed but no marker on map

**Solutions**:
1. Walk away from the flag (markers may have minimum distance)
2. Zoom out on the map
3. Check if the map is enabled in server settings

## Configuration

Currently, the mod doesn't require configuration. Future versions may add:
- `config/pickleflag.json` for customization
- Permission nodes for admin controls

## Uninstalling

1. Stop the server
2. Remove the JAR and pack:
   ```bash
   rm /path/to/hytale-server/mods/PicklePirateFlag-1.0.0.jar
   rm -rf /path/to/hytale-server/mods/PicklePirateFlag
   ```
3. Restart the server

> **Note**: Placed flags will remain in the world but won't function.

## Updates

To update to a new version:

1. Stop the server
2. Replace JAR and pack folder with new versions
3. Restart the server

## File Locations Reference

| File | Location |
|------|----------|
| Mod JAR | `server/mods/PicklePirateFlag-1.0.0.jar` |
| Asset Pack | `server/mods/PicklePirateFlag/` |
| Server logs | `server/logs/YYYY-MM-DD_HH-MM-SS_server.log` |
| World data | `server/universe/` |

## Getting Help

- **Issues**: Open an issue on GitHub
- **Discord**: Ask in the Hytale modding channels
- **Documentation**: See `CLAUDE.md` in project root for technical details
