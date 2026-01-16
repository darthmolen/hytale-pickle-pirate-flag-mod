#!/bin/bash
# Deploy script for Pickle Pirate Flag mod
# Deploys both the Pack (assets) and Plugin (Java) to Hytale
# Usage: ./scripts/deploy.sh

set -e  # Exit on error

echo "==================================="
echo "Deploying Pickle Pirate Flag"
echo "==================================="

# Navigate to project root
cd "$(dirname "$0")/.."

# Detect OS and set Hytale UserData path
if [[ "$OSTYPE" == "msys" ]] || [[ "$OSTYPE" == "cygwin" ]] || [[ -n "$WSLENV" ]] || grep -qi microsoft /proc/version 2>/dev/null; then
    # Windows or WSL
    if [[ -n "$WSLENV" ]] || grep -qi microsoft /proc/version 2>/dev/null; then
        # WSL - convert to Windows path
        APPDATA_WIN=$(cmd.exe /c "echo %APPDATA%" 2>/dev/null | tr -d '\r')
        HYTALE_DATA=$(wslpath "$APPDATA_WIN")/Hytale/UserData
    else
        # Native Windows (Git Bash/MSYS)
        HYTALE_DATA="$APPDATA/Hytale/UserData"
    fi
else
    # Linux/Mac - use home directory
    HYTALE_DATA="$HOME/.hytale/UserData"
fi

PACKS_FOLDER="$HYTALE_DATA/Packs"
MODS_FOLDER="$HYTALE_DATA/Mods"

echo "Hytale UserData: $HYTALE_DATA"
echo ""

# ==================== Deploy Pack ====================
echo "--- Deploying Pack (assets) ---"

PACK_DEST="$PACKS_FOLDER/PicklePirateFlag"

# Create packs folder if needed
mkdir -p "$PACKS_FOLDER"

# Remove old pack if exists
if [ -d "$PACK_DEST" ]; then
    echo "Removing old pack..."
    rm -rf "$PACK_DEST"
fi

# Copy pack
echo "Copying pack to $PACK_DEST"
cp -r pack "$PACK_DEST"

echo "Pack deployed!"
echo ""

# ==================== Deploy Plugin ====================
echo "--- Deploying Plugin (Java) ---"

JAR_FILE="build/libs/PicklePirateFlag-1.0.0.jar"

if [ ! -f "$JAR_FILE" ]; then
    echo "Warning: JAR not found at $JAR_FILE"
    echo "Run ./scripts/build.sh first to build the plugin"
    echo "Skipping plugin deployment..."
else
    # Create mods folder if needed
    mkdir -p "$MODS_FOLDER"

    # Copy JAR
    echo "Copying $JAR_FILE to $MODS_FOLDER"
    cp "$JAR_FILE" "$MODS_FOLDER/"
    echo "Plugin deployed!"
fi

echo ""
echo "==================================="
echo "Deployment complete!"
echo ""
echo "Locations:"
echo "  Pack:   $PACK_DEST"
echo "  Plugin: $MODS_FOLDER/PicklePirateFlag-1.0.0.jar"
echo ""
echo "Next steps:"
echo "1. Launch Hytale"
echo "2. Enable the pack in Creation Tools"
echo "3. The plugin loads automatically"
echo "4. Find 'Pickle Pirate Flag' in Creative menu"
echo "==================================="
