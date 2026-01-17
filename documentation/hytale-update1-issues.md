# Hytale Server v2026.01.17 Update - Breaking Changes & Fixes

This document catalogs all the issues encountered when updating from the initial Hytale server release to v2026.01.17.

## Summary

The first server update introduced numerous breaking changes that required significant debugging and workarounds. Many changes were undocumented and required trial-and-error to resolve.

---

## Issue 1: HytaleAssets Missing manifest.json

**Error:**
```
Failed to load any asset packs
Skipping pack at HytaleAssets: missing or invalid manifest.json
```

**Cause:** The official HytaleAssets folder shipped without a manifest.json file.

**Fix:** Create `/mnt/c/hytale-server/HytaleAssets/manifest.json`:
```json
{
  "Group": "Hytale",
  "Name": "HytaleAssets",
  "Version": "2026.01.17"
}
```

---

## Issue 2: HytaleAssets Must Be in mods/ Folder

**Error:**
```
Skipping pack at HytaleAssets: missing or invalid manifest.json
```

**Cause:** Even with manifest.json created, the server only loads packs from the `mods/` directory, not from the root HytaleAssets folder.

**Fix (Linux/WSL):** Create symlink:
```bash
ln -s /mnt/c/hytale-server/HytaleAssets /mnt/c/hytale-server/mods/HytaleAssets
```

**Fix (Windows):** Linux symlinks don't work with Windows Java. Create a Windows junction instead:
```cmd
mklink /J C:\hytale-server\mods\HytaleAssets C:\hytale-server\HytaleAssets
```

---

## Issue 3: Cosmetics Files Now Required

**Error:**
```
NoSuchFileException: .../Cosmetics/CharacterCreator/Emotes.json
NoSuchFileException: .../Cosmetics/CharacterCreator/EyeColors.json
NoSuchFileException: .../Cosmetics/CharacterCreator/HairColors.json
NoSuchFileException: .../Cosmetics/CharacterCreator/SkinColors.json
```

**Cause:** Every mod pack now requires a complete set of Cosmetics/CharacterCreator files.

**Fix:** Copy all files from HytaleAssets to your mod pack:
```bash
cp -r /mnt/c/hytale-server/HytaleAssets/Cosmetics /mnt/c/hytale-server/mods/YourMod/
```

Required files:
- Cosmetics/CharacterCreator/Emotes.json
- Cosmetics/CharacterCreator/EyeColors.json
- Cosmetics/CharacterCreator/HairColors.json
- Cosmetics/CharacterCreator/SkinColors.json
- (and others in CharacterCreator/)

---

## Issue 4: World Config Files Required

**Error:**
```
NoSuchFileException: .../Server/World/World.json
```

**Cause:** Mod packs now require Server/World configuration files.

**Fix:** Copy from HytaleAssets:
```bash
cp -r /mnt/c/hytale-server/HytaleAssets/Server/World /mnt/c/hytale-server/mods/YourMod/Server/
```

---

## Issue 5: Model Path Restrictions

**Error:**
```
Common Asset 'Models/pickle_pirate_flagpole.blockymodel' must be within the root: [Blocks/, Items/, Resources/, NPC/, VFX/, Consumable/]
```

**Cause:** Models can no longer be placed in a generic `Models/` folder. They must be in specific categorized folders.

**Fix:** Move model files to appropriate folders:
```bash
# Before: Common/Models/my_model.blockymodel
# After:  Common/Blocks/my_model.blockymodel
```

Update item JSON references:
```json
"CustomModel": "Blocks/my_model.blockymodel"
```

---

## Issue 6: Asset Key Naming Convention (PascalCase)

**Warning:**
```
Asset key 'pickle_flag' has incorrect format! Expected: 'Pickle_Flag'
```

**Cause:** Asset keys (derived from filenames) must now use PascalCase with underscores.

**Fix:** Rename item files:
```bash
mv pickle_flag.json Pickle_Flag.json
```

---

## Issue 7: Deprecated BlockType Fields

**Errors:**
```
Asset 'Cloth' of type BlockParticleSet doesn't exist
Asset 'Wood_Old' of type BlockSoundSet doesn't exist
```

**Cause:** Several BlockType fields were deprecated/removed.

**Deprecated fields to remove:**
- `BlockSoundSetId`
- `BlockParticleSetId`
- `PlayerAnimationsId`
- `Gathering`
- `Set`
- `Group`

---

## Issue 8: HitboxType Changes

**Error:**
```
Asset 'Block_Torch' of type BlockBoundingBoxes doesn't exist
```

**Cause:** Some HitboxType values were removed.

**Fix:** Replace deprecated hitbox types:
```json
// Before
"HitboxType": "Block_Torch"

// After
"HitboxType": "Block_Flat"
```

---

## Issue 9: World Data Incompatibility

**Error:**
```
World default already exists on disk!
```

**Cause:** Old world data format is incompatible with the new server version.

**Fix:** Delete the old world data:
```bash
rm -rf /mnt/c/hytale-server/universe/worlds
```

The server will regenerate a fresh world on next start.

---

## Issue 10: Config.json Format Changed

**Observation:** The server now uses a completely different config.json schema.

**Old format:**
```json
{
  "server-port": 25565,
  "online-mode": true,
  "difficulty": "normal",
  ...
}
```

**New format:**
```json
{
  "Version": 3,
  "ServerName": "Hytale Server",
  "MaxPlayers": 100,
  "Defaults": {
    "World": "default",
    "GameMode": "Adventure"
  },
  ...
}
```

The server will auto-migrate or regenerate the config on first run.

---

## Issue 11: Server Authentication

**Error:**
```
Server session token not available - cannot request auth grant
```

**Cause:** Server requires authentication tokens to validate client connections.

**Fix:** Run `/auth login` in the server console after startup to authenticate with Hytale services.

**Alternative:** Start with `--allow-op` flag for development:
```batch
java -jar Server\HytaleServer.jar --allow-op
```

---

## Recommended start-server.bat

```batch
@echo off
cd /d "%~dp0"
java -jar Server\HytaleServer.jar --allow-op
pause
```

---

## Pre-Update Checklist for Future Updates

1. **Backup everything** before updating
2. Set up git on hytale-server directory for easy rollback:
   ```bash
   cd /mnt/c/hytale-server
   git init
   echo "Assets.zip\nlogs/\nuniverse/\nmods/\npacks/" > .gitignore
   git add -A
   git commit -m "Pre-update snapshot"
   ```
3. Check release notes (if any exist)
4. Test on a copy of the server first
5. Be prepared to delete universe/worlds for world format changes

---

## Lessons Learned

1. **Symlinks don't work cross-platform** - Use Windows junctions for Windows Java
2. **Every pack needs cosmetics** - Server won't start without them
3. **Model paths are now restricted** - Must use Blocks/, Items/, etc.
4. **File naming matters** - Use PascalCase with underscores
5. **Old configs get overwritten** - Back up before updating
6. **World data may be incompatible** - Be ready to regenerate
