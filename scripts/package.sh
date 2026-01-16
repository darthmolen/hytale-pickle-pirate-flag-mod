#!/bin/bash
# Package script for Pickle Pirate Flag mod
# Creates a distributable zip with Pack + Plugin + docs
# Usage: ./scripts/package.sh

set -e  # Exit on error

echo "==================================="
echo "Packaging Pickle Pirate Flag Mod"
echo "==================================="

# Navigate to project root
cd "$(dirname "$0")/.."

# Build plugin first
if [ -f "./scripts/build.sh" ]; then
    ./scripts/build.sh
fi

# Create dist folder
DIST_FOLDER="dist"
rm -rf "$DIST_FOLDER"
mkdir -p "$DIST_FOLDER/PicklePirateFlag"

echo ""
echo "Packaging mod files..."

# Copy Pack
echo "  - Pack (assets)"
cp -r pack "$DIST_FOLDER/PicklePirateFlag/Pack"

# Copy Plugin JAR
if [ -f "build/libs/PicklePirateFlag-1.0.0.jar" ]; then
    echo "  - Plugin (Java)"
    mkdir -p "$DIST_FOLDER/PicklePirateFlag/Plugin"
    cp build/libs/PicklePirateFlag-1.0.0.jar "$DIST_FOLDER/PicklePirateFlag/Plugin/"
fi

# Copy documentation
echo "  - Documentation"
cp -r documentation "$DIST_FOLDER/PicklePirateFlag/Documentation"

# Create install instructions
cat > "$DIST_FOLDER/PicklePirateFlag/README.txt" << 'EOF'
Pickle Pirate Flag Mod
======================

A plantable flag that waves in the wind and shows on the map!

INSTALLATION
------------

1. Pack (required for assets):
   Copy the "Pack" folder to:
   %AppData%/Roaming/Hytale/UserData/Packs/PicklePirateFlag

2. Plugin (required for map markers):
   Copy PicklePirateFlag-1.0.0.jar to:
   %AppData%/Roaming/Hytale/UserData/Mods/

3. Launch Hytale and enable the pack in Creation Tools

CONTENTS
--------
- Pack/           - Block, item, textures, model definitions
- Plugin/         - Java plugin for map marker functionality
- Documentation/  - Full modding guides and how-it-was-made

FEATURES
--------
- Plantable flagpole with pickle pirate design
- Flag waves in the wind
- Shows as marker on the world map
- Discovery system - find flags to reveal them on map

Enjoy! Arr!
EOF

# Create zip
echo ""
echo "Creating zip archive..."
cd "$DIST_FOLDER"
zip -r "PicklePirateFlag-1.0.0.zip" "PicklePirateFlag"
cd ..

# Calculate size
SIZE=$(du -h "$DIST_FOLDER/PicklePirateFlag-1.0.0.zip" | cut -f1)

echo ""
echo "==================================="
echo "Package complete!"
echo ""
echo "Output: dist/PicklePirateFlag-1.0.0.zip ($SIZE)"
echo ""
echo "Contents:"
echo "  - Pack/           (block definitions, textures, model)"
echo "  - Plugin/         (Java plugin JAR)"
echo "  - Documentation/  (guides and tutorials)"
echo "  - README.txt      (install instructions)"
echo "==================================="
