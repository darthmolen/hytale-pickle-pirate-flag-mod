#!/bin/bash
# Build script for Pickle Pirate Flag mod
# Usage: ./scripts/build.sh

set -e  # Exit on error

echo "==================================="
echo "Building Pickle Pirate Flag Mod"
echo "==================================="

# Navigate to project root
cd "$(dirname "$0")/.."

# Check for Gradle wrapper, use system Gradle if not present
if [ -f "./gradlew" ]; then
    GRADLE="./gradlew"
else
    GRADLE="gradle"
fi

# Clean and build
echo ""
echo "Running clean build..."
$GRADLE clean build

# Check if build succeeded
if [ -f "build/libs/PicklePirateFlag-1.0.0.jar" ]; then
    echo ""
    echo "==================================="
    echo "Build successful!"
    echo "Output: build/libs/PicklePirateFlag-1.0.0.jar"
    echo "==================================="
else
    echo ""
    echo "Build failed - JAR not found"
    exit 1
fi
