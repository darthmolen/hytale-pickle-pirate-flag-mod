# Pickle Pirate Flag Mod - Installation Guide

This guide walks you through installing the Pickle Pirate Flag mod on your Hytale server.

## Quick Start

If you already have a Hytale server running:

1. Download `PicklePirateFlag-1.0.0.jar`
2. Copy to your server's `mods/` folder
3. Restart the server
4. Done!

## Prerequisites

### For Server Operators
- Hytale dedicated server (version 1.0.0 or later) — see [SERVER_SETUP.md](SERVER_SETUP.md) for download instructions using `hytale-downloader`
- Write access to the server's `mods/` folder

### For Developers
- Java 25 JDK
- Gradle 9.2.0 (for building from source)

> **Note**: Players don't need to install anything! Hytale mods are server-side only.

## Installation Methods

### Method 1: Pre-built JAR (Recommended)

1. **Download the mod**
   - Get `PicklePirateFlag-1.0.0.jar` from the releases

2. **Locate your server's mods folder**
   ```
   your-hytale-server/
   └── mods/           <-- Put the JAR here
   ```

3. **Copy the JAR**
   ```bash
   cp PicklePirateFlag-1.0.0.jar ~/hytale-server/mods/
   ```

4. **Restart the server**
   ```bash
   # Stop the server if running
   # Then start it again
   ./start-server.sh
   ```

5. **Verify installation**
   Check the server logs for:
   ```
   [INFO] Pickle Pirate Flag v1.0.0 loading...
   [INFO] Setting up Pickle Pirate Flag plugin...
   [INFO] Pickle Pirate Flag plugin setup complete!
   ```

### Method 2: Build from Source

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/pickle-pirate-flag.git
   cd pickle-pirate-flag
   ```

2. **Build the mod**
   ```bash
   ./scripts/build.sh
   ```

3. **Deploy to server**
   ```bash
   ./scripts/deploy.sh ~/hytale-server
   ```

   Or manually copy:
   ```bash
   cp build/libs/PicklePirateFlag-1.0.0.jar ~/hytale-server/mods/
   ```

4. **Restart the server**

## Verification

### Server-Side
Check your server logs (`logs/latest.log`) for:
```
[INFO] Pickle Pirate Flag v1.0.0 loading...
[INFO] Setting up Pickle Pirate Flag plugin...
[INFO] Pickle Pirate Flag plugin setup complete!
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

### "Class not found" Errors

**Symptom**: `ClassNotFoundException` or `NoClassDefFoundError`

**Solutions**:
1. Ensure Java 25 is installed: `java -version`
2. Check server version compatibility
3. Rebuild from source if using custom build

### Flag Not Appearing

**Symptom**: Can't find the flag item in game

**Solutions**:
1. The flag may need a custom block definition (see below)
2. Check Creative Mode item list
3. Verify plugin loaded in logs

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

## Custom Assets

To customize textures or models:

1. Extract the JAR:
   ```bash
   unzip PicklePirateFlag-1.0.0.jar -d extracted/
   ```

2. Modify files in `extracted/assets/`

3. Repack:
   ```bash
   cd extracted
   zip -r ../PicklePirateFlag-1.0.0.jar *
   ```

See `assets/textures/README.md` and `assets/models/README.md` for texture specifications.

## Uninstalling

1. Stop the server
2. Remove the JAR:
   ```bash
   rm ~/hytale-server/mods/PicklePirateFlag-1.0.0.jar
   ```
3. Restart the server

> **Note**: Placed flags will remain in the world but won't function. To remove them, either:
> - Manually break them before uninstalling
> - Edit the world files (advanced)

## Updates

To update to a new version:

1. Stop the server
2. Backup your existing JAR (optional)
3. Replace with the new version
4. Restart the server

Most updates are backward-compatible, but check the changelog for migration notes.

## Getting Help

- **Issues**: Open an issue on GitHub
- **Discord**: Ask in the Hytale modding channels
- **Documentation**: See `HOW_IT_WAS_MADE.md` for technical details

---

## File Locations Reference

| File | Location |
|------|----------|
| Mod JAR | `server/mods/PicklePirateFlag-1.0.0.jar` |
| Server logs | `server/logs/latest.log` |
| World data | `server/universe/` |
| Config (future) | `server/config/pickleflag.json` |

---

*Happy flag planting! Arr! 🏴‍☠️🥒*
