# Publishing to CurseForge

This guide covers how to publish the Pickle Pirate Flag mod to CurseForge, the official Hytale modding platform.

## Prerequisites

- CurseForge account (create at https://www.curseforge.com/login)
- Built packages (see Building Packages below)
- Logo image (`curseforge/logo.png`)

## Building Packages

The project includes gradle tasks for creating distribution packages.

### Build Commands

```bash
# Build plugin JAR only
./gradlew jar
# Output: build/libs/PicklePirateFlag-1.0.0.jar

# Create assets ZIP only
./gradlew packageAssets
# Output: build/dist/PicklePirateFlag-Assets-1.0.0.zip

# Create all packages (JAR + Assets ZIP + Combined ZIP)
./gradlew packageAll
# Outputs:
#   build/libs/PicklePirateFlag-1.0.0.jar
#   build/dist/PicklePirateFlag-Assets-1.0.0.zip
#   build/dist/PicklePirateFlag-Complete-1.0.0.zip
```

### Package Contents

| Package | Contents | Use Case |
|---------|----------|----------|
| JAR | Plugin code + manifest | Server-side plugin |
| Assets ZIP | Models, textures, items, UI | Client assets |
| Complete ZIP | JAR + Assets + README | Single download option |

## Creating a CurseForge Project

1. **Log in to CurseForge**
   - Go to https://www.curseforge.com/login
   - Sign in with Google, Discord, GitHub, or Twitch

2. **Access Author Console**
   - Click your profile → Author Console
   - Or go directly to https://authors.curseforge.com/

3. **Create New Project**
   - Navigate to Projects → Create a Project
   - Select **Hytale** as the game

4. **Fill in Project Details**

   | Field | Value |
   |-------|-------|
   | Name | Pickle Pirate Flag |
   | Summary | A plantable pickle pirate flagpole with wave animation and map marker support |
   | Description | Copy from `curseforge/description.md` |
   | Class | Mods |
   | Primary Category | Decoration |
   | Additional Categories | Items, Gameplay |
   | License | MIT (or your choice) |
   | Logo | Upload `curseforge/logo.png` |

5. **Save Project**
   - Review all fields
   - Click Create

## Uploading Files

1. **Navigate to Files Tab**
   - In your project page, click Files → Add File

2. **Upload Plugin JAR**
   - File: `build/libs/PicklePirateFlag-1.0.0.jar`
   - File type: Release
   - Game version: Select appropriate Hytale version
   - Add changelog notes

3. **Upload Assets ZIP**
   - File: `build/dist/PicklePirateFlag-Assets-1.0.0.zip`
   - Mark as required dependency of the JAR

4. **Or Upload Combined Package**
   - File: `build/dist/PicklePirateFlag-Complete-1.0.0.zip`
   - Contains everything in one download

## Project Settings

### License Options

| License | Description |
|---------|-------------|
| MIT | Permissive, allows any use with attribution |
| All Rights Reserved | No redistribution without permission |
| CC BY-NC | Non-commercial use only |
| Custom | Define your own terms |

### Visibility

- **Draft** - Only you can see it
- **Public** - Visible to everyone after approval
- **Unlisted** - Accessible via direct link only

### Categories

Recommended categories for this mod:
- Decoration
- Items
- Gameplay

## Moderation Review

After uploading:

1. Files enter the moderation queue
2. Review typically takes 24-48 hours
3. You'll receive email notification when approved
4. If rejected, you'll get feedback on required changes

### Common Rejection Reasons

- Missing or invalid manifest
- Copyrighted content without permission
- Malicious code detected
- Incomplete description

## Updating Your Mod

When releasing a new version:

1. **Update Version Numbers**
   - `build.gradle`: `version = '1.1.0'`
   - `src/main/resources/manifest.json`: `"Version": "1.1.0"`
   - `pack/manifest.json`: `"Version": "1.1.0"`

2. **Build New Packages**
   ```bash
   ./gradlew clean packageAll
   ```

3. **Upload to CurseForge**
   - Go to Files → Add File
   - Upload new version
   - Add changelog describing changes

4. **Version Numbering**
   - Use semantic versioning: MAJOR.MINOR.PATCH
   - MAJOR: Breaking changes
   - MINOR: New features, backward compatible
   - PATCH: Bug fixes

## Alternative Platforms

### Modtale

Community-run Hytale mod repository.

1. Create account at https://modtale.net
2. Click Create → Upload
3. Select content type (Plugin, Asset Pack)
4. Fill in details and upload

### GitHub Releases

For open-source distribution:

1. Push code to GitHub repository
2. Create a new Release
3. Upload build artifacts
4. Tag with version number

## Quick Reference

```bash
# Full publishing workflow
./gradlew clean packageAll    # Build packages
python scripts/create_logo.py # Regenerate logo if needed

# Output files for upload:
# - build/libs/PicklePirateFlag-1.0.0.jar
# - build/dist/PicklePirateFlag-Assets-1.0.0.zip
# - curseforge/logo.png
# - curseforge/description.md (copy content)
```

## Resources

- [CurseForge Hytale Portal](https://www.curseforge.com/hytale)
- [CurseForge Publishing Guide](https://hytalemodding.dev/en/docs/publishing/curseforge)
- [Hytale Modding Documentation](https://hytalemodding.dev/)
- [Modtale](https://hytalemodding.dev/en/docs/publishing/modtale)
